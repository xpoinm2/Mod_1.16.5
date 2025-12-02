package com.example.examplemod.client.render;

import com.example.examplemod.block.ClayPotBlock;
import com.example.examplemod.tileentity.ClayPotTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
public class ClayPotWaterRenderer extends TileEntityRenderer<ClayPotTileEntity> {
    private static final ResourceLocation WATER_STILL = new ResourceLocation("minecraft", "block/water_still");
    public ClayPotWaterRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }
    @Override
    public void render(ClayPotTileEntity tile, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int light, int overlay) {
        BlockState state = tile.getBlockState();
        int level = state.getValue(ClayPotBlock.FILL_LEVEL);
        if (level == 0) return;
        matrix.pushPose();
        matrix.translate(0.5D, 0, 0.5D);
        float fill = (level / 8.0F) + (partialTicks / 8.0F);
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
// Inner bottom
        matrix.pushPose();
        matrix.translate(0, 0.1, 0);
        matrix.scale(0.92F, 0.01F, 0.92F);
        renderFace(builder, matrix, sprite, light, overlay, r, g, b, a, Direction.UP);
        matrix.popPose();
// Inner curved walls (8 секций)
        float wallHeight = fill * 0.8F;
        for (int i = 0; i < 8; i++) {
            matrix.pushPose();
            float angle = (float) (i * Math.PI / 4.0);
            matrix.mulPose(Vector3f.YP.rotationDegrees((float) Math.toDegrees(angle)));
            matrix.translate(0.46F, wallHeight / 2, 0);
            matrix.scale(0.03F, wallHeight, 0.03F);
            renderFace(builder, matrix, sprite, light, overlay, r, g, b, a, Direction.NORTH);
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