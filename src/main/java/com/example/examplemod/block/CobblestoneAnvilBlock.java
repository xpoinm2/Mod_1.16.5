package com.example.examplemod.block;

import com.example.examplemod.ModContainers;
import com.example.examplemod.tileentity.CobblestoneAnvilTileEntity;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class CobblestoneAnvilBlock extends AnvilBlock {
    public CobblestoneAnvilBlock() {
        // Копируем свойства ванильной наковальни, но делаем её в 3 раза менее прочной.
        // У ванильной наковальни прочность ~5.0F и взрывоустойчивость ~1200.0F.
        // Здесь выставляем 5 / 3 и 1200 / 3.
        super(Properties
                .copy(net.minecraft.block.Blocks.ANVIL)
                .strength(5.0F / 3.0F, 1200.0F / 3.0F));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new CobblestoneAnvilTileEntity();
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos,
                                PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!world.isClientSide) {
            TileEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof CobblestoneAnvilTileEntity) {
                INamedContainerProvider containerProvider = new SimpleNamedContainerProvider(
                        (windowId, playerInventory, playerEntity) ->
                                new com.example.examplemod.container.CobblestoneAnvilContainer(
                                        windowId, playerInventory, (CobblestoneAnvilTileEntity) tileEntity),
                        new StringTextComponent("Наковальня из булыжника"));
                NetworkHooks.openGui((ServerPlayerEntity) player, containerProvider, pos);
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            TileEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof CobblestoneAnvilTileEntity) {
                ((CobblestoneAnvilTileEntity) tileEntity).dropInventoryContents();
            }
            super.onRemove(state, world, pos, newState, isMoving);
        }
    }
}