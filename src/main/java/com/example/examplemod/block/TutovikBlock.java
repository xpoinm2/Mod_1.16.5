package com.example.examplemod.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

public class TutovikBlock extends Block {
    public static final DirectionProperty FACING = HorizontalBlock.FACING;
    private static final VoxelShape SHAPE_N = Block.box(4, 4, 14, 12, 12, 16);
    private static final VoxelShape SHAPE_S = Block.box(4, 4, 0, 12, 12, 2);
    private static final VoxelShape SHAPE_W = Block.box(14, 4, 4, 16, 12, 12);
    private static final VoxelShape SHAPE_E = Block.box(0, 4, 4, 2, 12, 12);

    public TutovikBlock() {
        super(Properties.copy(Blocks.BROWN_MUSHROOM).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx) {
        switch (state.getValue(FACING)) {
            case SOUTH: return SHAPE_S;
            case WEST:  return SHAPE_W;
            case EAST:  return SHAPE_E;
            default:    return SHAPE_N;
        }
    }

    private boolean canAttach(IBlockReader world, BlockPos pos, Direction dir) {
        BlockPos supportPos = pos.relative(dir.getOpposite());
        BlockState support = world.getBlockState(supportPos);
        return support.is(BlockTags.LOGS);
    }

    @Override
    public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
        return canAttach(world, pos, state.getValue(FACING));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction dir, BlockState neighborState,
                                  IWorld world, BlockPos pos, BlockPos neighborPos) {
        return !canSurvive(state, world, pos) ? Blocks.AIR.defaultBlockState() : state;
    }
}