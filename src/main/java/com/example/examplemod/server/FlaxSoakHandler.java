package com.example.examplemod.server;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FlaxSoakHandler {
    @SubscribeEvent
    public static void onUse(PlayerInteractEvent.RightClickBlock event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() != ModItems.FLAX.get()) return;

        World world = event.getWorld();
        if (world.isClientSide) return;

        BlockPos pos = event.getPos();
        if (!world.getFluidState(pos).is(FluidTags.WATER)) return;

        PlayerEntity player = event.getPlayer();
        Hand hand = event.getHand();

        ItemStack soaked = new ItemStack(ModItems.SOAKED_FLAX.get());
        if (!player.abilities.instabuild) {
            stack.shrink(1);
        }
        if (player.getItemInHand(hand).isEmpty()) {
            player.setItemInHand(hand, soaked);
        } else {
            if (!player.inventory.add(soaked)) {
                player.drop(soaked, false);
            }
        }
        event.setCanceled(true);
    }
}