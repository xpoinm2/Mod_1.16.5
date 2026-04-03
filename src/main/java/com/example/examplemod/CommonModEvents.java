package com.example.examplemod;

import com.example.examplemod.item.HotRoastedOreItem;
import com.example.examplemod.item.MetalChunkItem;
import com.example.examplemod.item.RoastedOreItem;
import com.example.examplemod.item.SpongeMetalItem;
import com.example.examplemod.item.WetItemData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class CommonModEvents {
    private static final Map<World, Map<BlockPos, Long>> WET_PLACED_BLOCKS = new HashMap<>();
    private static final int BLOCK_WET_DURATION_TICKS = 20 * 45;
    private static final int WORLD_WET_PROCESS_INTERVAL_TICKS = 5;
    private static final int WATER_SCAN_INTERVAL_TICKS = 100;
    private static final int WATER_SCAN_HORIZONTAL_RADIUS = 4;
    private static final int WATER_SCAN_VERTICAL_RADIUS = 3;
    private static final int RAIN_SCAN_INTERVAL_TICKS = 60;
    private static final int RAIN_SAMPLES_PER_PLAYER = 20;
    private static final int RAIN_SCAN_RADIUS = 24;
    private static final List<BlockPos> WATER_TOUCH_OFFSETS = createWaterTouchOffsets();

    private CommonModEvents() {
    }

    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        // Временно убрали регистрацию генерации мира для диагностики
        event.enqueueWork(() -> {
            // WorldGenRegistry.register();
            // ModBiomes.setupBiomes();
        });
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.player.level.isClientSide) return;

        PlayerEntity player = event.player;

        // Проверяем горячие предметы в курсоре и инвентаре,
        // чтобы урон шёл даже при перетаскивании.
        if (applyHotItemDamage(player, player.inventory.getCarried())) {
            return;
        }

        for (int i = 0; i < player.inventory.getContainerSize(); i++) {
            ItemStack stack = player.inventory.getItem(i);
            if (applyHotItemDamage(player, stack)) {
                break; // Наносим урон только один раз за тик, даже если несколько горячих предметов
            }
        }

        applyWetState(player);
        applyLowHealthStarvationDamage(player);
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (event.getWorld().isClientSide) {
            return;
        }

        PlayerEntity player = event.getPlayer();
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) {
            return;
        }

        double reach = player.getAttribute(ForgeMod.REACH_DISTANCE.get()) != null
                ? player.getAttributeValue(ForgeMod.REACH_DISTANCE.get())
                : 5.0D;

        RayTraceResult rayTraceResult = player.pick(reach, 0.0F, true);
        if (rayTraceResult.getType() != RayTraceResult.Type.BLOCK) {
            return;
        }

        BlockPos pos = ((BlockRayTraceResult) rayTraceResult).getBlockPos();
        if (!event.getWorld().getFluidState(pos).is(FluidTags.WATER)) {
            return;
        }

        WetItemData.markSoaked(stack);
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getWorld().isClientSide) {
            return;
        }

        ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) {
            return;
        }

        if (event.getWorld().getFluidState(event.getPos()).is(FluidTags.WATER)) {
            WetItemData.markSoaked(stack);
        }
    }

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (event.getPlayer().getMainHandItem().isEmpty() && event.getState().is(BlockTags.LOGS)) {
            event.setNewSpeed(0.0F);
        }
    }

    @SubscribeEvent
    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        if (event.getWorld().isClientSide()) {
            return;
        }
        if (!(event.getWorld() instanceof ServerWorld)) {
            return;
        }
        if (!(event.getEntity() instanceof PlayerEntity)) {
            return;
        }

        PlayerEntity player = (PlayerEntity) event.getEntity();
        BlockState placedState = event.getPlacedBlock();
        Block placedBlock = placedState.getBlock();
        long gameTime = player.level.getGameTime();

        for (Hand hand : Hand.values()) {
            ItemStack stackInHand = player.getItemInHand(hand);
            if (stackInHand.isEmpty() || !(stackInHand.getItem() instanceof BlockItem)) {
                continue;
            }

            BlockItem blockItem = (BlockItem) stackInHand.getItem();
            if (blockItem.getBlock() != placedBlock) {
                continue;
            }

            if (WetItemData.isWet(stackInHand, gameTime)) {
                long wetUntil = WetItemData.isSoaked(stackInHand)
                        ? gameTime + WetItemData.DEFAULT_WET_DURATION_TICKS
                        : WetItemData.getWetUntil(stackInHand);
                markPlacedBlockWet((ServerWorld) event.getWorld(), event.getPos(), wetUntil);
            }
            return;
        }
    }

    @SubscribeEvent
    public static void onFluidPlaced(BlockEvent.FluidPlaceBlockEvent event) {
        if (event.getWorld().isClientSide()) {
            return;
        }
        if (!(event.getWorld() instanceof ServerWorld)) {
            return;
        }

        ServerWorld world = (ServerWorld) event.getWorld();
        BlockPos waterPos = event.getPos();
        if (!world.getFluidState(waterPos).is(FluidTags.WATER) && !event.getNewState().getFluidState().is(FluidTags.WATER)) {
            return;
        }

        long wetUntil = world.getGameTime() + BLOCK_WET_DURATION_TICKS;
        for (BlockPos offset : WATER_TOUCH_OFFSETS) {
            BlockPos targetPos = waterPos.offset(offset);
            if (world.isEmptyBlock(targetPos)) {
                continue;
            }
            markPlacedBlockWet(world, targetPos, wetUntil);
        }
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.world.isClientSide()) {
            return;
        }
        if (!(event.world instanceof ServerWorld)) {
            return;
        }

        ServerWorld world = (ServerWorld) event.world;
        long gameTime = world.getGameTime();
        if (gameTime % WATER_SCAN_INTERVAL_TICKS == 0L) {
            applyNearbyWaterWetState(world, gameTime);
        }
        if (gameTime % RAIN_SCAN_INTERVAL_TICKS == 0L) {
            applyRainWetState(world, gameTime);
        }

        Map<BlockPos, Long> wetBlocks = WET_PLACED_BLOCKS.get(world);
        if (wetBlocks == null || wetBlocks.isEmpty()) {
            return;
        }

        if (gameTime % WORLD_WET_PROCESS_INTERVAL_TICKS != 0L) {
            return;
        }

        Iterator<Map.Entry<BlockPos, Long>> iterator = wetBlocks.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<BlockPos, Long> entry = iterator.next();
            BlockPos pos = entry.getKey();
            long wetUntil = entry.getValue();

            if (wetUntil <= gameTime || world.isEmptyBlock(pos)) {
                iterator.remove();
                continue;
            }

            if (world.random.nextFloat() < 0.65F) {
                spawnWetFaceParticles(world, pos);
            }
        }

        if (wetBlocks.isEmpty()) {
            WET_PLACED_BLOCKS.remove(world);
        }
    }

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload event) {
        if (!(event.getWorld() instanceof ServerWorld)) {
            return;
        }
        WET_PLACED_BLOCKS.remove((ServerWorld) event.getWorld());
    }

    @SubscribeEvent
    public static void onServerStopping(FMLServerStoppingEvent event) {
        WET_PLACED_BLOCKS.clear();
    }

    private static void applyWetState(PlayerEntity player) {
        long gameTime = player.level.getGameTime();

        boolean inWetArea = player.isInWaterRainOrBubble() || player.isInWater();

        WetItemData.updateWetState(player.getMainHandItem(), gameTime, inWetArea);
        WetItemData.updateWetState(player.getOffhandItem(), gameTime, inWetArea);

        for (int i = 0; i < player.inventory.getContainerSize(); i++) {
            WetItemData.updateWetState(player.inventory.getItem(i), gameTime, inWetArea);
        }

        WetItemData.updateWetState(player.inventory.getCarried(), gameTime, inWetArea);
    }

    private static void applyLowHealthStarvationDamage(PlayerEntity player) {
        if (player.isCreative() || player.isSpectator()) {
            return;
        }
        if (player.level.getDifficulty() == Difficulty.PEACEFUL) {
            return;
        }
        if (player.getFoodData().getFoodLevel() > 0) {
            return;
        }

        // В обычном Minecraft на NORMAL starvation-урон останавливается на 5 сердцах (10 HP).
        // Доприменяем starvation-урон ниже этого порога, чтобы голод продолжал наносить урон.
        if (player.getHealth() <= 10.0F && player.tickCount % 80 == 0) {
            player.hurt(DamageSource.STARVE, 1.0F);
        }
    }

    private static void mergeInventoryStacksByWetState(PlayerEntity player, long gameTime) {
        int size = player.inventory.getContainerSize();
        for (int i = 0; i < size; i++) {
            ItemStack target = player.inventory.getItem(i);
            if (target.isEmpty()) {
                continue;
            }

            int maxStack = target.getMaxStackSize();
            if (target.getCount() >= maxStack) {
                continue;
            }

            for (int j = i + 1; j < size; j++) {
                ItemStack source = player.inventory.getItem(j);
                if (source.isEmpty()) {
                    continue;
                }

                if (!WetItemData.canMergeIgnoringWetness(target, source)) {
                    continue;
                }

                long wetUntil = Math.max(WetItemData.getWetUntil(target), WetItemData.getWetUntil(source));

                int transferable = Math.min(maxStack - target.getCount(), source.getCount());
                if (transferable <= 0) {
                    continue;
                }

                target.grow(transferable);
                source.shrink(transferable);

                if (wetUntil > gameTime) {
                    WetItemData.setWetUntil(target, wetUntil);
                } else {
                    WetItemData.clearWet(target);
                }

                if (source.isEmpty()) {
                    player.inventory.setItem(j, ItemStack.EMPTY);
                }

                if (target.getCount() >= maxStack) {
                    break;
                }
            }
        }
    }

    private static boolean applyHotItemDamage(PlayerEntity player, ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        if (stack.getItem() instanceof SpongeMetalItem) {
            if (SpongeMetalItem.getState(stack) == SpongeMetalItem.STATE_HOT) {
                player.hurt(DamageSource.HOT_FLOOR, 1.0F);
                return true;
            }
            return false;
        }
        if (stack.getItem() instanceof RoastedOreItem) {
            if (RoastedOreItem.getState(stack) == RoastedOreItem.STATE_HOT) {
                player.hurt(DamageSource.ON_FIRE, 2.0F);
                return true;
            }
            return false;
        }
        if (stack.getItem() instanceof HotRoastedOreItem) {
            if (HotRoastedOreItem.getState(stack) == HotRoastedOreItem.STATE_HOT) {
                player.hurt(DamageSource.ON_FIRE, 2.0F);
                return true;
            }
            return false;
        }
        if (stack.getItem() instanceof MetalChunkItem) {
            if (MetalChunkItem.getTemperature(stack) == MetalChunkItem.TEMP_HOT) {
                player.hurt(DamageSource.HOT_FLOOR, 1.0F);
                return true;
            }
            return false;
        }
        return false;
    }

    private static void markPlacedBlockWet(ServerWorld world, BlockPos pos, long wetUntil) {
        Map<BlockPos, Long> wetBlocks = WET_PLACED_BLOCKS.computeIfAbsent(world, ignored -> new HashMap<>());
        wetBlocks.put(pos.immutable(), wetUntil);
        spawnWetFaceParticles(world, pos);
    }

    private static void markPlacedBlockWetIfDry(ServerWorld world, BlockPos pos, long wetUntil, long gameTime) {
        Map<BlockPos, Long> wetBlocks = WET_PLACED_BLOCKS.get(world);
        if (wetBlocks != null) {
            Long currentWetUntil = wetBlocks.get(pos);
            if (currentWetUntil != null && currentWetUntil > gameTime) {
                return;
            }
        }

        markPlacedBlockWet(world, pos, wetUntil);
    }

    private static void applyNearbyWaterWetState(ServerWorld world, long gameTime) {
        if (world.players().isEmpty()) {
            return;
        }

        long wetUntil = gameTime + BLOCK_WET_DURATION_TICKS;
        for (PlayerEntity player : world.players()) {
            if (player.isSpectator()) {
                continue;
            }

            BlockPos playerPos = player.blockPosition();
            int minX = playerPos.getX() - WATER_SCAN_HORIZONTAL_RADIUS;
            int maxX = playerPos.getX() + WATER_SCAN_HORIZONTAL_RADIUS;
            int minY = Math.max(0, playerPos.getY() - WATER_SCAN_VERTICAL_RADIUS);
            int maxY = Math.min(world.getMaxBuildHeight() - 1, playerPos.getY() + WATER_SCAN_VERTICAL_RADIUS);
            int minZ = playerPos.getZ() - WATER_SCAN_HORIZONTAL_RADIUS;
            int maxZ = playerPos.getZ() + WATER_SCAN_HORIZONTAL_RADIUS;

            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        BlockPos waterPos = new BlockPos(x, y, z);
                        if (!world.getFluidState(waterPos).is(FluidTags.WATER)) {
                            continue;
                        }

                        for (BlockPos offset : WATER_TOUCH_OFFSETS) {
                            BlockPos targetPos = waterPos.offset(offset);
                            if (world.isEmptyBlock(targetPos)) {
                                continue;
                            }
                            markPlacedBlockWetIfDry(world, targetPos, wetUntil, gameTime);
                        }
                    }
                }
            }
        }
    }

    private static void applyRainWetState(ServerWorld world, long gameTime) {
        if (!world.isRaining()) {
            return;
        }

        int playerCount = world.players().size();
        if (playerCount == 0) {
            return;
        }

        long wetUntil = gameTime + BLOCK_WET_DURATION_TICKS;
        for (PlayerEntity player : world.players()) {
            if (player.isSpectator()) {
                continue;
            }

            BlockPos playerPos = player.blockPosition();
            for (int i = 0; i < RAIN_SAMPLES_PER_PLAYER; i++) {
                int x = playerPos.getX() + world.random.nextInt(RAIN_SCAN_RADIUS * 2 + 1) - RAIN_SCAN_RADIUS;
                int z = playerPos.getZ() + world.random.nextInt(RAIN_SCAN_RADIUS * 2 + 1) - RAIN_SCAN_RADIUS;
                int surfaceY = world.getHeight(Heightmap.Type.MOTION_BLOCKING, x, z);
                BlockPos rainCheckPos = new BlockPos(x, surfaceY, z);
                if (!world.isRainingAt(rainCheckPos)) {
                    continue;
                }

                BlockPos targetPos = rainCheckPos.below();
                if (world.isEmptyBlock(targetPos)) {
                    continue;
                }

                markPlacedBlockWetIfDry(world, targetPos, wetUntil, gameTime);
            }
        }
    }

    private static List<BlockPos> createWaterTouchOffsets() {
        List<BlockPos> offsets = new ArrayList<>();
        offsets.add(BlockPos.ZERO);
        offsets.add(new BlockPos(1, 0, 0));
        offsets.add(new BlockPos(-1, 0, 0));
        offsets.add(new BlockPos(0, 0, 1));
        offsets.add(new BlockPos(0, 0, -1));
        offsets.add(new BlockPos(0, 1, 0));
        offsets.add(new BlockPos(0, -1, 0));
        return offsets;
    }

    private static void spawnWetFaceParticles(ServerWorld world, BlockPos pos) {
        // Визуал мокрости для блоков перенесен на клиентский текстурный рендер.
    }
}
