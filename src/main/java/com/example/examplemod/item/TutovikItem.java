package com.example.examplemod.item;

import com.example.examplemod.block.TutovikBlock;
import com.example.examplemod.capability.PlayerStatsProvider;
import com.example.examplemod.network.ModNetworkHandler;
import com.example.examplemod.network.SyncPoisonPacket;
import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

public class TutovikItem extends BlockItem {
    public TutovikItem(Block block, Properties props) {
        super(block, props);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, World world, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, world, entity);
        if (!world.isClientSide && entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
                int value = Math.min(100, stats.getPoison() + 5);
                stats.setPoison(value);
                CompoundNBT root = player.getPersistentData();
                if (!root.contains(PlayerEntity.PERSISTED_NBT_TAG)) {
                    root.put(PlayerEntity.PERSISTED_NBT_TAG, new CompoundNBT());
                }
                root.getCompound(PlayerEntity.PERSISTED_NBT_TAG).putInt("poison", value);
                if (player instanceof ServerPlayerEntity) {
                    ModNetworkHandler.CHANNEL.send(
                            PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player),
                            new SyncPoisonPacket(value)
                    );
                }
            });
        }
        return result;
    }
}