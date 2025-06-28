package com.example.examplemod.item;

import com.example.examplemod.network.ModNetworkHandler;
import com.example.examplemod.network.SyncHypothermiaPacket;
import com.example.examplemod.capability.PlayerStatsProvider;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

/**
 * Item representing ginger. Eating it reduces hypothermia by 1%.
 */
public class GingerItem extends Item {
    public GingerItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, World world, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, world, entity);
        if (!world.isClientSide && entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
                int value = Math.max(0, stats.getHypothermia() - 1);
                stats.setHypothermia(value);
                CompoundNBT root = player.getPersistentData();
                if (!root.contains(PlayerEntity.PERSISTED_NBT_TAG)) {
                    root.put(PlayerEntity.PERSISTED_NBT_TAG, new CompoundNBT());
                }
                root.getCompound(PlayerEntity.PERSISTED_NBT_TAG).putInt("hypothermia", value);
                if (player instanceof ServerPlayerEntity) {
                    ModNetworkHandler.CHANNEL.send(
                            PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player),
                            new SyncHypothermiaPacket(value)
                    );
                }
            });
        }
        return result;
    }
}