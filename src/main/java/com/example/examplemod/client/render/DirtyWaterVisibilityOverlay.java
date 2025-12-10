package com.example.examplemod.client.render;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.ModFluids;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.opengl.GL11;

/**
 * Applies a murky, darkened overlay when the player's eyes are submerged in dirty water.
 */
@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class DirtyWaterVisibilityOverlay {

    private DirtyWaterVisibilityOverlay() {
    }

    @SubscribeEvent
    public static void onRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) {
            return;
        }

        Vec3 eyePosition = minecraft.player.getEyePosition(1.0F);
        BlockPos eyeBlockPos = new BlockPos(eyePosition);
        FluidState fluidState = minecraft.level.getFluidState(eyeBlockPos);
        if (!fluidState.getFluid().isSame(ModFluids.DIRTY_WATER.get())) {
            return;
        }

        MatrixStack matrixStack = event.getMatrixStack();

        RenderSystem.disableDepthTest();
        RenderSystem.disableAlphaTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        RenderSystem.disableTexture();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        int width = minecraft.getWindow().getGuiScaledWidth();
        int height = minecraft.getWindow().getGuiScaledHeight();
        Matrix4f matrix = matrixStack.last().pose();

        float red = 0.08f;
        float green = 0.1f;
        float blue = 0.12f;
        float alpha = 0.65f;

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

