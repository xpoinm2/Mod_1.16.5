package com.example.examplemod.client.model;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.entity.BeaverEntity;

import net.minecraft.util.ResourceLocation;

import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class BeaverModelGeo extends AnimatedGeoModel<BeaverEntity> {

    private static final double WALK_ANIMATION_THRESHOLD = 0.05D;

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

    public static void registerControllers(AnimationData data, BeaverEntity beaver) {
        data.addAnimationController(new AnimationController<>(beaver, "controller", 5, BeaverModelGeo::predicate));
    }

    private static <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if (!(event.getAnimatable() instanceof BeaverEntity)) {
            return PlayState.STOP;
        }

        BeaverEntity beaver = (BeaverEntity) event.getAnimatable();

        if (beaver.isInWater()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("beaver.swim", true));
            return PlayState.CONTINUE;
        }

        double horizontalSpeed = beaver.getDeltaMovement().horizontalDistanceSqr();
        if (horizontalSpeed > WALK_ANIMATION_THRESHOLD * WALK_ANIMATION_THRESHOLD) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("beaver.walk", true));
            return PlayState.CONTINUE;
        }

        event.getController().setAnimation(new AnimationBuilder().addAnimation("beaver.idle", true));
        return PlayState.CONTINUE;
    }
}