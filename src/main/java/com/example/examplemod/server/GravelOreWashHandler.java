package com.example.examplemod.server;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GravelOreWashHandler {
    @SubscribeEvent
    public static void onUse(PlayerInteractEvent.RightClickItem event) {
        ItemStack stack = event.getItemStack();
        Item resultItem = null;

        if (stack.getItem() == ModItems.TIN_ORE_GRAVEL.get()) {
            resultItem = ModItems.CLEANED_GRAVEL_TIN_ORE.get();
        } else if (stack.getItem() == ModItems.GOLD_ORE_GRAVEL.get()) {
            resultItem = ModItems.CLEANED_GRAVEL_GOLD_ORE.get();
        }
        if (resultItem == null) return;

        PlayerEntity player = event.getPlayer();
        if (player.level.isClientSide) return;

        double reach = player.getAttribute(ForgeMod.REACH_DISTANCE.get()) != null
                ? player.getAttributeValue(ForgeMod.REACH_DISTANCE.get())
                : 5.0D;
        RayTraceResult res = player.pick(reach, 0.0F, true);
        if (res.getType() != RayTraceResult.Type.BLOCK) return;

        BlockPos pos = ((BlockRayTraceResult) res).getBlockPos();
        if (!player.level.getFluidState(pos).is(FluidTags.WATER)) return;

        Hand hand = event.getHand();

        if (!player.abilities.instabuild) {
            stack.shrink(1);
        }

        ItemStack cleanedStack = new ItemStack(resultItem);
        if (player.getItemInHand(hand).isEmpty()) {
            player.setItemInHand(hand, cleanedStack);
        } else {
            if (!player.inventory.add(cleanedStack)) {
                player.drop(cleanedStack, false);
            }
        }

        event.setCanceled(true);
    }
}

