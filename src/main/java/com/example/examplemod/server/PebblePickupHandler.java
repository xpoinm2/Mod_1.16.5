package com.example.examplemod.server;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.ModItems;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Allows picking up pebble item entities only when right-clicked.
 */
@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PebblePickupHandler {
    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        if (!(event.getTarget() instanceof ItemEntity)) return;

        ItemEntity entity = (ItemEntity) event.getTarget();
        ItemStack stack = entity.getItem();
        if (stack.getItem() != ModItems.PEBBLE.get()) return;

        World world = event.getWorld();
        if (!world.isClientSide()) {
            PlayerEntity player = event.getPlayer();
            if (player.addItem(stack.copy())) {
                entity.remove();
            }
        }
        event.setCancellationResult(ActionResultType.SUCCESS);
        event.setCanceled(true);
    }
}