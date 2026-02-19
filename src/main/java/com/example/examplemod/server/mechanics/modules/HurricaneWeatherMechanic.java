package com.example.examplemod.server.mechanics.modules;

import com.example.examplemod.server.mechanics.IMechanicModule;
import com.example.examplemod.network.HurricaneStatePacket;
import com.example.examplemod.network.ModNetworkHandler;
import com.mojang.brigadier.context.CommandContext;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class HurricaneWeatherMechanic implements IMechanicModule {
    private static final int MIN_TREES_PER_HURRICANE = 5;
    private static final int MAX_TREES_PER_HURRICANE = 10;
    private static final int MIN_HURRICANE_DURATION = 3600;
    private static final int MAX_HURRICANE_DURATION = 15600;
    private static final int CHUNK_RADIUS = 6;
    private static final int BLOCK_RADIUS = CHUNK_RADIUS * 16;
    private static final int TREE_SCAN_ATTEMPTS = 20;
    private static final int TREE_SCAN_DEPTH = 40;
    private static final int MAX_TREE_BLOCKS = 512;
    private static final int TREE_RADIUS_XZ = 8;
    private static final int TREE_RADIUS_Y = 12;

    private static final Map<ServerWorld, HurricaneState> HURRICANE_STATES = new Object2ObjectOpenHashMap<>();

    @Override
    public String id() {
        return "hurricane_weather";
    }

    @Override
    public boolean enableRegisterCommands() {
        return true;
    }

    @Override
    public boolean enableWorldTick() {
        return true;
    }

    @Override
    public void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("hurricane")
                        .requires(source -> source.hasPermission(2))
                        .executes(this::startHurricane)
                        .then(Commands.literal("start")
                                .executes(this::startHurricane))
                        .then(Commands.literal("on")
                                .executes(this::enableHurricaneWeather))
                        .then(Commands.literal("enable")
                                .executes(this::enableHurricaneWeather))
                        .then(Commands.literal("off")
                                .executes(this::disableHurricaneWeather))
                        .then(Commands.literal("disable")
                                .executes(this::disableHurricaneWeather))
                        .then(Commands.literal("stop")
                                .executes(this::stopHurricane))
        );
    }

    @Override
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (!(event.world instanceof ServerWorld)) {
            return;
        }

        ServerWorld world = (ServerWorld) event.world;
        HurricaneWeatherData data = HurricaneWeatherData.get(world);
        if (!data.isEnabled()) {
            if (data.isActive() || HURRICANE_STATES.containsKey(world)) {
                clearHurricane(world, data);
            }
            return;
        }

        HurricaneState state = HURRICANE_STATES.get(world);
        if (state == null) {
            if (data.isActive()) {
                long tick = world.getGameTime();
                if (tick >= data.getEndTick()) {
                    data.clear();
                    return;
                }
                state = HurricaneState.fromData(data);
                HURRICANE_STATES.put(world, state);
                sendHurricaneState(world, true);
            } else {
                return;
            }
        }

        long tick = world.getGameTime();
        if (tick >= state.endTick) {
            clearHurricane(world, data);
            return;
        }

        clearVanillaWeather(world);

        if (state.breaksRemaining <= 0 || tick < state.nextBreakTick) {
            return;
        }

        int destroyed = destroyTreesNearPlayers(world, state.breaksRemaining);
        if (destroyed > 0) {
            state.breaksRemaining -= destroyed;
        }
        state.scheduleNextBreak(tick);
        data.updateProgress(state.breaksRemaining, state.nextBreakTick);
    }

    private int startHurricane(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        ServerWorld world = source.getLevel();
        HurricaneWeatherData data = HurricaneWeatherData.get(world);
        if (!data.isEnabled()) {
            source.sendFailure(new StringTextComponent("Hurricane weather from mod is disabled. Use /hurricane on."));
            return 0;
        }
        Random random = world.getRandom();
        int duration = rollHurricaneDuration(random);
        HurricaneState state = new HurricaneState(world.getGameTime(), duration, random);
        HURRICANE_STATES.put(world, state);
        data.start(state.endTick, state.totalBreaks, state.breaksRemaining, state.nextBreakTick);
        clearVanillaWeather(world);
        sendHurricaneState(world, true);
        source.sendSuccess(new StringTextComponent("Hurricane started for " + duration + " ticks."), true);
        return 1;
    }

    private int enableHurricaneWeather(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        ServerWorld world = source.getLevel();
        HurricaneWeatherData data = HurricaneWeatherData.get(world);
        if (data.isEnabled()) {
            source.sendSuccess(new StringTextComponent("Hurricane weather from mod is already enabled."), false);
            return 1;
        }
        data.setEnabled(true);
        source.sendSuccess(new StringTextComponent("Hurricane weather from mod enabled."), true);
        return 1;
    }

    private int disableHurricaneWeather(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        ServerWorld world = source.getLevel();
        HurricaneWeatherData data = HurricaneWeatherData.get(world);
        data.setEnabled(false);
        if (data.isActive() || HURRICANE_STATES.containsKey(world)) {
            clearHurricane(world, data);
        }
        source.sendSuccess(new StringTextComponent("Hurricane weather from mod disabled."), true);
        return 1;
    }

    private int stopHurricane(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        ServerWorld world = source.getLevel();
        HurricaneWeatherData data = HurricaneWeatherData.get(world);
        if (!HURRICANE_STATES.containsKey(world) && !data.isActive()) {
            source.sendFailure(new StringTextComponent("No active hurricane to stop."));
            return 0;
        }
        clearHurricane(world, data);
        source.sendSuccess(new StringTextComponent("Hurricane stopped."), true);
        return 1;
    }

    private int rollHurricaneDuration(Random random) {
        return random.nextInt((MAX_HURRICANE_DURATION - MIN_HURRICANE_DURATION) + 1) + MIN_HURRICANE_DURATION;
    }

    public static boolean isHurricaneActive(ServerWorld world) {
        return HURRICANE_STATES.containsKey(world);
    }

    @Override
    public void onPlayerLogin(ServerPlayerEntity player) {
        ServerWorld world = player.getLevel();
        if (isHurricaneActive(world) || HurricaneWeatherData.get(world).isActive()) {
            sendHurricaneState(player, true);
        }
    }

    private int destroyTreesNearPlayers(ServerWorld world, int remainingBreaks) {
        List<ServerPlayerEntity> players = world.getServer().getPlayerList().getPlayers();
        if (players.isEmpty()) {
            return 0;
        }

        int destroyed = 0;
        for (ServerPlayerEntity player : players) {
            if (destroyed >= remainingBreaks) {
                break;
            }
            BlockPos origin = player.blockPosition();

            for (int attempt = 0; attempt < TREE_SCAN_ATTEMPTS; attempt++) {
                int x = origin.getX() + world.random.nextInt(BLOCK_RADIUS * 2 + 1) - BLOCK_RADIUS;
                int z = origin.getZ() + world.random.nextInt(BLOCK_RADIUS * 2 + 1) - BLOCK_RADIUS;
                int topY = world.getHeight(Heightmap.Type.MOTION_BLOCKING, x, z);
                int minY = Math.max(0, topY - TREE_SCAN_DEPTH);

                BlockPos.Mutable pos = new BlockPos.Mutable(x, topY, z);
                for (int y = topY; y >= minY; y--) {
                    pos.set(x, y, z);
                    if (world.getBlockState(pos).is(BlockTags.LOGS)) {
                        if (destroyTreeAt(world, pos.immutable())) {
                            destroyed++;
                        }
                        attempt = TREE_SCAN_ATTEMPTS;
                        break;
                    }
                }
            }
        }

        return destroyed;
    }

    private boolean destroyTreeAt(ServerWorld world, BlockPos start) {
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        ObjectOpenHashSet<BlockPos> visited = new ObjectOpenHashSet<>();
        queue.add(start);
        visited.add(start);

        int destroyed = 0;
        while (!queue.isEmpty() && destroyed < MAX_TREE_BLOCKS) {
            BlockPos current = queue.poll();
            BlockState state = world.getBlockState(current);
            if (!isTreeBlock(state)) {
                continue;
            }

            world.destroyBlock(current, true);
            destroyed++;

            for (Direction direction : Direction.values()) {
                BlockPos next = current.relative(direction);
                if (!visited.contains(next) && withinTreeBounds(start, next)) {
                    visited.add(next);
                    queue.add(next);
                }
            }
        }

        return destroyed > 0;
    }

    private boolean isTreeBlock(BlockState state) {
        return state.is(BlockTags.LOGS) || state.is(BlockTags.LEAVES);
    }

    private boolean withinTreeBounds(BlockPos start, BlockPos candidate) {
        int dx = Math.abs(candidate.getX() - start.getX());
        int dy = Math.abs(candidate.getY() - start.getY());
        int dz = Math.abs(candidate.getZ() - start.getZ());
        return dx <= TREE_RADIUS_XZ && dy <= TREE_RADIUS_Y && dz <= TREE_RADIUS_XZ;
    }

    private static class HurricaneState {
        private final long endTick;
        private final int totalBreaks;
        private int breaksRemaining;
        private long nextBreakTick;

        private HurricaneState(long startTick, int duration, Random random) {
            this.endTick = startTick + duration;
            this.totalBreaks = random.nextInt((MAX_TREES_PER_HURRICANE - MIN_TREES_PER_HURRICANE) + 1)
                    + MIN_TREES_PER_HURRICANE;
            this.breaksRemaining = totalBreaks;
            scheduleNextBreak(startTick);
        }

        private HurricaneState(long endTick, int totalBreaks, int breaksRemaining, long nextBreakTick) {
            this.endTick = endTick;
            this.totalBreaks = totalBreaks;
            this.breaksRemaining = breaksRemaining;
            this.nextBreakTick = nextBreakTick;
        }

        private static HurricaneState fromData(HurricaneWeatherData data) {
            return new HurricaneState(data.getEndTick(), data.getTotalBreaks(), data.getBreaksRemaining(),
                    data.getNextBreakTick());
        }

        private void scheduleNextBreak(long currentTick) {
            if (breaksRemaining <= 0) {
                nextBreakTick = endTick;
                return;
            }
            long remainingTicks = Math.max(1, endTick - currentTick);
            long interval = Math.max(1, remainingTicks / breaksRemaining);
            nextBreakTick = currentTick + interval;
        }
    }

    private void sendHurricaneState(ServerWorld world, boolean active) {
        for (ServerPlayerEntity player : world.getPlayers(player -> true)) {
            sendHurricaneState(player, active);
        }
    }

    private void sendHurricaneState(ServerPlayerEntity player, boolean active) {
        ModNetworkHandler.CHANNEL.sendTo(new HurricaneStatePacket(active), player.connection.connection, net.minecraftforge.fml.network.NetworkDirection.PLAY_TO_CLIENT);
    }

    private void clearHurricane(ServerWorld world, HurricaneWeatherData data) {
        sendHurricaneState(world, false);
        HURRICANE_STATES.remove(world);
        data.clear();
    }

    private void clearVanillaWeather(ServerWorld world) {
        world.setWeatherParameters(6000, 0, false, false);
    }
}
