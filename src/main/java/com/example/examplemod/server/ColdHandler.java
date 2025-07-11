package com.example.examplemod.server;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.capability.PlayerStatsProvider;
import com.example.examplemod.network.ModNetworkHandler;
import com.example.examplemod.network.SyncColdPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ColdHandler {
    private static final String KEY_COLD = "cold";
    private static final int TICKS_PER_HOUR = 20 * 60;

    private static final Map<UUID, Integer> HOUR_TICKS = new HashMap<>();

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

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayerEntity)) return;
        ServerPlayerEntity player = (ServerPlayerEntity) event.player;
        UUID id = player.getUUID();

        int temp = getAmbientTemperature(player);
        boolean increase = false;
        if (temp == -40 || temp == -25) {
            increase = true;
        } else if (temp < 16 && noArmor(player)) {
            increase = true;
        }

        if (increase) {
            int t = HOUR_TICKS.getOrDefault(id, 0) + 1;
            if (t >= TICKS_PER_HOUR) {
                t -= TICKS_PER_HOUR;
                int cold = Math.min(100, getStat(player, KEY_COLD, 0) + 4);
                setStat(player, KEY_COLD, cold);
                player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(s -> s.setCold(cold));
                ModNetworkHandler.CHANNEL.send(
                        PacketDistributor.PLAYER.with(() -> player),
                        new SyncColdPacket(cold)
                );
            }
            HOUR_TICKS.put(id, t);
        } else {
            HOUR_TICKS.remove(id);
        }
    }
}