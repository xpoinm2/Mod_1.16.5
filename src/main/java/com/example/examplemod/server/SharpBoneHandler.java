package com.example.examplemod.server;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SharpBoneHandler {
    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() != ModItems.BIG_BONE.get()) return;

        World world = event.getWorld();
        if (world.isClientSide) return;

        BlockPos pos = event.getPos();
        BlockState state = world.getBlockState(pos);
        if (!state.is(BlockTags.BASE_STONE_OVERWORLD)) return;

        PlayerEntity player = event.getPlayer();
        Hand hand = event.getHand();
        player.setItemInHand(hand, new ItemStack(ModItems.SHARPENED_BONE.get()));
    }
}