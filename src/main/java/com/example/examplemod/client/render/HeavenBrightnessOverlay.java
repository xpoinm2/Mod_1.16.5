package com.example.examplemod.client.render;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.world.heaven.HeavenManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.opengl.GL11;

/**
 * Applies an additive brightness overlay while the player is inside the Heaven dimension.
 */
@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class HeavenBrightnessOverlay {

    private static final float OVERLAY_STRENGTH = 0.45f;

    private HeavenBrightnessOverlay() {
    }

    @SubscribeEvent
    public static void onPreRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        ClientWorld world = minecraft.level;
        if (world == null || !world.dimension().equals(HeavenManager.HEAVEN_WORLD_KEY)) {
            return;
        }

        MatrixStack matrixStack = event.getMatrixStack();

        RenderSystem.disableDepthTest();
        RenderSystem.disableAlphaTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GL11.GL_ONE, GL11.GL_ONE, GL11.GL_ONE, GL11.GL_ZERO);
        RenderSystem.disableTexture();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        int width = minecraft.getWindow().getGuiScaledWidth();
        int height = minecraft.getWindow().getGuiScaledHeight();
        Matrix4f matrix = matrixStack.last().pose();

        float red = 0.9f * OVERLAY_STRENGTH;
        float green = 0.95f * OVERLAY_STRENGTH;
        float blue = 1.0f * OVERLAY_STRENGTH;
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