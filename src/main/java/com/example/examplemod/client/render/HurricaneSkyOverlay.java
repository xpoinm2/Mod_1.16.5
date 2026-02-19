package com.example.examplemod.client.render;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.client.HurricaneClientState;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.opengl.GL11;

/**
 * Darkens only the upper sky region during custom hurricane weather (no rain).
 */
@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class HurricaneSkyOverlay {
    private static final float SKY_TOP_ALPHA = 0.65f;
    private static final float SKY_BOTTOM_ALPHA = 0.18f;
    private static final float SKY_DARKEN_HEIGHT_RATIO = 0.65f;

    private HurricaneSkyOverlay() {
    }

    @SubscribeEvent
    public static void onPreRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
        if (HurricaneSkyEffects.isCustomSkyActive()) {
            return;
        }

        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        float intensity = HurricaneClientState.getIntensity();
        if (intensity <= 0.0F) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }

        MatrixStack matrixStack = event.getMatrixStack();

        RenderSystem.disableDepthTest();
        RenderSystem.disableAlphaTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        int width = minecraft.getWindow().getGuiScaledWidth();
        int height = minecraft.getWindow().getGuiScaledHeight();
        Matrix4f matrix = matrixStack.last().pose();

        float grayPhase = smoothStepRange(intensity, 0.0F, 0.6F);
        float contrastPhase = smoothStepRange(intensity, 0.45F, 1.0F);

        float red = lerp(0.34F, 0.11F, grayPhase);
        float green = lerp(0.38F, 0.13F, grayPhase);
        float blue = lerp(0.45F, 0.16F, grayPhase);

        float contrastDarken = lerp(1.0F, 0.75F, contrastPhase);
        red *= contrastDarken;
        green *= contrastDarken;
        blue *= contrastDarken;
        float horizonY = height * SKY_DARKEN_HEIGHT_RATIO;

        float topAlpha = SKY_TOP_ALPHA * (0.6F * grayPhase + 0.4F * contrastPhase);
        float bottomAlpha = SKY_BOTTOM_ALPHA * contrastPhase;

        buffer.vertex(matrix, 0.0f, horizonY, -90.0f).color(red, green, blue, bottomAlpha).endVertex();
        buffer.vertex(matrix, width, horizonY, -90.0f).color(red, green, blue, bottomAlpha).endVertex();
        buffer.vertex(matrix, width, 0.0f, -90.0f).color(red, green, blue, topAlpha).endVertex();
        buffer.vertex(matrix, 0.0f, 0.0f, -90.0f).color(red, green, blue, topAlpha).endVertex();

        tessellator.end();

        RenderSystem.enableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
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
