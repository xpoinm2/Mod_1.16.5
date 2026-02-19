package com.example.examplemod.client.render;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.client.HurricaneClientState;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.client.ISkyRenderHandler;
import org.lwjgl.opengl.GL11;

public final class HurricaneSkyRenderer implements ISkyRenderHandler {
    /**
     * Place textures under:
     * assets/examplemod/textures/environment/hurricane_skybox_*.png
     */
    private static final ResourceLocation SKYBOX_TOP = new ResourceLocation(ExampleMod.MODID,
            "textures/environment/hurricane_skybox_top.png");
    private static final ResourceLocation SKYBOX_BOTTOM = new ResourceLocation(ExampleMod.MODID,
            "textures/environment/hurricane_skybox_bottom.png");
    private static final ResourceLocation SKYBOX_NORTH = new ResourceLocation(ExampleMod.MODID,
            "textures/environment/hurricane_skybox_back.png");
    private static final ResourceLocation SKYBOX_SOUTH = new ResourceLocation(ExampleMod.MODID,
            "textures/environment/hurricane_skybox_front.png");
    private static final ResourceLocation SKYBOX_EAST = new ResourceLocation(ExampleMod.MODID,
            "textures/environment/hurricane_skybox_right.png");
    private static final ResourceLocation SKYBOX_WEST = new ResourceLocation(ExampleMod.MODID,
            "textures/environment/hurricane_skybox_left.png");

    private static final ResourceLocation[] SKYBOX_FACES = {
            SKYBOX_TOP,
            SKYBOX_BOTTOM,
            SKYBOX_NORTH,
            SKYBOX_SOUTH,
            SKYBOX_EAST,
            SKYBOX_WEST
    };

    @Override
    public void render(int ticks, float partialTicks, MatrixStack matrixStack, ClientWorld world, Minecraft mc) {
        float intensity = HurricaneClientState.getIntensity();
        if (intensity <= 0.0F) {
            return;
        }

        ISkyRenderHandler originalSkyHandler = HurricaneSkyEffects.getOriginalSkyHandler();
        if (originalSkyHandler != null) {
            originalSkyHandler.render(ticks, partialTicks, matrixStack, world, mc);
        }

        float texturedSkyAlpha = intensity * intensity;
        if (texturedSkyAlpha <= 0.0F) {
            return;
        }

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, texturedSkyAlpha);
        RenderSystem.disableCull();
        RenderSystem.enableTexture();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();

        float size = 200.0F;
        for (int i = 0; i < SKYBOX_FACES.length; i++) {
            mc.getTextureManager().bind(SKYBOX_FACES[i]);
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
        }

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }

    private static float smoothStepRange(float value, float start, float end) {
        if (end <= start) {
            return value >= end ? 1.0F : 0.0F;
        }
        float t = clamp((value - start) / (end - start));
        return t * t * (3.0F - 2.0F * t);
    }

    private static float lerp(float start, float end, float delta) {
        return start + (end - start) * delta;
    }

    private static float clamp(float value) {
        return Math.max(0.0F, Math.min(1.0F, value));
    }
}
