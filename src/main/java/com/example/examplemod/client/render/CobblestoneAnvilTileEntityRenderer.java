package com.example.examplemod.client.render;

import com.example.examplemod.tileentity.CobblestoneAnvilTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;

public class CobblestoneAnvilTileEntityRenderer extends TileEntityRenderer<CobblestoneAnvilTileEntity> {
    private static final double ITEM_Y_OFFSET = 0.78D;
    private static final float ITEM_SCALE = 0.4F;

    public CobblestoneAnvilTileEntityRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(CobblestoneAnvilTileEntity tileEntity, float partialTicks, MatrixStack matrixStack,
                       IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        // Нам больше не нужно отображать предметы на булыжниковой наковальне.
        // Оставляем метод пустым, чтобы логика блока и инвентаря не менялась,
        // но визуальные предметы сверху больше не рендерились.
    }
}
