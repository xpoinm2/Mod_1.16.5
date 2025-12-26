package com.example.examplemod.server;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.capability.PlayerStatsProvider;
import com.example.examplemod.network.ModNetworkHandler;
import com.example.examplemod.network.SyncAllStatsPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.network.PacketDistributor;

public class RedMushroomHandler {
    public static void onMushroomUse(PlayerInteractEvent.RightClickItem event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() != Items.RED_MUSHROOM) return;
        PlayerEntity player = event.getPlayer();
        if (player.level.isClientSide) return;

        player.addEffect(new EffectInstance(Effects.CONFUSION, 200));
        player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
            int value = Math.min(100, stats.getPoison() + 25);
            stats.setPoison(value);
            if (player instanceof ServerPlayerEntity) {
                ModNetworkHandler.CHANNEL.send(
                        PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player),
                        new SyncAllStatsPacket(stats)
                );
            }
        });
        if (!player.abilities.instabuild) {
            stack.shrink(1);
        }
        event.setCanceled(true);
    }
}