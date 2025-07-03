package com.example.examplemod.server;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.ForgeMod;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FlaxSoakHandler {
    @SubscribeEvent
    public static void onUse(PlayerInteractEvent.RightClickItem event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() != ModItems.FLAX.get()) return;

        PlayerEntity player = event.getPlayer();
        World world = event.getWorld();
        if (world.isClientSide) return;

        double reach = player.getAttribute(ForgeMod.REACH_DISTANCE.get()) != null
                ? player.getAttributeValue(ForgeMod.REACH_DISTANCE.get())
                : 5.0D;
        RayTraceResult res = player.pick(reach, 0.0F, true);
        if (res.getType() != RayTraceResult.Type.BLOCK) return;

        BlockPos pos = ((BlockRayTraceResult) res).getBlockPos();
        if (!world.getFluidState(pos).is(FluidTags.WATER)) return;

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