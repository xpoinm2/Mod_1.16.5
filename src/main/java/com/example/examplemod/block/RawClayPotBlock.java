// Put into src/main/java/com/example/examplemod/block/RawClayPotBlock.java
package com.example.examplemod.block;

import net.minecraft.block.Block;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class RawClayPotBlock extends Block {
    private static final VoxelShape SHAPE = VoxelShapes.or(
        Block.makeCuboidShape(6,0,6, 10,1,10),
        Block.makeCuboidShape(6,1,6, 10,2,10),
        Block.makeCuboidShape(5,2,5, 11,3,11),
        Block.makeCuboidShape(4,3,4, 12,5,12),
        Block.makeCuboidShape(4,5,4, 12,7,12),
        Block.makeCuboidShape(5,7,5, 11,8,11),
        Block.makeCuboidShape(4,7.75,4, 12,8,12)
    );

    public RawClayPotBlock() {
        super(AbstractBlock.Properties.create(Material.CLAY)
            .hardnessAndResistance(0.8F)
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