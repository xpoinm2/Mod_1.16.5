package com.example.examplemod.world;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.ModBlocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.blockplacer.SimpleBlockPlacer;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.BlockClusterFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.placement.ChanceConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * World generation for fly agaric mushrooms.
 */
@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MuhomorWorldGen {
    private static ConfiguredFeature<?, ?> PATCH;

    private static void ensureFeature() {
        if (PATCH != null) return;
        BlockClusterFeatureConfig config = (new BlockClusterFeatureConfig.Builder(
                new SimpleBlockStateProvider(ModBlocks.MUHOMOR.get().defaultBlockState()),
                SimpleBlockPlacer.INSTANCE)).tries(8).build();
        PATCH = Feature.RANDOM_PATCH.configured(config)
                .decorated(Placement.CHANCE.configured(new ChanceConfig(2)));
        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, ExampleMod.MODID + ":muhomor_patch", PATCH);
    }

    @SubscribeEvent
    public static void onBiomeLoad(BiomeLoadingEvent event) {
        ensureFeature();
        ResourceLocation name = event.getName();
        Biome.Category cat = event.getCategory();
        if (name == null) return;
        if (name.equals(Biomes.BIRCH_FOREST.getRegistryName()) ||
                name.equals(Biomes.DARK_FOREST.getRegistryName()) ||
                name.equals(Biomes.FLOWER_FOREST.getRegistryName()) ||
                cat == Biome.Category.TAIGA ||
                cat == Biome.Category.MUSHROOM) {
            event.getGeneration().addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, PATCH);
        }
    }
}