package com.example.examplemod.client.render;

import com.example.examplemod.ExampleMod;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.world.DimensionRenderInfo;
import net.minecraft.client.world.DimensionRenderInfo.FogType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.ISkyRenderHandler;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Custom visual settings for the Heaven dimension.
 */

@ParametersAreNonnullByDefault
public class HeavenDimensionRenderInfo extends DimensionRenderInfo {

    private static final ResourceLocation HEAVEN_SKY = new ResourceLocation(ExampleMod.MODID,
            "textures/environment/heaven_sky.png");

    public HeavenDimensionRenderInfo() {
        super(Float.NaN, false, FogType.NONE, false, true);
        this.setSkyRenderHandler(new HeavenSkyRenderer());
        this.setCloudRenderHandler((ticks, partialTicks, matrixStack, level, mc, cameraX, cameraY, cameraZ) -> {
            // Intentionally empty: clouds are disabled in Heaven.
        });
        this.setWeatherRenderHandler((ticks, partialTicks, level, mc, lightTexture, x, y, z) -> {
            // No weather in Heaven.
        });
    }

    @Override
    public Vector3d getBrightnessDependentFogColor(@Nonnull Vector3d color, float sunHeight) {
        return new Vector3d(1.0D, 1.0D, 1.0D);
    }

    @Override
    public boolean isFoggyAt(int x, int z) {
        return false;
    }


    @ParametersAreNonnullByDefault

    private static final class HeavenSkyRenderer implements ISkyRenderHandler {

        @Override
        public void render(int ticks, float partialTicks, MatrixStack matrixStack, ClientWorld world, Minecraft mc) {
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableCull();
            RenderSystem.enableTexture();

            mc.getTextureManager().bind(HEAVEN_SKY);

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuilder();

            float size = 200.0F;
            for (int i = 0; i < 6; i++) {
                matrixStack.pushPose();
                Matrix4f matrix4f = matrixStack.last().pose();
                buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

                switch (i) {
                    case 0: // Top
                        buffer.vertex(matrix4f, -size, size, -size).uv(0.0F, 0.0F).endVertex();
                        buffer.vertex(matrix4f, -size, size, size).uv(0.0F, 1.0F).endVertex();
                        buffer.vertex(matrix4f, size, size, size).uv(1.0F, 1.0F).endVertex();
                        buffer.vertex(matrix4f, size, size, -size).uv(1.0F, 0.0F).endVertex();
                        break;
                    case 1: // Bottom
                        buffer.vertex(matrix4f, -size, -size, size).uv(0.0F, 0.0F).endVertex();
                        buffer.vertex(matrix4f, -size, -size, -size).uv(0.0F, 1.0F).endVertex();
                        buffer.vertex(matrix4f, size, -size, -size).uv(1.0F, 1.0F).endVertex();
                        buffer.vertex(matrix4f, size, -size, size).uv(1.0F, 0.0F).endVertex();
                        break;
                    case 2: // North (-Z)
                        buffer.vertex(matrix4f, -size, -size, -size).uv(0.0F, 0.0F).endVertex();
                        buffer.vertex(matrix4f, -size, size, -size).uv(0.0F, 1.0F).endVertex();
                        buffer.vertex(matrix4f, size, size, -size).uv(1.0F, 1.0F).endVertex();
                        buffer.vertex(matrix4f, size, -size, -size).uv(1.0F, 0.0F).endVertex();
                        break;
                    case 3: // South (+Z)
                        buffer.vertex(matrix4f, size, -size, size).uv(0.0F, 0.0F).endVertex();
                        buffer.vertex(matrix4f, size, size, size).uv(0.0F, 1.0F).endVertex();
                        buffer.vertex(matrix4f, -size, size, size).uv(1.0F, 1.0F).endVertex();
                        buffer.vertex(matrix4f, -size, -size, size).uv(1.0F, 0.0F).endVertex();
                        break;
                    case 4: // East (+X)
                        buffer.vertex(matrix4f, size, -size, -size).uv(0.0F, 0.0F).endVertex();
                        buffer.vertex(matrix4f, size, size, -size).uv(0.0F, 1.0F).endVertex();
                        buffer.vertex(matrix4f, size, size, size).uv(1.0F, 1.0F).endVertex();
                        buffer.vertex(matrix4f, size, -size, size).uv(1.0F, 0.0F).endVertex();
                        break;
                    case 5: // West (-X)
                    default:
                        buffer.vertex(matrix4f, -size, -size, size).uv(0.0F, 0.0F).endVertex();
                        buffer.vertex(matrix4f, -size, size, size).uv(0.0F, 1.0F).endVertex();
                        buffer.vertex(matrix4f, -size, size, -size).uv(1.0F, 1.0F).endVertex();
                        buffer.vertex(matrix4f, -size, -size, -size).uv(1.0F, 0.0F).endVertex();
                        break;
                }
                tessellator.end();
                matrixStack.popPose();
            }

            RenderSystem.enableCull();
            RenderSystem.disableBlend();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
        }
    }
}