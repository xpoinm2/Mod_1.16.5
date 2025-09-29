package com.example.examplemod.client.model;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.entity.BeaverEntity;

import net.minecraft.util.ResourceLocation;

import software.bernie.geckolib3.model.AnimatedGeoModel;

public class BeaverModelGeo extends AnimatedGeoModel<BeaverEntity> {

    private static ResourceLocation rl(String path) {
        return new ResourceLocation(ExampleMod.MODID, path);
    }

    @Override
    public ResourceLocation getModelLocation(BeaverEntity object) {
        return rl("geo/beaver.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(BeaverEntity object) {
        return rl("textures/entity/beaver/beaver.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(BeaverEntity animatable) {
        return rl("animations/beaver.animation.json");
    }
}