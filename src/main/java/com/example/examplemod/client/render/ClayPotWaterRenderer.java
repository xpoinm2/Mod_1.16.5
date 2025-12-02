package com.example.examplemod.client.render;

import com.example.examplemod.tileentity.ClayPotTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
public class ClayPotWaterRenderer extends TileEntityRenderer<ClayPotTileEntity> {
    private static final ResourceLocation WATER_STILL = new ResourceLocation("minecraft", "block/water_still");
    public ClayPotWaterRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }
    @Override
    public void render(ClayPotTileEntity tile, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int light, int overlay) {
        int fluidAmount = tile.getTank().getFluidAmount();
        if (fluidAmount <= 0) {
            return;
        }

        matrix.pushPose();
        matrix.translate(0.5D, 0, 0.5D);
        float fill = MathHelper.clamp(fluidAmount / (float) ClayPotTileEntity.CAPACITY, 0.0F, 1.0F);
        renderInnerWater(matrix, buffer, light, overlay, fill);
        matrix.popPose();
    }
    private void renderInnerWater(MatrixStack matrix, IRenderTypeBuffer buffer, int light, int overlay, float fill) {
        IVertexBuilder builder = buffer.getBuffer(RenderType.translucent());
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(new ResourceLocation("textures/atlas/blocks.png")).apply(WATER_STILL);
        int color = 0x3F76E4;
        float r = ((color >> 16) & 0xFF) / 255.0F;
        float g = ((color >> 8) & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;
        float a = 0.7F;

        float waterHeight = fill * 7.5F; // Общая высота горшка 7.5 блоков

        // Определяем уровни воды и их размеры (внутренние размеры, уменьшенные на толщину стенок)
        // Уровень 1: 0.0-1.0, внутренний размер: 0.875x1.0x0.875
        // Уровень 2: 1.0-3.0, внутренний размер: 0.75x2.0x0.75
        // Уровень 3: 3.0-5.0, внутренний размер: 0.625x2.0x0.625
        // Уровень 4: 5.0-6.5, внутренний размер: 0.75x1.5x0.75
        // Уровень 5: 6.5-7.5, внутренний размер: 0.625x1.0x0.625

        // Рисуем воду по уровням снизу вверх
        float currentHeight = 0.0F;

        // Уровень 1: дно (высота 1.0)
        if (waterHeight > currentHeight) {
            float levelHeight = Math.min(waterHeight - currentHeight, 1.0F);
            matrix.pushPose();
            matrix.translate(0, currentHeight + levelHeight/2, 0);
            matrix.scale(0.92F, levelHeight, 0.92F);
            renderFace(builder, matrix, sprite, light, overlay, r, g, b, a, Direction.UP);
            renderFace(builder, matrix, sprite, light, overlay, r, g, b, a, Direction.DOWN);
            matrix.popPose();

            // Боковые стенки уровня 1
            renderLevelWalls(matrix, builder, sprite, light, overlay, r, g, b, a,
                           currentHeight, currentHeight + levelHeight, 0.92F, 0.92F);
        }
        currentHeight += 1.0F;

        // Уровень 2: средний широкий (высота 2.0)
        if (waterHeight > currentHeight) {
            float levelHeight = Math.min(waterHeight - currentHeight, 2.0F);
            matrix.pushPose();
            matrix.translate(0, currentHeight + levelHeight/2, 0);
            matrix.scale(0.8F, levelHeight, 0.8F);
            renderFace(builder, matrix, sprite, light, overlay, r, g, b, a, Direction.UP);
            renderFace(builder, matrix, sprite, light, overlay, r, g, b, a, Direction.DOWN);
            matrix.popPose();

            // Боковые стенки уровня 2
            renderLevelWalls(matrix, builder, sprite, light, overlay, r, g, b, a,
                           currentHeight, currentHeight + levelHeight, 0.8F, 0.8F);
        }
        currentHeight += 2.0F;

        // Уровень 3: самый широкий (высота 2.0)
        if (waterHeight > currentHeight) {
            float levelHeight = Math.min(waterHeight - currentHeight, 2.0F);
            matrix.pushPose();
            matrix.translate(0, currentHeight + levelHeight/2, 0);
            matrix.scale(0.68F, levelHeight, 0.68F);
            renderFace(builder, matrix, sprite, light, overlay, r, g, b, a, Direction.UP);
            renderFace(builder, matrix, sprite, light, overlay, r, g, b, a, Direction.DOWN);
            matrix.popPose();

            // Боковые стенки уровня 3
            renderLevelWalls(matrix, builder, sprite, light, overlay, r, g, b, a,
                           currentHeight, currentHeight + levelHeight, 0.68F, 0.68F);
        }
        currentHeight += 2.0F;

        // Уровень 4: средний узкий (высота 1.5)
        if (waterHeight > currentHeight) {
            float levelHeight = Math.min(waterHeight - currentHeight, 1.5F);
            matrix.pushPose();
            matrix.translate(0, currentHeight + levelHeight/2, 0);
            matrix.scale(0.8F, levelHeight, 0.8F);
            renderFace(builder, matrix, sprite, light, overlay, r, g, b, a, Direction.UP);
            renderFace(builder, matrix, sprite, light, overlay, r, g, b, a, Direction.DOWN);
            matrix.popPose();

            // Боковые стенки уровня 4
            renderLevelWalls(matrix, builder, sprite, light, overlay, r, g, b, a,
                           currentHeight, currentHeight + levelHeight, 0.8F, 0.8F);
        }
        currentHeight += 1.5F;

        // Уровень 5: верхний широкий (высота 1.0)
        if (waterHeight > currentHeight) {
            float levelHeight = Math.min(waterHeight - currentHeight, 1.0F);
            matrix.pushPose();
            matrix.translate(0, currentHeight + levelHeight/2, 0);
            matrix.scale(0.68F, levelHeight, 0.68F);
            renderFace(builder, matrix, sprite, light, overlay, r, g, b, a, Direction.UP);
            renderFace(builder, matrix, sprite, light, overlay, r, g, b, a, Direction.DOWN);
            matrix.popPose();

            // Боковые стенки уровня 5
            renderLevelWalls(matrix, builder, sprite, light, overlay, r, g, b, a,
                           currentHeight, currentHeight + levelHeight, 0.68F, 0.68F);
        }
    }

    private void renderLevelWalls(MatrixStack matrix, IVertexBuilder builder, TextureAtlasSprite sprite,
                                int light, int overlay, float r, float g, float b, float a,
                                float yMin, float yMax, float xSize, float zSize) {
        // Рисуем 4 стенки для каждого уровня
        Direction[] directions = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

        for (Direction dir : directions) {
            matrix.pushPose();
            switch (dir) {
                case NORTH:
                    matrix.translate(0, (yMin + yMax) / 2, -zSize/2);
                    matrix.scale(xSize, yMax - yMin, 0.001F);
                    break;
                case SOUTH:
                    matrix.translate(0, (yMin + yMax) / 2, zSize/2);
                    matrix.scale(xSize, yMax - yMin, 0.001F);
                    break;
                case EAST:
                    matrix.translate(xSize/2, (yMin + yMax) / 2, 0);
                    matrix.scale(0.001F, yMax - yMin, zSize);
                    break;
                case WEST:
                    matrix.translate(-xSize/2, (yMin + yMax) / 2, 0);
                    matrix.scale(0.001F, yMax - yMin, zSize);
                    break;
            }
            renderFace(builder, matrix, sprite, light, overlay, r, g, b, a, dir);
            matrix.popPose();
        }
    }
    private void renderFace(IVertexBuilder builder, MatrixStack matrix, TextureAtlasSprite sprite, int light, int overlay, float r, float g, float b, float a, Direction face) {
        float u0 = sprite.getU0(), v0 = sprite.getV0();
        float u1 = sprite.getU1(), v1 = sprite.getV1();
        builder.vertex(matrix.last().pose(), -0.5F, 0, -0.5F).color(r, g, b, a).uv(u0, v0).uv2(light).overlayCoords(overlay).normal(0, 1, 0).endVertex();
        builder.vertex(matrix.last().pose(), 0.5F, 0, -0.5F).color(r, g, b, a).uv(u1, v0).uv2(light).overlayCoords(overlay).normal(0, 1, 0).endVertex();
        builder.vertex(matrix.last().pose(), 0.5F, 0, 0.5F).color(r, g, b, a).uv(u1, v1).uv2(light).overlayCoords(overlay).normal(0, 1, 0).endVertex();
        builder.vertex(matrix.last().pose(), -0.5F, 0, 0.5F).color(r, g, b, a).uv(u0, v1).uv2(light).overlayCoords(overlay).normal(0, 1, 0).endVertex();
    }
}