package com.example.examplemod.item;

import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PyriteFlintItem extends Item {
    public PyriteFlintItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = world.getBlockState(pos);
        if (state.is(BlockTags.PLANKS) ||
                (state.getBlock() instanceof SlabBlock && state.getMaterial() == net.minecraft.block.material.Material.WOOD)) {
            BlockPos firePos = pos.above();
            if (world.isEmptyBlock(firePos)) {
                world.playSound(context.getPlayer(), firePos,
                        SoundEvents.FLINTANDSTEEL_USE, SoundCategory.BLOCKS,
                        1.0F, world.random.nextFloat() * 0.4F + 0.8F);
                world.setBlock(firePos, AbstractFireBlock.getState(world, firePos), 11);
                PlayerEntity player = context.getPlayer();
                if (player != null) {
                    context.getItemInHand().hurtAndBreak(1, player,
                            (p) -> p.broadcastBreakEvent(context.getHand()));
                } else {
                    context.getItemInHand().shrink(1);
                }
                return ActionResultType.sidedSuccess(world.isClientSide());
            }
        }
        return ActionResultType.PASS;
    }
}