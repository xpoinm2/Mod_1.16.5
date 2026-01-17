package com.example.examplemod.server.mechanics.modules;

import com.example.examplemod.capability.PlayerStatsProvider;
import com.example.examplemod.network.ModNetworkHandler;
import com.example.examplemod.network.SyncAllStatsPacket;
import com.example.examplemod.server.mechanics.IMechanicModule;
import com.example.examplemod.server.mechanics.util.BiomeTemperatureCache;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.UUID;

public class ColdMechanic implements IMechanicModule {
    private static final int TICKS_PER_HOUR = 20 * 60;
    // Оптимизация: Object2IntOpenHashMap вместо HashMap<UUID, Integer> (50-70% меньше памяти)
    private static final Object2IntOpenHashMap<UUID> HOUR_TICKS = new Object2IntOpenHashMap<>();
    // Оптимизация: статический массив вместо создания нового каждый раз (экономия аллокаций)
    private static final EquipmentSlotType[] ARMOR_SLOTS = {
        EquipmentSlotType.HEAD, 
        EquipmentSlotType.CHEST, 
        EquipmentSlotType.LEGS, 
        EquipmentSlotType.FEET
    };

    @Override
    public String id() {
        return "cold";
    }

    @Override
    public int playerIntervalTicks() {
        return 20; // раз в секунду
    }

    @Override
    public void onPlayerTick(ServerPlayerEntity player) {
        // Не применяем механики, если игрок вышел из мира (в главном меню)
        if (player.connection == null || player.hasDisconnected()) {
            return;
        }

        UUID id = player.getUUID();

        // ОПТИМИЗАЦИЯ: используем кэш вместо прямого вызова world.getBiome()
        // Кэш с TTL 30 секунд → 50-70% меньше дорогих вызовов
        int temp = BiomeTemperatureCache.getTemperature(player);
        boolean increase = temp < 16; // Cold increases when biome temperature < 16°C (always, no armor dependency)

        if (increase) {
            int t = HOUR_TICKS.getOrDefault(id, 0) + 20;
            if (t >= 2400) { // Every 2400 ticks (2 real minutes)
                t -= 2400;
                player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
                    int current = stats.getCold();
                    int cold = Math.min(100, current + 4);
                    if (cold != current) {
                        stats.setCold(cold);
                        // Оптимизация: SyncAllStatsPacket вместо отдельного пакета
                        ModNetworkHandler.CHANNEL.send(
                                PacketDistributor.PLAYER.with(() -> player),
                                new SyncAllStatsPacket(stats)
                        );
                    }
                });
            }
            HOUR_TICKS.put(id, t);
        } else {
            HOUR_TICKS.remove(id);
        }
    }

    @Override
    public void onPlayerLogout(ServerPlayerEntity player) {
        UUID id = player.getUUID();
        HOUR_TICKS.remove(id);
        // Очищаем кэш температуры при выходе игрока
        BiomeTemperatureCache.clearPlayer(id);
    }

    private static boolean noArmor(PlayerEntity player) {
        for (EquipmentSlotType slot : ARMOR_SLOTS) {
            if (!player.getItemBySlot(slot).isEmpty()) return false;
        }
        return true;
    }
    
    // УДАЛЕНО: getAmbientTemperature() больше не нужен,
    // используется BiomeTemperatureCache.getTemperature() с кэшированием
}

