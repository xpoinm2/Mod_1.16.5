package com.example.examplemod.world;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.world.feature.BasaltMountainFeature;
import com.example.examplemod.world.feature.DesertPyramidFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Holds vanilla-style feature registrations for the mod.
 */
public final class ModFeatures {
    private static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(ForgeRegistries.FEATURES, ExampleMod.MODID);

    public static final RegistryObject<Feature<NoFeatureConfig>> BASALT_MOUNTAIN = FEATURES.register(
            "basalt_mountain",
            () -> new BasaltMountainFeature(NoFeatureConfig.CODEC)
    );

    public static final RegistryObject<Feature<NoFeatureConfig>> DESERT_PYRAMID = FEATURES.register(
            "desert_pyramid",
            () -> new DesertPyramidFeature(NoFeatureConfig.CODEC)
    );

    private ModFeatures() {
    }

    public static void register(IEventBus bus) {
        FEATURES.register(bus);
    }
}