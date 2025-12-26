package com.example.examplemod.item;

import com.example.examplemod.network.ModNetworkHandler;
import com.example.examplemod.network.SyncAllStatsPacket;
import com.example.examplemod.capability.PlayerStatsProvider;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

public class ElderberryItem extends Item {
    public ElderberryItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, World world, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, world, entity);
        if (!world.isClientSide && entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
                int cold = Math.max(0, stats.getCold() - 1);
                stats.setCold(cold);
                if (player instanceof ServerPlayerEntity) {
                    ModNetworkHandler.CHANNEL.send(
                            PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player),
                            new SyncAllStatsPacket(stats)
                    );
                }
            });
        }
        return result;
    }
}