package com.example.examplemod.world;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.ModBlocks;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.blockplacer.SimpleBlockPlacer;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.BlockClusterFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.placement.ChanceConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

/**
 * Registry for world generation configured features.
 */
public class WorldGenRegistry {
    public static final DeferredRegister<ConfiguredFeature<?, ?>> FEATURES =
            DeferredRegister.create(Registry.CONFIGURED_FEATURE_REGISTRY, ExampleMod.MODID);

    public static final RegistryObject<ConfiguredFeature<?, ?>> ANGELICA_PATCH = FEATURES.register("angelica_patch", () -> {
        BlockClusterFeatureConfig config = new BlockClusterFeatureConfig.Builder(
                new SimpleBlockStateProvider(ModBlocks.ANGELICA.get().defaultBlockState()),
                SimpleBlockPlacer.INSTANCE).tries(8).build();
        return Feature.RANDOM_PATCH.configured(config)
                .decorated(Placement.CHANCE.configured(new ChanceConfig(1)));
    });

    public static final RegistryObject<ConfiguredFeature<?, ?>> ELDERBERRY_PATCH = FEATURES.register("elderberry_patch", () -> {
        BlockClusterFeatureConfig config = new BlockClusterFeatureConfig.Builder(
                new SimpleBlockStateProvider(ModBlocks.ELDERBERRY_BUSH.get().defaultBlockState()),
                SimpleBlockPlacer.INSTANCE).tries(8).build();
        return Feature.RANDOM_PATCH.configured(config)
                .decorated(Placement.CHANCE.configured(new ChanceConfig(3)));
    });

    public static final RegistryObject<ConfiguredFeature<?, ?>> CRANBERRY_PATCH = FEATURES.register("cranberry_patch", () -> {
        BlockClusterFeatureConfig config = new BlockClusterFeatureConfig.Builder(
                new SimpleBlockStateProvider(ModBlocks.CRANBERRY_BUSH.get().defaultBlockState()),
                SimpleBlockPlacer.INSTANCE).tries(8).build();
        return Feature.RANDOM_PATCH.configured(config)
                .decorated(Placement.CHANCE.configured(new ChanceConfig(3)));
    });

    public static final RegistryObject<ConfiguredFeature<?, ?>> RASPBERRY_PATCH = FEATURES.register("raspberry_patch", () -> {
        BlockClusterFeatureConfig config = new BlockClusterFeatureConfig.Builder(
                new SimpleBlockStateProvider(ModBlocks.RASPBERRY_BUSH.get().defaultBlockState()),
                SimpleBlockPlacer.INSTANCE).tries(8).build();
        return Feature.RANDOM_PATCH.configured(config)
                .decorated(Placement.CHANCE.configured(new ChanceConfig(3)));
    });

    public static final RegistryObject<ConfiguredFeature<?, ?>> HORSERADISH_PATCH = FEATURES.register("horseradish_patch", () -> {
        BlockClusterFeatureConfig config = new BlockClusterFeatureConfig.Builder(
                new SimpleBlockStateProvider(ModBlocks.HORSERADISH_PLANT.get().defaultBlockState()),
                SimpleBlockPlacer.INSTANCE).tries(8).build();
        return Feature.RANDOM_PATCH.configured(config)
                .decorated(Placement.CHANCE.configured(new ChanceConfig(1)));
    });

    public static final RegistryObject<ConfiguredFeature<?, ?>> GINGER_PATCH = FEATURES.register("ginger_patch", () -> {
        BlockClusterFeatureConfig config = new BlockClusterFeatureConfig.Builder(
                new SimpleBlockStateProvider(ModBlocks.GINGER_PLANT.get().defaultBlockState()),
                SimpleBlockPlacer.INSTANCE).tries(8).build();
        return Feature.RANDOM_PATCH.configured(config)
                .decorated(Placement.CHANCE.configured(new ChanceConfig(1)));
    });

    public static final RegistryObject<ConfiguredFeature<?, ?>> MUHOMOR_PATCH = FEATURES.register("muhomor_patch", () -> {
        BlockClusterFeatureConfig config = new BlockClusterFeatureConfig.Builder(
                new SimpleBlockStateProvider(ModBlocks.MUHOMOR.get().defaultBlockState()),
                SimpleBlockPlacer.INSTANCE).tries(8).build();
        return Feature.RANDOM_PATCH.configured(config)
                .decorated(Placement.CHANCE.configured(new ChanceConfig(2)));
    });

    public static void register(IEventBus bus) {
        FEATURES.register(bus);
    }
}