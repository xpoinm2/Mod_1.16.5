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
import net.minecraftforge.client.ICloudRenderHandler;
import org.lwjgl.opengl.GL11;

public final class HurricaneCloudRenderer implements ICloudRenderHandler {
    /**
     * Place the cloud texture under:
     * assets/examplemod/textures/environment/hurricane_clouds.png
     */
    private static final ResourceLocation CLOUD_TEXTURE = new ResourceLocation(ExampleMod.MODID,
            "textures/environment/hurricane_clouds.png");
    private static final float CLOUD_SCROLL_SPEED = 0.03F;
    private static final float CLOUD_HEIGHT_OFFSET = 4.0F;
    private static final float CLOUD_RENDER_RADIUS = 256.0F;

    @Override
    public void render(int ticks, float partialTicks, MatrixStack matrixStack, ClientWorld world, Minecraft mc,
                       double cameraX, double cameraY, double cameraZ) {
        float cloudHeight = world.effects().getCloudHeight();
        if (Float.isNaN(cloudHeight)) {
            return;
        }

        float cloudAlpha = 0.85F * (1.0F - HurricaneClientState.getIntensity());
        if (cloudAlpha <= 0.0F) {
            return;
        }
        float cloudColor = 0.65F;
        float scroll = (ticks + partialTicks) * CLOUD_SCROLL_SPEED;

        double renderY = cloudHeight - cameraY + CLOUD_HEIGHT_OFFSET;

        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableTexture();
        mc.getTextureManager().bind(CLOUD_TEXTURE);

        matrixStack.pushPose();
        matrixStack.translate(-cameraX, renderY, -cameraZ);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        Matrix4f matrix = matrixStack.last().pose();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

        float minX = -CLOUD_RENDER_RADIUS;
        float maxX = CLOUD_RENDER_RADIUS;
        float minZ = -CLOUD_RENDER_RADIUS;
        float maxZ = CLOUD_RENDER_RADIUS;

        float u0 = (minX + scroll * 12.0F) / 256.0F;
        float u1 = (maxX + scroll * 12.0F) / 256.0F;
        float v0 = (minZ + scroll * 12.0F) / 256.0F;
        float v1 = (maxZ + scroll * 12.0F) / 256.0F;

        buffer.vertex(matrix, minX, 0.0F, maxZ).uv(u0, v1).color(cloudColor, cloudColor, cloudColor, cloudAlpha).endVertex();
        buffer.vertex(matrix, maxX, 0.0F, maxZ).uv(u1, v1).color(cloudColor, cloudColor, cloudColor, cloudAlpha).endVertex();
        buffer.vertex(matrix, maxX, 0.0F, minZ).uv(u1, v0).color(cloudColor, cloudColor, cloudColor, cloudAlpha).endVertex();
        buffer.vertex(matrix, minX, 0.0F, minZ).uv(u0, v0).color(cloudColor, cloudColor, cloudColor, cloudAlpha).endVertex();

        tessellator.end();
        matrixStack.popPose();

        RenderSystem.disableBlend();
        RenderSystem.enableCull();
    }
}
