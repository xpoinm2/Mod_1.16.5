package com.example.examplemod.world;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.ModStructures;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.Structure;
import net.minecraft.world.gen.feature.StructureFeature;

/**
 * Holds configured versions of custom structures and registers them.
 */
public final class ModConfiguredStructures {
    private ModConfiguredStructures() {}

    public static StructureFeature<NoFeatureConfig, ? extends Structure<NoFeatureConfig>> CONFIGURED_VOLCANO;

    public static void register() {
        CONFIGURED_VOLCANO = ModStructures.VOLCANO.get().configured(NoFeatureConfig.INSTANCE);
        Registry<StructureFeature<?, ?>> registry = WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE;
        Registry.register(registry, new ResourceLocation(ExampleMod.MODID, "volcano"), CONFIGURED_VOLCANO);
        FlatGenerationSettings.STRUCTURE_FEATURES.put(ModStructures.VOLCANO.get(), CONFIGURED_VOLCANO);
    }
}