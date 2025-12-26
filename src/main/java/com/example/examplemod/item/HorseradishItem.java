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

/**
 * Item representing horseradish. Eating it reduces player's virus level.
 */
public class HorseradishItem extends Item {
    public HorseradishItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, World world, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, world, entity);
        if (!world.isClientSide && entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
                int virus = Math.max(0, stats.getVirus() - 5);
                stats.setVirus(virus);
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