package com.example.examplemod.client.render;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.block.BellowsBlock;
import com.example.examplemod.tileentity.BellowsTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.data.EmptyModelData;

import java.util.List;
import java.util.Random;

public class BellowsRenderer extends TileEntityRenderer<BellowsTileEntity> {
    private static final ResourceLocation TOP_MODEL = new ResourceLocation(ExampleMod.MODID, "block/bellows_top");

    public BellowsRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(BellowsTileEntity tile, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int light, int overlay) {
        if (tile.getLevel() == null) {
            return;
        }

        IBakedModel model = Minecraft.getInstance().getModelManager().getModel(TOP_MODEL);
        if (model == null) {
            return;
        }

        float offsetPixels = tile.getProgress() * 4.0F;
        float yOffset = (-offsetPixels / 16.0F) + 0.001F;

        matrix.pushPose();
        matrix.translate(0.5D, 0.0D, 0.5D);
        matrix.mulPose(Vector3f.YP.rotationDegrees(getRotationDegrees(tile.getBlockState().getValue(BellowsBlock.FACING))));
        matrix.translate(-0.5D, yOffset, -0.5D);

        IVertexBuilder builder = buffer.getBuffer(RenderType.cutout());
        Random random = new Random(42L);
        renderQuads(matrix, builder, model.getQuads(null, null, random, EmptyModelData.INSTANCE), light, overlay);

        for (Direction direction : Direction.values()) {
            random.setSeed(42L + direction.ordinal());
            renderQuads(matrix, builder, model.getQuads(null, direction, random, EmptyModelData.INSTANCE), light, overlay);
        }

        matrix.popPose();
    }

    private void renderQuads(MatrixStack matrix, IVertexBuilder builder, List<BakedQuad> quads, int light, int overlay) {
        for (BakedQuad quad : quads) {
            builder.putBulkData(matrix.last(), quad, 1.0F, 1.0F, 1.0F, light, overlay);
        }
    }

    private float getRotationDegrees(Direction facing) {
        switch (facing) {
            case SOUTH:
                return 180.0F;
            case WEST:
                return 270.0F;
            case EAST:
                return 90.0F;
            case NORTH:
            default:
                return 0.0F;
        }
    }
}
