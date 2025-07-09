package com.example.examplemod.server;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
public class IronClusterWashHandler {
    @SubscribeEvent
    public static void onUse(PlayerInteractEvent.RightClickItem event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() != ModItems.IRON_CLUSTER.get()) return;

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

        ItemStack pureOre = new ItemStack(ModItems.PURE_IRON_ORE.get());
        ItemStack clay = new ItemStack(Items.CLAY_BALL, 2);

        if (!player.abilities.instabuild) {
            stack.shrink(1);
        }

        if (player.getItemInHand(hand).isEmpty()) {
            player.setItemInHand(hand, pureOre);
        } else {
            if (!player.inventory.add(pureOre)) {
                player.drop(pureOre, false);
            }
        }

        if (!player.inventory.add(clay)) {
            player.drop(clay, false);
        }
        event.setCanceled(true);
    }
}