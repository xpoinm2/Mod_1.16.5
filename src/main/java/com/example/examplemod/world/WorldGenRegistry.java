package com.example.examplemod.world;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.ModBlocks;
import com.example.examplemod.world.ModFeatures;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.blockplacer.SimpleBlockPlacer;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.BlockClusterFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.placement.ChanceConfig;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.Placement;

/**
 * Registry for configured features.
 * Вызови WorldGenRegistry.register() в FMLCommonSetupEvent через event.enqueueWork(...).
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
    public static ConfiguredFeature<?, ?> TIN_GRAVEL_ORE;
    public static ConfiguredFeature<?, ?> TIN_ORE;
    public static ConfiguredFeature<?, ?> GOLD_GRAVEL_ORE;
    public static ConfiguredFeature<?, ?> BASALT_MOUNTAIN;
    public static ConfiguredFeature<?, ?> DESERT_PYRAMID;

    public static void register() {
        ANGELICA_PATCH = register("angelica_patch", 1, ModBlocks.ANGELICA.get().defaultBlockState());
        ELDERBERRY_PATCH = register("elderberry_patch", 3, ModBlocks.ELDERBERRY_BUSH.get().defaultBlockState());
        CRANBERRY_PATCH = register("cranberry_patch", 3, ModBlocks.CRANBERRY_BUSH.get().defaultBlockState());
        RASPBERRY_PATCH = register("raspberry_patch", 3, ModBlocks.RASPBERRY_BUSH.get().defaultBlockState());
        HORSERADISH_PATCH = register("horseradish_patch", 1, ModBlocks.HORSERADISH_PLANT.get().defaultBlockState());
        GINGER_PATCH = register("ginger_patch", 1, ModBlocks.GINGER_PLANT.get().defaultBlockState());
        FLAX_PATCH = register("flax_patch", 1, ModBlocks.FLAX_PLANT.get().defaultBlockState());
        PYRITE_ORE = registerPyriteOre();
        TIN_GRAVEL_ORE = registerTinGravelOre();
        TIN_ORE = registerTinOre();
        GOLD_GRAVEL_ORE = registerGoldGravelOre();
        BASALT_MOUNTAIN = registerBasaltMountain();
        DESERT_PYRAMID = registerDesertPyramid();
    }

    private static ConfiguredFeature<?, ?> register(String name, int chance, net.minecraft.block.BlockState state) {
        BlockClusterFeatureConfig config = new BlockClusterFeatureConfig.Builder(
                new SimpleBlockStateProvider(state), SimpleBlockPlacer.INSTANCE
        ).tries(8).build();
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
                .configured(config).range(64).squared().count(6);
        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE,
                new ResourceLocation(ExampleMod.MODID, "pyrite_ore"), feature);
        return feature;
    }

    private static ConfiguredFeature<?, ?> registerTinGravelOre() {
        net.minecraft.world.gen.feature.OreFeatureConfig config =
                new net.minecraft.world.gen.feature.OreFeatureConfig(
                        new net.minecraft.world.gen.feature.template.BlockMatchRuleTest(net.minecraft.block.Blocks.GRAVEL),
                        ModBlocks.TIN_GRAVEL_ORE.get().defaultBlockState(),
                        1);
        ConfiguredFeature<?, ?> feature = net.minecraft.world.gen.feature.Feature.ORE
                .configured(config).range(64).squared().count(4); // 3-5 blocks per chunk
        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE,
                new ResourceLocation(ExampleMod.MODID, "tin_gravel_ore"), feature);
        return feature;
    }

    private static ConfiguredFeature<?, ?> registerTinOre() {
        net.minecraft.world.gen.feature.OreFeatureConfig config =
                new net.minecraft.world.gen.feature.OreFeatureConfig(
                        net.minecraft.world.gen.feature.OreFeatureConfig.FillerBlockType.NATURAL_STONE,
                        ModBlocks.TIN_ORE.get().defaultBlockState(),
                        4);
        ConfiguredFeature<?, ?> feature = net.minecraft.world.gen.feature.Feature.ORE
                .configured(config).range(40).squared().count(6); // Y=10-50, ~1.5x less than iron
        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE,
                new ResourceLocation(ExampleMod.MODID, "tin_ore"), feature);
        return feature;
    }

    private static ConfiguredFeature<?, ?> registerGoldGravelOre() {
        net.minecraft.world.gen.feature.OreFeatureConfig config =
                new net.minecraft.world.gen.feature.OreFeatureConfig(
                        new net.minecraft.world.gen.feature.template.BlockMatchRuleTest(net.minecraft.block.Blocks.GRAVEL),
                        ModBlocks.GOLD_GRAVEL_ORE.get().defaultBlockState(),
                        1);
        ConfiguredFeature<?, ?> feature = net.minecraft.world.gen.feature.Feature.ORE
                .configured(config).range(64).squared().count(4); // 3-5 blocks per chunk
        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE,
                new ResourceLocation(ExampleMod.MODID, "gold_gravel_ore"), feature);
        return feature;
    }

    private static ConfiguredFeature<?, ?> registerBasaltMountain() {
        Feature<NoFeatureConfig> feature = ModFeatures.BASALT_MOUNTAIN.get();
        ConfiguredFeature<?, ?> configured = feature.configured(NoFeatureConfig.INSTANCE);
        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE,
                new ResourceLocation(ExampleMod.MODID, "basalt_mountain"), configured);
        return configured;
    }

    private static ConfiguredFeature<?, ?> registerDesertPyramid() {
        Feature<NoFeatureConfig> feature = ModFeatures.DESERT_PYRAMID.get();
        ConfiguredFeature<?, ?> configured = feature.configured(NoFeatureConfig.INSTANCE)
                .decorated(Placement.CHANCE.configured(new ChanceConfig(128)))
                .decorated(Placement.SQUARE.configured(NoPlacementConfig.INSTANCE))
                .decorated(Placement.HEIGHTMAP.configured(NoPlacementConfig.INSTANCE));
        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE,
                new ResourceLocation(ExampleMod.MODID, "desert_pyramid"), configured);
        return configured;
    }
}