package com.example.examplemod.server;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.ModBlocks;
import com.example.examplemod.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class FlaxDryingHandler {
    public static void onUse(PlayerInteractEvent.RightClickBlock event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() != ModItems.SOAKED_FLAX.get()) return;

        World world = event.getWorld();
        if (world.isClientSide) return;

        if (event.getFace() != Direction.DOWN) return;

        BlockPos pos = event.getPos();
        if (!world.getBlockState(pos).is(BlockTags.LEAVES)) return;

        BlockPos place = pos.below();
        if (!world.getBlockState(place).isAir()) return;

        world.setBlock(place, ModBlocks.HANGING_FLAX.get().defaultBlockState(), 3);
        if (!event.getPlayer().abilities.instabuild) {
            stack.shrink(1);
        }
        event.setCanceled(true);
    }
}