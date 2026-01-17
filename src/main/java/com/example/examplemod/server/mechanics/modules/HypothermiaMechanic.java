package com.example.examplemod.server.mechanics.modules;

import com.example.examplemod.capability.PlayerStatsProvider;
import com.example.examplemod.network.ModNetworkHandler;
import com.example.examplemod.network.SyncAllStatsPacket;
import com.example.examplemod.server.mechanics.IMechanicModule;
import com.example.examplemod.server.mechanics.util.BiomeTemperatureCache;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.UUID;

public class HypothermiaMechanic implements IMechanicModule {
    private static final int TICKS_PER_HOUR = 20 * 60;
    // Оптимизация: Object2IntOpenHashMap вместо HashMap<UUID, Integer> (50-70% меньше памяти)
    private static final Object2IntOpenHashMap<UUID> ANY_TICKS = new Object2IntOpenHashMap<>();

    @Override
    public String id() {
        return "hypothermia";
    }

    @Override
    public int playerIntervalTicks() {
        return 20; // раз в секунду
    }

    @Override
    public void onPlayerTick(ServerPlayerEntity player) {
        UUID id = player.getUUID();

        // ОПТИМИЗАЦИЯ: используем кэш вместо прямого вызова world.getBiome()
        // Кэш с TTL 30 секунд → 50-70% меньше дорогих вызовов
        int temp = BiomeTemperatureCache.getTemperature(player);
        if (temp < 0) { // Hypothermia increases when biome temperature < 0°C (always, no armor dependency)
            int t = ANY_TICKS.getOrDefault(id, 0) + 20;
            if (t >= 2400) { // Every 2400 ticks (2 real minutes)
                t -= 2400;
                increase(player);
            }
            ANY_TICKS.put(id, t);
        } else {
            ANY_TICKS.remove(id);
        }
    }

    @Override
    public void onPlayerLogout(ServerPlayerEntity player) {
        UUID id = player.getUUID();
        ANY_TICKS.remove(id);
        // Очищаем кэш температуры при выходе игрока
        BiomeTemperatureCache.clearPlayer(id);
    }

    private static void increase(ServerPlayerEntity player) {
        player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
            int current = stats.getHypothermia();
            int value = Math.min(100, current + 5);
            if (value != current) {
                stats.setHypothermia(value);
                // Оптимизация: SyncAllStatsPacket вместо отдельного пакета
                ModNetworkHandler.CHANNEL.send(
                        PacketDistributor.PLAYER.with(() -> player),
                        new SyncAllStatsPacket(stats)
                );
            }
        });
    }
    
    // УДАЛЕНО: getAmbientTemperature() больше не нужен,
    // используется BiomeTemperatureCache.getTemperature() с кэшированием
}

