package com.example.examplemod.server;

import com.example.examplemod.ExampleMod;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Removes any items not from this mod from player inventories on the server.
 */
@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForeignItemRemovalHandler {
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayerEntity)) return;
        ServerPlayerEntity player = (ServerPlayerEntity) event.player;

        boolean changed = false;
        for (int i = 0; i < player.inventory.getContainerSize(); i++) {
            ItemStack stack = player.inventory.getItem(i);
            if (stack.isEmpty()) continue;
            ResourceLocation id = stack.getItem().getRegistryName();
            if (id != null && !ExampleMod.MODID.equals(id.getNamespace())) {
                player.inventory.setItem(i, ItemStack.EMPTY);
                changed = true;
            }
        }
        if (changed) {
            player.containerMenu.broadcastChanges();
        }
    }
}