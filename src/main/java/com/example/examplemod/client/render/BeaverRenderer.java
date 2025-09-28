package com.example.examplemod.client.render;

import com.example.examplemod.client.model.BeaverModelGeo;
import com.example.examplemod.entity.BeaverEntity;

import net.minecraft.client.renderer.entity.EntityRendererManager;

import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class BeaverRenderer extends GeoEntityRenderer<BeaverEntity> {

    public BeaverRenderer(EntityRendererManager renderManager) {
        super(renderManager, new BeaverModelGeo());
        this.shadowRadius = 0.4F;
    }
}