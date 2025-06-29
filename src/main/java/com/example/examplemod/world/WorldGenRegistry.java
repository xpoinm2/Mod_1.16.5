package com.example.examplemod.world;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.ModBlocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.blockplacer.SimpleBlockPlacer;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.BlockClusterFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.placement.ChanceConfig;
import net.minecraft.world.gen.placement.Placement;

/**
 * Registry for world generation configured features.
 */
public class WorldGenRegistry {

    public static ConfiguredFeature<?, ?> ANGELICA_PATCH;
    public static ConfiguredFeature<?, ?> ELDERBERRY_PATCH;
    public static ConfiguredFeature<?, ?> CRANBERRY_PATCH;
    public static ConfiguredFeature<?, ?> RASPBERRY_PATCH;
    public static ConfiguredFeature<?, ?> HORSERADISH_PATCH;
    public static ConfiguredFeature<?, ?> GINGER_PATCH;
    public static ConfiguredFeature<?, ?> MUHOMOR_PATCH;

    /**
     * Registers all configured features to the world generation registry.
     * Should be called during common setup.
     */
    public static void register() {
        ANGELICA_PATCH = register("angelica_patch", 1, ModBlocks.ANGELICA.get().defaultBlockState());
        ELDERBERRY_PATCH = register("elderberry_patch", 3, ModBlocks.ELDERBERRY_BUSH.get().defaultBlockState());
        CRANBERRY_PATCH = register("cranberry_patch", 3, ModBlocks.CRANBERRY_BUSH.get().defaultBlockState());
        RASPBERRY_PATCH = register("raspberry_patch", 3, ModBlocks.RASPBERRY_BUSH.get().defaultBlockState());
        HORSERADISH_PATCH = register("horseradish_patch", 1, ModBlocks.HORSERADISH_PLANT.get().defaultBlockState());
        GINGER_PATCH = register("ginger_patch", 1, ModBlocks.GINGER_PLANT.get().defaultBlockState());
        MUHOMOR_PATCH = register("muhomor_patch", 2, ModBlocks.MUHOMOR.get().defaultBlockState());
    }

    private static ConfiguredFeature<?, ?> register(String name, int chance, net.minecraft.block.BlockState state) {
        BlockClusterFeatureConfig config = new BlockClusterFeatureConfig.Builder(
                new SimpleBlockStateProvider(state),
                SimpleBlockPlacer.INSTANCE).tries(8).build();
        ConfiguredFeature<?, ?> feature = Feature.RANDOM_PATCH.configured(config)
                .decorated(Placement.CHANCE.configured(new ChanceConfig(chance)));
        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE,
                new ResourceLocation(ExampleMod.MODID, name), feature);
        return feature;
    }
}