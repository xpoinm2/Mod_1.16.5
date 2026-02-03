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
    private static final float SKY_BOTTOM_ALPHA = 0.0f;
    private static final float SKY_DARKEN_HEIGHT_RATIO = 0.65f;

    private HurricaneSkyOverlay() {
    }

    @SubscribeEvent
    public static void onPreRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        if (!HurricaneClientState.isActive()) {
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

        float red = 0.08f;
        float green = 0.1f;
        float blue = 0.15f;
        float horizonY = height * SKY_DARKEN_HEIGHT_RATIO;

        buffer.vertex(matrix, 0.0f, horizonY, -90.0f).color(red, green, blue, SKY_BOTTOM_ALPHA).endVertex();
        buffer.vertex(matrix, width, horizonY, -90.0f).color(red, green, blue, SKY_BOTTOM_ALPHA).endVertex();
        buffer.vertex(matrix, width, 0.0f, -90.0f).color(red, green, blue, SKY_TOP_ALPHA).endVertex();
        buffer.vertex(matrix, 0.0f, 0.0f, -90.0f).color(red, green, blue, SKY_TOP_ALPHA).endVertex();

        tessellator.end();

        RenderSystem.enableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableDepthTest();
    }
}
