package com.example.examplemod.client.model;

import com.example.examplemod.entity.BeaverEntity;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class BeaverModel<T extends BeaverEntity> extends SegmentedModel<T> {
    public final ModelRenderer body;
    public final ModelRenderer head;
    public final ModelRenderer tail;
    public final ModelRenderer legFL;
    public final ModelRenderer legFR;
    public final ModelRenderer legBL;
    public final ModelRenderer legBR;

    public BeaverModel() {
        this.texWidth = 64;
        this.texHeight = 64;

        body = new ModelRenderer(this, 0, 16);
        body.setPos(0.0F, 16.0F, 0.0F);
        body.addBox(-4.0F, -3.0F, -6.0F, 8, 7, 12);

        head = new ModelRenderer(this, 0, 0);
        head.setPos(0.0F, -1.0F, -6.0F);
        head.addBox(-3.0F, -2.0F, -6.0F, 6, 6, 6);

        ModelRenderer snout = new ModelRenderer(this, 28, 0);
        snout.addBox(-2.0F, 0.0F, -8.0F, 4, 3, 2);
        head.addChild(snout);

        ModelRenderer teeth = new ModelRenderer(this, 36, 0);
        teeth.addBox(-1.0F, 2.0F, -9.0F, 2, 2, 1);
        head.addChild(teeth);
        body.addChild(head);

        tail = new ModelRenderer(this, 0, 35);
        tail.setPos(0.0F, 1.0F, 6.0F);
        tail.addBox(-2.0F, 0.0F, 0.0F, 4, 1, 8);
        body.addChild(tail);

        legFL = new ModelRenderer(this, 40, 16);
        legFL.setPos(2.0F, 3.0F, -3.5F);
        legFL.addBox(-1.0F, 0.0F, -1.0F, 2, 5, 2);
        body.addChild(legFL);

        legFR = new ModelRenderer(this, 32, 16);
        legFR.setPos(-2.0F, 3.0F, -3.5F);
        legFR.addBox(-1.0F, 0.0F, -1.0F, 2, 5, 2);
        body.addChild(legFR);

        legBL = new ModelRenderer(this, 40, 24);
        legBL.setPos(2.0F, 3.0F, 3.5F);
        legBL.addBox(-1.0F, 0.0F, -1.0F, 2, 5, 2);
        body.addChild(legBL);

        legBR = new ModelRenderer(this, 32, 24);
        legBR.setPos(-2.0F, 3.0F, 3.5F);
        legBR.addBox(-1.0F, 0.0F, -1.0F, 2, 5, 2);
        body.addChild(legBR);
    }

    @Override
    public Iterable<ModelRenderer> parts() {
        return ImmutableList.of(body);
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay,
                               float red, float green, float blue, float alpha) {
        body.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        head.xRot = headPitch * ((float) Math.PI / 180F);

        float swing = limbSwing;
        float amount = limbSwingAmount;
        legFL.xRot = MathHelper.cos(swing * 0.6662F) * 1.2F * amount;
        legBR.xRot = MathHelper.cos(swing * 0.6662F) * 1.2F * amount;
        legFR.xRot = MathHelper.cos(swing * 0.6662F + (float) Math.PI) * 1.2F * amount;
        legBL.xRot = MathHelper.cos(swing * 0.6662F + (float) Math.PI) * 1.2F * amount;

        body.xRot = 0.02F * MathHelper.cos(ageInTicks * 0.2F);
        tail.zRot = 0.15F * MathHelper.cos(ageInTicks * 0.2F);

        if (entity.isInWater()) {
            body.xRot = 0.25F;
            tail.xRot = 0.3F * MathHelper.cos(ageInTicks * 0.5F);
            legFL.zRot = 0.6F * MathHelper.cos(ageInTicks * 0.7F);
            legFR.zRot = -legFL.zRot;
            legBL.zRot = -legFL.zRot;
            legBR.zRot = legFL.zRot;
        } else {
            tail.xRot = 0.0F;
            legFL.zRot = 0.0F;
            legFR.zRot = 0.0F;
            legBL.zRot = 0.0F;
            legBR.zRot = 0.0F;
        }
    }
}