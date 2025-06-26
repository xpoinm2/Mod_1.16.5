package com.example.examplemod.server;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.capability.PlayerStatsProvider;
import com.example.examplemod.network.ModNetworkHandler;
import com.example.examplemod.network.SyncStatsPacket;
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

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class StatsCommands {
    private static final String KEY_THIRST = "thirst";
    private static final String KEY_FATIGUE = "fatigue";
    private static final String KEY_DISEASE = "disease";

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

    private static int setThirst(CommandContext<CommandSource> ctx, int value) {
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

    private static int setFatigue(CommandContext<CommandSource> ctx, int value) {
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

    private static int setDisease(CommandContext<CommandSource> ctx, int value) {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
        setStat(player, KEY_DISEASE, value);
        player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(s -> s.setDisease(value));
        ctx.getSource().sendSuccess(new StringTextComponent("Disease set to " + value), true);
        return 1;
    }
}