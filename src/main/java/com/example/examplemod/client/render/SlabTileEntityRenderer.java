package com.example.examplemod.client.render;

import com.example.examplemod.tileentity.SlabTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.math.vector.Vector3f;

public class SlabTileEntityRenderer extends TileEntityRenderer<SlabTileEntity> {
    
    public SlabTileEntityRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(SlabTileEntity tile, float partialTicks, MatrixStack matrixStack, 
                      IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        BlockState state = tile.getBlockState();
        SlabType slabType = state.getValue(SlabBlock.TYPE);
        
        // Определяем высоту в зависимости от типа полублока
        // TOP = верхний полублок (y = 1.0), BOTTOM = нижний (y = 0.5), DOUBLE = полный блок (y = 0.5)
        double yOffset = (slabType == SlabType.TOP) ? 1.0D : 0.5D;
        
        // Рендерим предметы в сетке 3x3
        int slotIndex = 0;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                ItemStack stack = tile.getInventory().getStackInSlot(slotIndex);
                if (!stack.isEmpty()) {
                    matrixStack.pushPose();
                    
                    // Позиция предмета на сетке 3x3
                    // Распределяем предметы равномерно по площади блока
                    double xOffset = (col - 1) * 0.25D; // -0.25, 0, 0.25
                    double zOffset = (row - 1) * 0.25D; // -0.25, 0, 0.25
                    
                    matrixStack.translate(0.5D + xOffset, yOffset + 0.05D, 0.5D + zOffset);
                    
                    // Масштабируем предмет, чтобы он был меньше
                    matrixStack.scale(0.4F, 0.4F, 0.4F);
                    
                    // Вращаем предмет для лучшей видимости (лежит на поверхности)
                    matrixStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
                    
                    // Рендерим предмет
                    itemRenderer.renderStatic(stack, ItemCameraTransforms.TransformType.FIXED, 
                                             combinedLight, combinedOverlay, matrixStack, buffer);
                    
                    matrixStack.popPose();
                }
                slotIndex++;
            }
        }
    }
}

