package com.example.examplemod.server;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.capability.PlayerStatsProvider;
import com.example.examplemod.network.ModNetworkHandler;
import com.example.examplemod.network.SyncVirusPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraft.world.World;

public class VirusHandler {
    private static final String KEY_VIRUS = "virus";

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

    private static void increase(ServerPlayerEntity player, int amount) {
        int value = Math.min(100, getStat(player, KEY_VIRUS, 0) + amount);
        setStat(player, KEY_VIRUS, value);
        player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(s -> s.setVirus(value));
        ModNetworkHandler.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                new SyncVirusPacket(value)
        );
    }

    public static void onFoodEaten(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntityLiving() instanceof ServerPlayerEntity)) return;
        ItemStack stack = event.getItem();
        if (stack.getItem().isEdible()) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getEntityLiving();
            if (player.level.random.nextFloat() < 0.02f) {
                increase(player, 10);
            }
        }
    }

    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        World world = (World) event.getWorld();
        if (world.isClientSide()) return;
        PlayerEntity player = event.getPlayer();
        if (world.random.nextFloat() < 0.01f && player instanceof ServerPlayerEntity) {
            increase((ServerPlayerEntity) player, 5);
        }
    }
}