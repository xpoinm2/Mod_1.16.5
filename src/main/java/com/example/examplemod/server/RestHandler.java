package com.example.examplemod.server;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.network.ModNetworkHandler;
import com.example.examplemod.network.SyncStatsPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RestHandler {
    private static final String KEY_FATIGUE = "fatigue";
    private static final String KEY_THIRST = "thirst";

    private static final int TICKS_PER_HOUR = 20 * 60; // 1 real minute

    private enum Type { SIT, LIE, SLEEP }

    private static class Info {
        Type type;
        int ticks;
        int remaining;
        ArmorStandEntity seat;
        Info(Type t) { this.type = t; }
    }

    private static final Map<UUID, Info> REST = new HashMap<>();
    private static final Map<UUID, Integer> BED_TICKS = new HashMap<>();

    // Helpers to store stats similar to ThirstHandler
    private static CompoundNBT getStatsTag(PlayerEntity player) {
        CompoundNBT root = player.getPersistentData();
        if (!root.contains(PlayerEntity.PERSISTED_NBT_TAG)) {
            root.put(PlayerEntity.PERSISTED_NBT_TAG, new CompoundNBT());
        }
        return root.getCompound(PlayerEntity.PERSISTED_NBT_TAG);
    }

    private static int getStat(PlayerEntity player, String key, int def) {
        CompoundNBT stats = getStatsTag(player);
        if (!stats.contains(key)) {
            stats.putInt(key, def);
        }
        return stats.getInt(key);
    }

    private static void setStat(PlayerEntity player, String key, int value) {
        getStatsTag(player).putInt(key, value);
    }

    private static void reduceFatigue(ServerPlayerEntity player, int amount) {
        int fatigue = Math.max(0, getStat(player, KEY_FATIGUE, 0) - amount);
        setStat(player, KEY_FATIGUE, fatigue);
        ModNetworkHandler.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                new SyncStatsPacket(getStat(player, KEY_THIRST, 40), fatigue)
        );
    }

    public static void startSitting(ServerPlayerEntity player) {
        Info info = new Info(Type.SIT);
        ArmorStandEntity seat = EntityType.ARMOR_STAND.create(player.level);
        if (seat != null) {
            seat.setInvisible(true);
            seat.setNoGravity(true);
            seat.setInvulnerable(true);
            seat.setPos(player.getX(), player.getY(), player.getZ());
            player.level.addFreshEntity(seat);
            player.startRiding(seat, false);
            info.seat = seat;
        }
        REST.put(player.getUUID(), info);

    }

    public static void startLying(ServerPlayerEntity player) {
        Info info = new Info(Type.LIE);
        REST.put(player.getUUID(), info);
        player.setForcedPose(net.minecraft.entity.Pose.SLEEPING);
    }

    public static void startSleeping(ServerPlayerEntity player, int hours) {
        if (hours < 1 || hours > 8) return;
        if (getStat(player, KEY_FATIGUE, 0) < 50) return;
        Info info = new Info(Type.SLEEP);
        info.remaining = hours * TICKS_PER_HOUR;
        REST.put(player.getUUID(), info);
        player.setForcedPose(net.minecraft.entity.Pose.SLEEPING);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayerEntity)) return;
        ServerPlayerEntity player = (ServerPlayerEntity) event.player;
        UUID id = player.getUUID();

        if (player.isSleeping()) {
            int t = BED_TICKS.getOrDefault(id, 0) + 1;
            if (t >= TICKS_PER_HOUR) {
                t -= TICKS_PER_HOUR;
                reduceFatigue(player, 10);
            }
            BED_TICKS.put(id, t);
            return;
        } else {
            BED_TICKS.remove(id);
        }

        Info info = REST.get(id);
        if (info == null) return;

        if (info.type == Type.SIT && info.seat != null) {
            if (!player.isPassenger() || player.getVehicle() != info.seat) {
                info.seat.remove();
                REST.remove(id);
                return;
            }
        } else if (info.type == Type.LIE && player.isShiftKeyDown()) {
            REST.remove(id);
            player.setForcedPose(null);
            return;
        }

        info.ticks++;
        switch (info.type) {
            case SIT:
                if (info.ticks >= TICKS_PER_HOUR / 4) {
                    info.ticks -= TICKS_PER_HOUR / 4;
                    reduceFatigue(player, 5);
                }
                break;
            case LIE:
                if (info.ticks >= TICKS_PER_HOUR / 6) {
                    info.ticks -= TICKS_PER_HOUR / 6;
                    reduceFatigue(player, 5);
                }
                break;
            case SLEEP:
                info.remaining--;
                if (info.ticks >= TICKS_PER_HOUR) {
                    info.ticks -= TICKS_PER_HOUR;
                    reduceFatigue(player, 10);
                }
                if (info.remaining <= 0) {
                    REST.remove(id);
                    player.setForcedPose(null);
                }
                break;
        }
    }
}