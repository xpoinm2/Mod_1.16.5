package com.example.examplemod.client.render;

import com.example.examplemod.ExampleMod;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.opengl.GL11;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class WetBlockDropletRenderer {
    private static final ResourceLocation DROPLET_TEXTURE =
            new ResourceLocation(ExampleMod.MODID, "textures/environment/wet_drops_overlay.png");
    private static final int RENDER_RADIUS = 6;
    private static final double FACE_OFFSET = 0.002D;

    private WetBlockDropletRenderer() {
    }

    @SubscribeEvent
    public static void onRenderWorldLast(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            return;
        }

        MatrixStack stack = event.getMatrixStack();
        Vector3d camPos = mc.gameRenderer.getMainCamera().getPosition();
        World world = mc.level;
        BlockPos center = mc.player.blockPosition();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        mc.getTextureManager().bind(DROPLET_TEXTURE);

        stack.pushPose();
        stack.translate(-camPos.x, -camPos.y, -camPos.z);

        Matrix4f matrix = stack.last().pose();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int x = -RENDER_RADIUS; x <= RENDER_RADIUS; x++) {
            for (int y = -RENDER_RADIUS; y <= RENDER_RADIUS; y++) {
                for (int z = -RENDER_RADIUS; z <= RENDER_RADIUS; z++) {
                    mutable.set(center.getX() + x, center.getY() + y, center.getZ() + z);
                    BlockState state = world.getBlockState(mutable);
                    if (state.isAir(world, mutable) || !state.getMaterial().isSolid()) {
                        continue;
                    }

                    if (!isWetByEnvironment(world, mutable)) {
                        continue;
                    }

                    for (Direction dir : Direction.values()) {
                        BlockPos neighbor = mutable.relative(dir);
                        BlockState neighborState = world.getBlockState(neighbor);
                        if (neighborState.getMaterial().isSolid()) {
                            continue;
                        }
                        addFace(buffer, matrix, mutable, dir);
                    }
                }
            }
        }

        tessellator.end();
        stack.popPose();

        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    private static boolean isWetByEnvironment(World world, BlockPos pos) {
        if (world.getFluidState(pos).is(FluidTags.WATER)) {
            return false;
        }

        if (world.isRainingAt(pos.above())) {
            return true;
        }

        for (Direction direction : Direction.values()) {
            if (world.getFluidState(pos.relative(direction)).is(FluidTags.WATER)) {
                return true;
            }
        }

        return false;
    }

    private static void addFace(BufferBuilder buffer, Matrix4f matrix, BlockPos pos, Direction side) {
        float x0 = pos.getX();
        float y0 = pos.getY();
        float z0 = pos.getZ();
        float x1 = x0 + 1.0F;
        float y1 = y0 + 1.0F;
        float z1 = z0 + 1.0F;
        int alpha = 155;

        switch (side) {
            case DOWN:
                y0 -= FACE_OFFSET;
                vertex(buffer, matrix, x0, y0, z1, 0, 1, alpha);
                vertex(buffer, matrix, x1, y0, z1, 1, 1, alpha);
                vertex(buffer, matrix, x1, y0, z0, 1, 0, alpha);
                vertex(buffer, matrix, x0, y0, z0, 0, 0, alpha);
                break;
            case UP:
                y1 += FACE_OFFSET;
                vertex(buffer, matrix, x0, y1, z0, 0, 0, alpha);
                vertex(buffer, matrix, x1, y1, z0, 1, 0, alpha);
                vertex(buffer, matrix, x1, y1, z1, 1, 1, alpha);
                vertex(buffer, matrix, x0, y1, z1, 0, 1, alpha);
                break;
            case NORTH:
                z0 -= FACE_OFFSET;
                vertex(buffer, matrix, x1, y0, z0, 1, 1, alpha);
                vertex(buffer, matrix, x0, y0, z0, 0, 1, alpha);
                vertex(buffer, matrix, x0, y1, z0, 0, 0, alpha);
                vertex(buffer, matrix, x1, y1, z0, 1, 0, alpha);
                break;
            case SOUTH:
                z1 += FACE_OFFSET;
                vertex(buffer, matrix, x0, y0, z1, 0, 1, alpha);
                vertex(buffer, matrix, x1, y0, z1, 1, 1, alpha);
                vertex(buffer, matrix, x1, y1, z1, 1, 0, alpha);
                vertex(buffer, matrix, x0, y1, z1, 0, 0, alpha);
                break;
            case WEST:
                x0 -= FACE_OFFSET;
                vertex(buffer, matrix, x0, y0, z0, 0, 1, alpha);
                vertex(buffer, matrix, x0, y0, z1, 1, 1, alpha);
                vertex(buffer, matrix, x0, y1, z1, 1, 0, alpha);
                vertex(buffer, matrix, x0, y1, z0, 0, 0, alpha);
                break;
            case EAST:
                x1 += FACE_OFFSET;
                vertex(buffer, matrix, x1, y0, z1, 1, 1, alpha);
                vertex(buffer, matrix, x1, y0, z0, 0, 1, alpha);
                vertex(buffer, matrix, x1, y1, z0, 0, 0, alpha);
                vertex(buffer, matrix, x1, y1, z1, 1, 0, alpha);
                break;
        }
    }

    private static void vertex(BufferBuilder buffer, Matrix4f matrix,
                               float x, float y, float z,
                               float u, float v,
                               int alpha) {
        buffer.vertex(matrix, x, y, z)
                .uv(u, v)
                .color(255, 255, 255, alpha)
                .endVertex();
    }
}
