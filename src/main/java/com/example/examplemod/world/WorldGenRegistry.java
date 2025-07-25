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
    public static ConfiguredFeature<?, ?> FLAX_PATCH;
    public static ConfiguredFeature<?, ?> PYRITE_ORE;


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
        FLAX_PATCH = register("flax_patch", 1, ModBlocks.FLAX_PLANT.get().defaultBlockState());
        PYRITE_ORE = registerPyriteOre();
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
    private static ConfiguredFeature<?, ?> registerPyriteOre() {
        net.minecraft.world.gen.feature.OreFeatureConfig config =
                new net.minecraft.world.gen.feature.OreFeatureConfig(
                        new net.minecraft.world.gen.feature.template.BlockMatchRuleTest(net.minecraft.block.Blocks.GRAVEL),
                        ModBlocks.PYRITE.get().defaultBlockState(),
                        4);
        ConfiguredFeature<?, ?> feature = net.minecraft.world.gen.feature.Feature.ORE
                .configured(config)
                .range(64).squared().count(6);
        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE,
                new ResourceLocation(ExampleMod.MODID, "pyrite_ore"), feature);
        return feature;
    }
}