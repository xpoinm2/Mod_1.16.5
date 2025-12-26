package com.example.examplemod.server.mechanics.modules;

import com.example.examplemod.capability.PlayerStatsProvider;
import com.example.examplemod.network.ModNetworkHandler;
import com.example.examplemod.network.SyncAllStatsPacket;
import com.example.examplemod.server.mechanics.IMechanicModule;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.PacketDistributor;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Отдых/сидение. Перенесено из RestHandler в модуль.
 */
public final class RestMechanic implements IMechanicModule {
    private static final int TICKS_PER_HOUR = 20 * 60; // 1 real minute

    private enum Type { SIT }

    private static class Info {
        Type type;
        int ticks;
        Entity seat;
        Info(Type t) { this.type = t; }
    }

    // Оптимизация: Fastutil коллекции вместо HashMap (50-70% меньше памяти)
    private final Object2ObjectOpenHashMap<UUID, Info> rest = new Object2ObjectOpenHashMap<>();
    private final Object2IntOpenHashMap<UUID> bedTicks = new Object2IntOpenHashMap<>();

    @Override
    public String id() {
        return "rest";
    }

    @Override
    public int playerIntervalTicks() {
        return 1;
    }

    @Override
    public void onPlayerTick(ServerPlayerEntity player) {
        tick(player);
    }

    public void startSitting(ServerPlayerEntity player) {
        UUID id = player.getUUID();
        Info current = rest.get(id);
        if (current != null && current.type == Type.SIT) {
            if (player.isPassenger()) player.stopRiding();
            if (current.seat != null) current.seat.remove();
            rest.remove(id);
            player.setForcedPose(null);
            return;
        }

        Info info = new Info(Type.SIT);
        ArmorStandEntity seat = EntityType.ARMOR_STAND.create(player.level);
        if (seat != null) {
            seat.setInvisible(true);
            seat.setNoGravity(true);
            seat.setInvulnerable(true);
            setMarker(seat, true);
            seat.setPos(player.getX(), player.getY() - 0.1, player.getZ());
            player.level.addFreshEntity(seat);
            player.startRiding(seat, false);
            info.seat = seat;
        }
        rest.put(id, info);
    }

    private static void setMarker(ArmorStandEntity stand, boolean value) {
        try {
            Method m = ArmorStandEntity.class.getDeclaredMethod("setMarker", boolean.class);
            m.setAccessible(true);
            m.invoke(stand, value);
        } catch (Exception ignored) {
        }
    }

    private static void reduceFatigue(ServerPlayerEntity player, int amount) {
        player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
            int thirst = stats.getThirst();
            int fatigue = Math.max(0, stats.getFatigue() - amount);
            stats.setFatigue(fatigue);
            ModNetworkHandler.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new SyncAllStatsPacket(stats)
            );
        });
    }

    private void tick(ServerPlayerEntity player) {
        UUID id = player.getUUID();

        if (player.isSleeping()) {
            int t = bedTicks.getOrDefault(id, 0) + 1;
            bedTicks.put(id, t);
            return;
        } else if (bedTicks.containsKey(id)) {
            bedTicks.remove(id);
            player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
                stats.setFatigue(0);
                ModNetworkHandler.CHANNEL.send(
                        PacketDistributor.PLAYER.with(() -> player),
                        new SyncAllStatsPacket(stats)
                );
            });
        }

        Info info = rest.get(id);
        if (info == null) return;

        if (info.type == Type.SIT && info.seat != null) {
            if (!player.isPassenger() || player.getVehicle() != info.seat) {
                info.seat.remove();
                rest.remove(id);
                player.setForcedPose(null);
                return;
            }
        }

        info.ticks++;
        if (info.type == Type.SIT && info.ticks >= TICKS_PER_HOUR / 4) {
            info.ticks -= TICKS_PER_HOUR / 4;
            reduceFatigue(player, 5);
        }
    }
}


