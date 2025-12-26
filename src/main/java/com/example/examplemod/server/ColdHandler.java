package com.example.examplemod.server;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.capability.PlayerStatsProvider;
import com.example.examplemod.network.ModNetworkHandler;
import com.example.examplemod.network.SyncColdPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ColdHandler {
    private static final int TICKS_PER_HOUR = 20 * 60;

    private static final Map<UUID, Integer> HOUR_TICKS = new HashMap<>();

    private static boolean noArmor(PlayerEntity player) {
        for (EquipmentSlotType slot : new EquipmentSlotType[]{EquipmentSlotType.HEAD, EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.FEET}) {
            if (!player.getItemBySlot(slot).isEmpty()) return false;
        }
        return true;
    }

    private static int getAmbientTemperature(PlayerEntity player) {
        if (player == null) return 0;
        World world = player.level;
        if (world.dimension() == World.NETHER) return 666;
        if (world.dimension() == World.END) return -666;
        Biome biome = world.getBiome(new BlockPos(player.getX(), player.getY(), player.getZ()));
        Biome.Category cat = biome.getBiomeCategory();
        switch (cat) {
            case PLAINS:
                return 23;
            case DESERT:
            case MESA:
                return 37;
            case SAVANNA:
                return 30;
            case FOREST:
                return 17;
            case JUNGLE:
                return 30;
            case SWAMP:
                return -13;
            case TAIGA:
                return -25;
            case EXTREME_HILLS:
                return -10;
            case ICY:
                return -40;
            case BEACH:
            case RIVER:
                return 10;
            case OCEAN:
                return 6;
            case MUSHROOM:
                return 0;
            case NETHER:
                return 666;
            case THEEND:
                return -666;
            default:
                return 0;
        }
    }

    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayerEntity)) return;
        tick((ServerPlayerEntity) event.player);
    }

    /**
     * Вынесено для менеджера механик: можно вызывать напрямую без создания TickEvent.
     */
    public static void tick(ServerPlayerEntity player) {
        UUID id = player.getUUID();

        // Оптимизация: пересчитываем температуру/биом не каждый тик, а раз в секунду.
        if ((player.tickCount % 20) != 0) return;

        int temp = getAmbientTemperature(player);
        boolean increase = false;
        if (temp == -40 || temp == -25) {
            increase = true;
        } else if (temp < 16 && noArmor(player)) {
            increase = true;
        }

        if (increase) {
            int t = HOUR_TICKS.getOrDefault(id, 0) + 20;
            if (t >= TICKS_PER_HOUR) {
                t -= TICKS_PER_HOUR;
                player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
                    int current = stats.getCold();
                    int cold = Math.min(100, current + 4);
                    if (cold != current) {
                        stats.setCold(cold);
                        ModNetworkHandler.CHANNEL.send(
                                PacketDistributor.PLAYER.with(() -> player),
                                new SyncColdPacket(cold)
                        );
                    }
                });
            }
            HOUR_TICKS.put(id, t);
        } else {
            HOUR_TICKS.remove(id);
        }
    }
}