package com.example.examplemod.client.render;

import com.example.examplemod.ExampleMod;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.world.DimensionRenderInfo;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.IRenderHandler;
import org.lwjgl.opengl.GL11;

/**
 * Custom visual settings for the Heaven dimension.
 */
public class HeavenDimensionRenderInfo extends DimensionRenderInfo {

    private static final ResourceLocation HEAVEN_SKY = new ResourceLocation(ExampleMod.MODID,
            "textures/environment/heaven_sky.png");

    public HeavenDimensionRenderInfo() {
        super(Float.NaN, false, SkyType.NONE, false, true);
        this.setSkyRenderHandler(new HeavenSkyRenderer());
        this.setCloudRenderHandler((mc, level, ticks, partialTicks, matrixStack, cameraX, cameraY, cameraZ) -> {
            // Intentionally empty: clouds are disabled in Heaven.
        });
        this.setWeatherRenderHandler((ticks, partialTicks, level, mc, camera, matrixStack) -> {
            // No weather in Heaven.
        });
    }

    @Override
    public Vector3d getBrightnessDependentFogColor(Vector3d color, float sunHeight) {
        return new Vector3d(1.0D, 1.0D, 1.0D);
    }

    @Override
    public boolean isFoggyAt(int x, int z) {
        return false;
    }

    private static final class HeavenSkyRenderer implements IRenderHandler {

        @Override
        public void render(int ticks, float partialTicks, MatrixStack matrixStack, ClientWorld world, Minecraft mc) {
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableAlphaTest();
            RenderSystem.disableCull();
            RenderSystem.enableTexture();

            mc.getTextureManager().bind(HEAVEN_SKY);

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuilder();

            float size = 200.0F;
            for (int i = 0; i < 6; i++) {
                matrixStack.pushPose();
                applyFaceRotation(matrixStack, i);
                Matrix4f matrix4f = matrixStack.last().pose();
                buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
                buffer.vertex(matrix4f, -size, size, -size).uv(0.0F, 0.0F).endVertex();
                buffer.vertex(matrix4f, -size, size, size).uv(0.0F, 1.0F).endVertex();
                buffer.vertex(matrix4f, size, size, size).uv(1.0F, 1.0F).endVertex();
                buffer.vertex(matrix4f, size, size, -size).uv(1.0F, 0.0F).endVertex();
                tessellator.end();
                matrixStack.popPose();
            }

            RenderSystem.enableCull();
            RenderSystem.disableBlend();
            RenderSystem.enableAlphaTest();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
        }

        private void applyFaceRotation(MatrixStack stack, int face) {
            switch (face) {
                case 1:
                    stack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
                    break;
                case 2:
                    stack.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
                    break;
                case 3:
                    stack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
                    break;
                case 4:
                    stack.mulPose(Vector3f.YP.rotationDegrees(90.0F));
                    break;
                case 5:
                    stack.mulPose(Vector3f.YP.rotationDegrees(-90.0F));
                    break;
                default:
                    break;
            }
        }
    }
}