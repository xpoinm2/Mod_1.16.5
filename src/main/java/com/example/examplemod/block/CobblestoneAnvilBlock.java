package com.example.examplemod.block;

import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public class CobblestoneAnvilBlock extends AnvilBlock {
    public CobblestoneAnvilBlock() {
        super(Properties.copy(net.minecraft.block.Blocks.ANVIL));
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos,
                                PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        // Пока просто возвращаем успех - в будущем можно добавить функциональность наковальни
        return ActionResultType.sidedSuccess(world.isClientSide);
    }
}