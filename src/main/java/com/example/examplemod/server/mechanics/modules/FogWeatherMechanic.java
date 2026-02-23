package com.example.examplemod.server.mechanics.modules;

import com.example.examplemod.network.FogStatePacket;
import com.example.examplemod.network.ModNetworkHandler;
import com.example.examplemod.server.mechanics.IMechanicModule;
import com.mojang.brigadier.context.CommandContext;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;

import java.util.Map;

public final class FogWeatherMechanic implements IMechanicModule {
    private static final long FOG_START_TICK = 22000L;
    private static final long FOG_END_TICK = 0L;

    private static final Map<ServerWorld, Boolean> LAST_STATES = new Object2BooleanOpenHashMap<>();

    @Override
    public String id() {
        return "fog_weather";
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
                Commands.literal("fog")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("start").executes(this::startFog))
                        .then(Commands.literal("stop").executes(this::stopFog))
                        .then(Commands.literal("on").executes(this::enableFogWeather))
                        .then(Commands.literal("enable").executes(this::enableFogWeather))
                        .then(Commands.literal("off").executes(this::disableFogWeather))
                        .then(Commands.literal("disable").executes(this::disableFogWeather))
                        .executes(this::fogStatus)
        );
    }

    @Override
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.world instanceof ServerWorld)) {
            return;
        }

        ServerWorld world = (ServerWorld) event.world;
        FogWeatherData data = FogWeatherData.get(world);
        boolean shouldBeActive = resolveFogActive(world, data);
        boolean previous = LAST_STATES.getOrDefault(world, false);
        if (previous == shouldBeActive) {
            return;
        }

        LAST_STATES.put(world, shouldBeActive);
        sendFogState(world, shouldBeActive);
    }

    @Override
    public void onPlayerLogin(ServerPlayerEntity player) {
        ServerWorld world = player.getLevel();
        FogWeatherData data = FogWeatherData.get(world);
        sendFogState(player, resolveFogActive(world, data));
    }

    @Override
    public void onPlayerLogout(ServerPlayerEntity player) {
        ServerWorld world = player.getLevel();
        if (world.getPlayers(p -> true).isEmpty()) {
            LAST_STATES.remove(world);
        }
    }

    private int fogStatus(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        ServerWorld world = source.getLevel();
        FogWeatherData data = FogWeatherData.get(world);
        boolean active = resolveFogActive(world, data);
        source.sendSuccess(new StringTextComponent("Fog status: "
                + (active ? "active" : "inactive")
                + ", schedule=" + (data.isEnabled() ? "on" : "off")
                + ", forced=" + (data.isForcedActive() ? "on" : "off")), false);
        return 1;
    }

    private int startFog(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        ServerWorld world = source.getLevel();
        FogWeatherData data = FogWeatherData.get(world);
        data.setForcedActive(true);
        updateAndBroadcast(world, data);
        source.sendSuccess(new StringTextComponent("Fog forced on."), true);
        return 1;
    }

    private int stopFog(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        ServerWorld world = source.getLevel();
        FogWeatherData data = FogWeatherData.get(world);
        data.setForcedActive(false);
        updateAndBroadcast(world, data);
        source.sendSuccess(new StringTextComponent("Fog forced mode disabled."), true);
        return 1;
    }

    private int enableFogWeather(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        ServerWorld world = source.getLevel();
        FogWeatherData data = FogWeatherData.get(world);
        data.setEnabled(true);
        updateAndBroadcast(world, data);
        source.sendSuccess(new StringTextComponent("Fog daily schedule enabled (04:00-06:00)."), true);
        return 1;
    }

    private int disableFogWeather(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        ServerWorld world = source.getLevel();
        FogWeatherData data = FogWeatherData.get(world);
        data.setEnabled(false);
        data.setForcedActive(false);
        updateAndBroadcast(world, data);
        source.sendSuccess(new StringTextComponent("Fog disabled."), true);
        return 1;
    }

    private void updateAndBroadcast(ServerWorld world, FogWeatherData data) {
        boolean active = resolveFogActive(world, data);
        LAST_STATES.put(world, active);
        sendFogState(world, active);
    }

    private boolean resolveFogActive(ServerWorld world, FogWeatherData data) {
        if (data.isForcedActive()) {
            return true;
        }
        if (!data.isEnabled()) {
            return false;
        }
        long dayTime = world.getDayTime() % 24000L;
        if (dayTime < 0L) {
            dayTime += 24000L;
        }
        if (FOG_START_TICK <= FOG_END_TICK) {
            return dayTime >= FOG_START_TICK && dayTime < FOG_END_TICK;
        }
        return dayTime >= FOG_START_TICK || dayTime < FOG_END_TICK;
    }

    private void sendFogState(ServerWorld world, boolean active) {
        for (ServerPlayerEntity player : world.getPlayers(player -> true)) {
            sendFogState(player, active);
        }
    }

    private void sendFogState(ServerPlayerEntity player, boolean active) {
        ModNetworkHandler.CHANNEL.sendTo(new FogStatePacket(active),
                player.connection.connection,
                net.minecraftforge.fml.network.NetworkDirection.PLAY_TO_CLIENT);
    }
}
