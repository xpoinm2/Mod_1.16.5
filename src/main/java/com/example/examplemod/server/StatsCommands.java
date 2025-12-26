package com.example.examplemod.server;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.capability.PlayerStatsProvider;
import com.example.examplemod.network.ModNetworkHandler;
import com.example.examplemod.network.SyncStatsPacket;
import com.example.examplemod.network.SyncColdPacket;
import com.example.examplemod.network.SyncHypothermiaPacket;
import com.example.examplemod.network.SyncVirusPacket;
import com.example.examplemod.network.SyncPoisonPacket;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class StatsCommands {
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("stats")
                        .requires(cs -> cs.hasPermission(2))
                        .then(Commands.literal("thirst")
                                .then(Commands.argument("value", IntegerArgumentType.integer(0, 100))
                                        .executes(ctx -> setThirst(ctx, IntegerArgumentType.getInteger(ctx, "value")))))
                        .then(Commands.literal("fatigue")
                                .then(Commands.argument("value", IntegerArgumentType.integer(0, 100))
                                        .executes(ctx -> setFatigue(ctx, IntegerArgumentType.getInteger(ctx, "value")))))
                        .then(Commands.literal("disease")
                                .then(Commands.argument("value", IntegerArgumentType.integer(0, 100))
                                        .executes(ctx -> setDisease(ctx, IntegerArgumentType.getInteger(ctx, "value")))))
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
        );
    }

    private static int setThirst(CommandContext<CommandSource> ctx, int value) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
        player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
            stats.setThirst(value);
            ModNetworkHandler.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new SyncStatsPacket(value, stats.getFatigue())
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
                    new SyncStatsPacket(stats.getThirst(), value)
            );
        });
        ctx.getSource().sendSuccess(new StringTextComponent("Fatigue set to " + value), true);
        return 1;
    }

    private static int setDisease(CommandContext<CommandSource> ctx, int value) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
        player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(s -> s.setDisease(value));
        ctx.getSource().sendSuccess(new StringTextComponent("Disease set to " + value), true);
        return 1;
    }

    private static int setPoison(CommandContext<CommandSource> ctx, int value) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
        player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(s -> s.setPoison(value));
        ModNetworkHandler.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                new SyncPoisonPacket(value)
        );
        ctx.getSource().sendSuccess(new StringTextComponent("Poison set to " + value), true);
        return 1;
    }

    private static int setVirus(CommandContext<CommandSource> ctx, int value) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
        player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(s -> s.setVirus(value));
        ModNetworkHandler.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                new SyncVirusPacket(value)
        );
        ctx.getSource().sendSuccess(new StringTextComponent("Virus set to " + value), true);
        return 1;
    }

    private static int setCold(CommandContext<CommandSource> ctx, int value) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
        player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(s -> s.setCold(value));
        ModNetworkHandler.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                new SyncColdPacket(value)
        );
        ctx.getSource().sendSuccess(new StringTextComponent("Cold set to " + value), true);
        return 1;
    }

    private static int setHypothermia(CommandContext<CommandSource> ctx, int value) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
        player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(s -> s.setHypothermia(value));
        ModNetworkHandler.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                new SyncHypothermiaPacket(value)
        );
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
}