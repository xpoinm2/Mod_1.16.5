package com.example.examplemod.item;

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
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

/**
 * Fly agaric item. Eating it gives nausea and increases poison by 25%.
 */
public class MuhomorItem extends BlockItem {
    public MuhomorItem(Block block, Properties props) {
        super(block, props);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, World world, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, world, entity);
        if (!world.isClientSide && entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            player.addEffect(new EffectInstance(Effects.CONFUSION, 200));
            player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
                int value = Math.min(100, stats.getPoison() + 25);
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