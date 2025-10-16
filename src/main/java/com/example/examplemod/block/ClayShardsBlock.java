package com.example.examplemod.block;

import com.example.examplemod.ModItems;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ClayShardsBlock extends Block {
    private static final VoxelShape SHAPE = VoxelShapes.or(
            Block.box(2.0D, 0.0D, 2.0D, 6.0D, 2.0D, 6.0D),
            Block.box(9.0D, 0.0D, 3.0D, 13.0D, 2.0D, 8.0D),
            Block.box(4.0D, 0.0D, 9.0D, 12.0D, 2.0D, 13.0D)
    );

    public ClayShardsBlock() {
        super(AbstractBlock.Properties.of(Material.CLAY)
                .strength(0.2F)
                .noOcclusion()
        );
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public void stepOn(World world, BlockPos pos, Entity entity) {
        super.stepOn(world, pos, entity);
        if (!world.isClientSide) {
            entity.hurt(DamageSource.CACTUS, 1.0F);
        }
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos,
                                PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (hand != Hand.MAIN_HAND) {
            return ActionResultType.PASS;
        }

        ItemStack held = player.getItemInHand(hand);
        if (!held.isEmpty()) {
            return ActionResultType.PASS;
        }

        if (!world.isClientSide) {
            ItemStack shards = new ItemStack(ModItems.CLAY_SHARDS.get());
            if (!player.addItem(shards)) {
                player.drop(shards, false);
            }
            world.removeBlock(pos, false);
        }

        return ActionResultType.sidedSuccess(world.isClientSide);
    }

    @Override
    public void playerDestroy(World world, PlayerEntity player, BlockPos pos, BlockState state,
                              @Nullable net.minecraft.tileentity.TileEntity tile, ItemStack toolStack) {
        player.awardStat(net.minecraft.stats.Stats.BLOCK_MINED.get(this));
        player.causeFoodExhaustion(0.005F);
        if (!world.isClientSide && toolStack.isEmpty()) {
            player.hurt(DamageSource.CACTUS, 4.0F);
        }
    }
}