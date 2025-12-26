package com.example.examplemod.server.mechanics.modules;

import com.example.examplemod.capability.PlayerStatsProvider;
import com.example.examplemod.network.ModNetworkHandler;
import com.example.examplemod.network.SyncHypothermiaPacket;
import com.example.examplemod.server.mechanics.IMechanicModule;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HypothermiaMechanic implements IMechanicModule {
    private static final int TICKS_PER_HOUR = 20 * 60;
    private static final Map<UUID, Integer> ANY_TICKS = new HashMap<>();
    private static final Map<UUID, Integer> BARE_TICKS = new HashMap<>();

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

        int temp = getAmbientTemperature(player);
        if (temp < -24) {
            int t = ANY_TICKS.getOrDefault(id, 0) + 20;
            if (t >= TICKS_PER_HOUR * 5) {
                t -= TICKS_PER_HOUR * 5;
                increase(player);
            }
            ANY_TICKS.put(id, t);

            if (noArmor(player)) {
                int b = BARE_TICKS.getOrDefault(id, 0) + 20;
                if (b >= TICKS_PER_HOUR * 2) {
                    b -= TICKS_PER_HOUR * 2;
                    increase(player);
                }
                BARE_TICKS.put(id, b);
            } else {
                BARE_TICKS.remove(id);
            }
        } else {
            ANY_TICKS.remove(id);
            BARE_TICKS.remove(id);
        }
    }

    @Override
    public void onPlayerLogout(ServerPlayerEntity player) {
        UUID id = player.getUUID();
        ANY_TICKS.remove(id);
        BARE_TICKS.remove(id);
    }

    private static void increase(ServerPlayerEntity player) {
        player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
            int current = stats.getHypothermia();
            int value = Math.min(100, current + 5);
            if (value != current) {
                stats.setHypothermia(value);
                ModNetworkHandler.CHANNEL.send(
                        PacketDistributor.PLAYER.with(() -> player),
                        new SyncHypothermiaPacket(value)
                );
            }
        });
    }

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
}

