package com.example.examplemod.client.render;

import com.example.examplemod.client.model.BeaverModel;
import com.example.examplemod.entity.BeaverEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class BeaverRenderer extends MobRenderer<BeaverEntity, BeaverModel<BeaverEntity>> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("examplemod", "textures/entity/beaver/beaver.png");

    public BeaverRenderer(EntityRendererManager manager) {
        super(manager, new BeaverModel<>(), 0.4F);
    }

    @Override
    public ResourceLocation getTextureLocation(BeaverEntity entity) {
        return TEXTURE;
    }
}