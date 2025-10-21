package com.example.examplemod.block;

import com.example.examplemod.ModBlocks;
import com.example.examplemod.ModItems;
import com.example.examplemod.ModTileEntities;
import com.example.examplemod.tileentity.ClayPotTileEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.Explosion;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;

import javax.annotation.Nullable;

public class ClayPotBlock extends Block {
    private static final VoxelShape SHAPE = VoxelShapes.or(
            Block.box(7.0D, 0.0D, 7.0D, 9.0D, 1.0D, 9.0D),
            Block.box(6.0D, 1.0D, 6.0D, 10.0D, 3.0D, 10.0D),
            Block.box(5.0D, 3.0D, 5.0D, 11.0D, 5.0D, 11.0D),
            Block.box(6.0D, 5.0D, 6.0D, 10.0D, 6.5D, 10.0D),
            Block.box(5.0D, 6.5D, 5.0D, 11.0D, 7.5D, 11.0D)
    );

    public static final IntegerProperty FILL_LEVEL = IntegerProperty.create("level", 0, 4);

    public ClayPotBlock() {
        super(AbstractBlock.Properties.of(Material.CLAY)
                .strength(0.8F, 0.8F)
                .noOcclusion()
        );
        this.registerDefaultState(this.stateDefinition.any().setValue(FILL_LEVEL, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FILL_LEVEL);
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
    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType use(BlockState state, World world, BlockPos pos,
                                PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (hand != Hand.MAIN_HAND) {
            return ActionResultType.PASS;
        }

        ItemStack heldStack = player.getItemInHand(hand);

        if (!heldStack.isEmpty()) {
            return ActionResultType.PASS;
        }

        if (!world.isClientSide) {
            TileEntity tile = world.getBlockEntity(pos);
            if (tile instanceof ClayPotTileEntity) {
                ((ClayPotTileEntity) tile).clear();
            }
            ItemStack stack = new ItemStack(ModItems.CLAY_POT.get());
            if (!player.addItem(stack)) {
                player.drop(stack, false);
            }
            world.removeBlock(pos, false);
        }

        return ActionResultType.sidedSuccess(world.isClientSide);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntities.CLAY_POT.get().create();
    }

    @Override
    public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean moving) {
        if (!state.is(newState.getBlock())) {
            super.onRemove(state, world, pos, newState, moving);
            world.removeBlockEntity(pos);
        }
    }

    @Override
    public void playerDestroy(World world, PlayerEntity player, BlockPos pos, BlockState state,
                              @javax.annotation.Nullable net.minecraft.tileentity.TileEntity tile, ItemStack stack) {
        player.awardStat(Stats.BLOCK_MINED.get(this));
        player.causeFoodExhaustion(0.005F);
        if (!world.isClientSide) {
            world.setBlock(pos, ModBlocks.CLAY_SHARDS.get().defaultBlockState(), 3);
        }
    }

    @Override
    public void onBlockExploded(BlockState state, World world, BlockPos pos, Explosion explosion) {
        super.onBlockExploded(state, world, pos, explosion);
        if (!world.isClientSide) {
            world.setBlock(pos, ModBlocks.CLAY_SHARDS.get().defaultBlockState(), 3);
        }
    }
}