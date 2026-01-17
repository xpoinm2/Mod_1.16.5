package com.example.examplemod.server.mechanics.modules;

import com.example.examplemod.capability.PlayerStatsProvider;
import com.example.examplemod.network.ModNetworkHandler;
import com.example.examplemod.network.SyncAllStatsPacket;
import com.example.examplemod.server.mechanics.IMechanicModule;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.network.PacketDistributor;

public final class StatsCommandsMechanic implements IMechanicModule {
    @Override
    public String id() {
        return "stats_commands";
    }

    @Override
    public boolean enableRegisterCommands() {
        return true;
    }

    @Override
    public void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("stats")
                        .requires(cs -> cs.hasPermission(2))
                        .then(Commands.literal("thirst")
                                .then(Commands.argument("value", IntegerArgumentType.integer(0, 100))
                                        .executes(ctx -> setThirst(ctx, IntegerArgumentType.getInteger(ctx, "value")))))
                        .then(Commands.literal("fatigue")
                                .then(Commands.argument("value", IntegerArgumentType.integer(0, 100))
                                        .executes(ctx -> setFatigue(ctx, IntegerArgumentType.getInteger(ctx, "value")))))
                        .then(Commands.literal("poison")
                                .then(Commands.argument("value", IntegerArgumentType.integer(0, 100))
                                        .executes(ctx -> setPoison(ctx, IntegerArgumentType.getInteger(ctx, "value")))))
                        .then(Commands.literal("virus")
                                .then(Commands.argument("value", IntegerArgumentType.integer(0, 100))
                                        .executes(ctx -> setVirus(ctx, IntegerArgumentType.getInteger(ctx, "value")))))
                        .then(Commands.literal("cold")
                                .then(Commands.argument("value", IntegerArgumentType.integer(0, 100))
                                        .executes(ctx -> setCold(ctx, IntegerArgumentType.getInteger(ctx, "value")))))
                        .then(Commands.literal("hypothermia")
                                .then(Commands.argument("value", IntegerArgumentType.integer(0, 100))
                                        .executes(ctx -> setHypothermia(ctx, IntegerArgumentType.getInteger(ctx, "value")))))
                        .then(Commands.literal("hunger")
                                .then(Commands.argument("value", IntegerArgumentType.integer(0, 20))
                                        .executes(ctx -> setHunger(ctx, IntegerArgumentType.getInteger(ctx, "value")))))
                        .then(Commands.literal("health")
                                .then(Commands.argument("value", IntegerArgumentType.integer(0, 20))
                                        .executes(ctx -> setHealth(ctx, IntegerArgumentType.getInteger(ctx, "value")))))
                        .then(Commands.literal("reset")
                                .executes(ctx -> resetStats(ctx)))
        );
    }

    private static int setThirst(CommandContext<CommandSource> ctx, int value) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
        player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
            stats.setThirst(value);
            ModNetworkHandler.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new SyncAllStatsPacket(stats)
            );
        });
        ctx.getSource().sendSuccess(new StringTextComponent("Thirst set to " + value), true);
        return 1;
    }

    private static int setFatigue(CommandContext<CommandSource> ctx, int value) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
        player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
            stats.setFatigue(value);
            ModNetworkHandler.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new SyncAllStatsPacket(stats)
            );
        });
        ctx.getSource().sendSuccess(new StringTextComponent("Fatigue set to " + value), true);
        return 1;
    }

    private static int setPoison(CommandContext<CommandSource> ctx, int value) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
        player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
            stats.setPoison(value);
            ModNetworkHandler.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new SyncAllStatsPacket(stats)
            );
        });
        ctx.getSource().sendSuccess(new StringTextComponent("Poison set to " + value), true);
        return 1;
    }

    private static int setVirus(CommandContext<CommandSource> ctx, int value) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
        player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
            stats.setVirus(value);
            ModNetworkHandler.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new SyncAllStatsPacket(stats)
            );
        });
        ctx.getSource().sendSuccess(new StringTextComponent("Virus set to " + value), true);
        return 1;
    }

    private static int setCold(CommandContext<CommandSource> ctx, int value) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
        player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
            stats.setCold(value);
            ModNetworkHandler.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new SyncAllStatsPacket(stats)
            );
        });
        ctx.getSource().sendSuccess(new StringTextComponent("Cold set to " + value), true);
        return 1;
    }

    private static int setHypothermia(CommandContext<CommandSource> ctx, int value) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
        player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
            stats.setHypothermia(value);
            ModNetworkHandler.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new SyncAllStatsPacket(stats)
            );
        });
        ctx.getSource().sendSuccess(new StringTextComponent("Hypothermia set to " + value), true);
        return 1;
    }

    private static int setHunger(CommandContext<CommandSource> ctx, int value) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
        player.getFoodData().setFoodLevel(value);
        ctx.getSource().sendSuccess(new StringTextComponent("Hunger set to " + value), true);
        return 1;
    }

    private static int setHealth(CommandContext<CommandSource> ctx, int value) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
        float clamped = Math.min(player.getMaxHealth(), Math.max(0, value));
        player.setHealth(clamped);
        ctx.getSource().sendSuccess(new StringTextComponent("Health set to " + value), true);
        return 1;
    }

    private static int resetStats(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrException();

        // Check if player is in creative mode
        if (!player.abilities.instabuild) {
            ctx.getSource().sendFailure(new StringTextComponent("This command can only be used in Creative mode!"));
            return 0;
        }

        player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
            // Reset thirst and fatigue to 0
            stats.setThirst(0);
            stats.setFatigue(0);

            // Restore blood to 100
            stats.setBlood(100);

            // Reset all diseases to 0
            stats.setPoison(0);
            stats.setVirus(0);
            stats.setCold(0);
            stats.setHypothermia(0);

            // Send updated stats to client
            ModNetworkHandler.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new SyncAllStatsPacket(stats)
            );
        });

        ctx.getSource().sendSuccess(new StringTextComponent("All stats reset! Thirst: 0, Fatigue: 0, Blood: 100, Diseases: 0"), true);
        return 1;
    }
}


