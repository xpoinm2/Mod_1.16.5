package com.example.examplemod.client.render;

import com.example.examplemod.ExampleMod;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.world.DimensionRenderInfo;
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

    private static final ResourceLocation SKYBOX_TOP = new ResourceLocation(ExampleMod.MODID,
            "textures/environment/skybox_top.png");
    private static final ResourceLocation SKYBOX_BOTTOM = new ResourceLocation(ExampleMod.MODID,
            "textures/environment/skybox_bottom.png");
    private static final ResourceLocation SKYBOX_NORTH = new ResourceLocation(ExampleMod.MODID,
            "textures/environment/skybox_back.png");
    private static final ResourceLocation SKYBOX_SOUTH = new ResourceLocation(ExampleMod.MODID,
            "textures/environment/skybox_front.png");
    private static final ResourceLocation SKYBOX_EAST = new ResourceLocation(ExampleMod.MODID,
            "textures/environment/skybox_right.png");
    private static final ResourceLocation SKYBOX_WEST = new ResourceLocation(ExampleMod.MODID,
            "textures/environment/skybox_left.png");

    /**
     * Order of faces matches the quad cases below: top, bottom, north (-Z / back), south (+Z / front),
     * east (+X / right), west (-X / left).
     */
    private static final ResourceLocation[] SKYBOX_FACES = {
            SKYBOX_TOP,
            SKYBOX_BOTTOM,
            SKYBOX_NORTH,
            SKYBOX_SOUTH,
            SKYBOX_EAST,
            SKYBOX_WEST
    };

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

            RenderSystem.enableCull();
            RenderSystem.disableBlend();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
        }
    }
}