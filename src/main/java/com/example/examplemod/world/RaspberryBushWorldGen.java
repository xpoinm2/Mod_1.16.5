package com.example.examplemod.world;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.world.WorldGenRegistry;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RaspberryBushWorldGen {

    @SubscribeEvent
    public static void onBiomeLoad(BiomeLoadingEvent event) {
        if (event.getCategory() == Biome.Category.PLAINS) {
            ConfiguredFeature<?, ?> feature = WorldGenRegistry.RASPBERRY_PATCH;
            event.getGeneration().addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, feature);
        }
        if (event.getCategory() == Biome.Category.EXTREME_HILLS) {
            ConfiguredFeature<?, ?> feature = WorldGenRegistry.RASPBERRY_PATCH;
            event.getGeneration().addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, feature);
        }
    }
}