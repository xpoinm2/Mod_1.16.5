package com.example.examplemod;

import com.example.examplemod.world.VolcanoFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Registry for custom world generation features.
 */
public class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(ForgeRegistries.FEATURES, ExampleMod.MODID);

    // Volcano feature used in vanilla mountain biomes
    public static final Feature<NoFeatureConfig> VOLCANO_FEATURE =
            new VolcanoFeature(NoFeatureConfig.CODEC);
    public static final RegistryObject<Feature<NoFeatureConfig>> VOLCANO = FEATURES.register(
            "volcano", () -> VOLCANO_FEATURE);

    public static void register(IEventBus bus) {
        FEATURES.register(bus);
    }
}