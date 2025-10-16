package com.example.examplemod.item;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ClayShardsBlockItem extends BlockItem {
    public ClayShardsBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();

        if (context.getClickedFace() != Direction.UP) {
            return ActionResultType.FAIL;
        }

        if (!world.getBlockState(clickedPos).getMaterial().isSolid()) {
            return ActionResultType.FAIL;
        }

        BlockPos placePos = clickedPos.above();
        if (!world.getFluidState(placePos).isEmpty()) {
            return ActionResultType.FAIL;
        }

        return super.useOn(context);
    }
}