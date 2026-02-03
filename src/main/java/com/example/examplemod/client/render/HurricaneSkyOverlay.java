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
 * Darkens the sky view during custom hurricane weather (no rain).
 */
@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class HurricaneSkyOverlay {
    private static final float OVERLAY_STRENGTH = 0.35f;

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

        float red = 0.45f;
        float green = 0.45f;
        float blue = 0.5f;
        float alpha = OVERLAY_STRENGTH;

        buffer.vertex(matrix, 0.0f, height, -90.0f).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, width, height, -90.0f).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, width, 0.0f, -90.0f).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, 0.0f, 0.0f, -90.0f).color(red, green, blue, alpha).endVertex();

        tessellator.end();

        RenderSystem.enableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableDepthTest();
    }
}
