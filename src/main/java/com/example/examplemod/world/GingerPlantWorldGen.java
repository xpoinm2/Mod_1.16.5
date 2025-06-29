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
public class GingerPlantWorldGen {

    @SubscribeEvent
    public static void onBiomeLoad(BiomeLoadingEvent event) {
        Biome.Category cat = event.getCategory();
        if (cat == Biome.Category.JUNGLE) {
            ConfiguredFeature<?, ?> feature = WorldGenRegistry.GINGER_PATCH.get();
            event.getGeneration().addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, feature);
        }
        if (cat == Biome.Category.SAVANNA) {
            ConfiguredFeature<?, ?> feature = WorldGenRegistry.GINGER_PATCH.get();
            event.getGeneration().addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, feature);
        }
    }
}