package com.example.examplemod;

import com.example.examplemod.world.VolcanoFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.RegistryObject;

/**
 * Registry for custom world generation features.
 */
public class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(ForgeRegistries.FEATURES, ExampleMod.MODID);

    // Правильная регистрация фичи через RegistryObject
    public static final RegistryObject<Feature<NoFeatureConfig>> VOLCANO = FEATURES.register(
            "volcano", () -> new VolcanoFeature(NoFeatureConfig.CODEC)
    );

    public static void register(IEventBus bus) {
        FEATURES.register(bus);
    }
}