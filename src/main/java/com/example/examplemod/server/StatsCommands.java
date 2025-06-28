package com.example.examplemod.server;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.capability.PlayerStatsProvider;
import com.example.examplemod.network.ModNetworkHandler;
import com.example.examplemod.network.SyncStatsPacket;
import com.example.examplemod.network.SyncColdPacket;
import com.example.examplemod.network.SyncHypothermiaPacket;
import com.example.examplemod.network.SyncVirusPacket;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class StatsCommands {
    private static final String KEY_THIRST = "thirst";
    private static final String KEY_FATIGUE = "fatigue";
    private static final String KEY_DISEASE = "disease";
    private static final String KEY_VIRUS = "virus";
    private static final String KEY_COLD = "cold";
    private static final String KEY_HYPOTHERMIA = "hypothermia";

    @SubscribeEvent
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

    private static CompoundNBT getStatsTag(PlayerEntity player) {
        CompoundNBT root = player.getPersistentData();
        if (!root.contains(PlayerEntity.PERSISTED_NBT_TAG)) {
            root.put(PlayerEntity.PERSISTED_NBT_TAG, new CompoundNBT());
        }
        return root.getCompound(PlayerEntity.PERSISTED_NBT_TAG);
    }

    private static void setStat(PlayerEntity player, String key, int value) {
        getStatsTag(player).putInt(key, value);
    }

    private static int getStat(PlayerEntity player, String key, int def) {
        CompoundNBT stats = getStatsTag(player);
        if (!stats.contains(key)) {
            stats.putInt(key, def);
        }
        return stats.getInt(key);
    }

    private static int setThirst(CommandContext<CommandSource> ctx, int value) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
        setStat(player, KEY_THIRST, value);
        player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(s -> s.setThirst(value));
        int fatigue = getStat(player, KEY_FATIGUE, 0);
        ModNetworkHandler.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                new SyncStatsPacket(value, fatigue)
        );
        ctx.getSource().sendSuccess(new StringTextComponent("Thirst set to " + value), true);
        return 1;
    }

    private static int setFatigue(CommandContext<CommandSource> ctx, int value) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
        setStat(player, KEY_FATIGUE, value);
        player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(s -> s.setFatigue(value));
        int thirst = getStat(player, KEY_THIRST, 40);
        ModNetworkHandler.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                new SyncStatsPacket(thirst, value)
        );
        ctx.getSource().sendSuccess(new StringTextComponent("Fatigue set to " + value), true);
        return 1;
    }

    private static int setDisease(CommandContext<CommandSource> ctx, int value) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
        setStat(player, KEY_DISEASE, value);
        player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(s -> s.setDisease(value));
        ctx.getSource().sendSuccess(new StringTextComponent("Disease set to " + value), true);
        return 1;
    }

    private static int setVirus(CommandContext<CommandSource> ctx, int value) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
        setStat(player, KEY_VIRUS, value);
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
        setStat(player, KEY_COLD, value);
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
        setStat(player, KEY_HYPOTHERMIA, value);
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