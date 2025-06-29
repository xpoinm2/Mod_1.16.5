package com.example.examplemod.world;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.world.WorldGenRegistry;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
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

    @SubscribeEvent
    public static void onBiomeLoad(BiomeLoadingEvent event) {
        ResourceLocation name = event.getName();
        Biome.Category cat = event.getCategory();
        if (name == null) return;
        if (name.equals(Biomes.BIRCH_FOREST.getRegistryName()) ||
                name.equals(Biomes.DARK_FOREST.getRegistryName()) ||
                name.equals(Biomes.FLOWER_FOREST.getRegistryName()) ||
                cat == Biome.Category.TAIGA ||
                cat == Biome.Category.MUSHROOM) {
            ConfiguredFeature<?, ?> feature = WorldGenRegistry.MUHOMOR_PATCH;
            event.getGeneration().addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, feature);
        }
    }
}