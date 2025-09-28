package com.example.examplemod.client.render;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.entity.BeaverEntity;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.PigModel;
import net.minecraft.util.ResourceLocation;

public class BeaverRenderer extends MobRenderer<BeaverEntity, PigModel<BeaverEntity>> {

    private static final ResourceLocation BEAVER_TEXTURE =
            new ResourceLocation(ExampleMod.MODID, "textures/entity/beaver.png");

    public BeaverRenderer(EntityRendererManager renderManager) {
        super(renderManager, new PigModel<>(0.0f), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(BeaverEntity entity) {
        return BEAVER_TEXTURE;
    }
}