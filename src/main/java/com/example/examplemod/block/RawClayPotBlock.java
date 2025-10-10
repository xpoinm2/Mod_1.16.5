package com.example.examplemod.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class RawClayPotBlock extends Block {
    private static final VoxelShape SHAPE = VoxelShapes.or(
            Block.box(6.0D, 0.0D, 6.0D, 10.0D, 1.0D, 10.0D),
            Block.box(6.0D, 1.0D, 6.0D, 10.0D, 2.0D, 10.0D),
            Block.box(5.0D, 2.0D, 5.0D, 11.0D, 3.0D, 11.0D),
            Block.box(4.0D, 3.0D, 4.0D, 12.0D, 5.0D, 12.0D),
            Block.box(4.0D, 5.0D, 4.0D, 12.0D, 7.0D, 12.0D),
            Block.box(5.0D, 7.0D, 5.0D, 11.0D, 8.0D, 11.0D),
            Block.box(4.0D, 7.75D, 4.0D, 12.0D, 8.0D, 12.0D)
    );

    public RawClayPotBlock() {
        super(AbstractBlock.Properties.of(Material.CLAY)
                .hardnessAndResistance(0.8F, 0.8F)
                .notSolid()
        );
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx) {
        return SHAPE;
    }
    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx) {
        return SHAPE;
    }
    @Override
    public boolean propagatesSkylightDown(BlockState state, IBlockReader world, BlockPos pos) {
        return true;
    }
}