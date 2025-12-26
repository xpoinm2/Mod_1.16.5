package com.example.examplemod.block;

import com.example.examplemod.tileentity.SlabTileEntity;
import com.example.examplemod.container.SlabContainer;
import com.example.examplemod.ModTileEntities;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

/**
 * Базовый класс для всех полублоков с GUI
 */
public abstract class BaseSlabBlock extends SlabBlock {

    public BaseSlabBlock(AbstractBlock.Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntities.SLAB.get().create();
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType use(BlockState state, World world, BlockPos pos,
                                PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (world.isClientSide) {
            return ActionResultType.SUCCESS;
        }

        TileEntity tile = world.getBlockEntity(pos);
        if (tile instanceof SlabTileEntity) {
            INamedContainerProvider provider = new SimpleNamedContainerProvider(
                    (windowId, playerInventory, playerEntity) -> new SlabContainer(windowId, playerInventory, (SlabTileEntity) tile),
                    new TranslationTextComponent("container.examplemod.slab")
            );
            if (player instanceof ServerPlayerEntity) {
                NetworkHooks.openGui((ServerPlayerEntity) player, provider, pos);
            }
        }

        return ActionResultType.CONSUME;
    }
}
