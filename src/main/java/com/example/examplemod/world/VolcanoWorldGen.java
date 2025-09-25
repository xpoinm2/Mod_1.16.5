package com.example.examplemod.world;

import com.example.examplemod.ExampleMod;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VolcanoWorldGen {

    @SubscribeEvent
    public static void onBiomeLoad(BiomeLoadingEvent event) {
        boolean isMountainsByName = event.getName() != null &&
                (event.getName().getPath().contains("mountains") || event.getName().getPath().contains("mountain"));

        if (event.getCategory() == Biome.Category.EXTREME_HILLS || isMountainsByName) {
            ConfiguredFeature<?, ?> feature = WorldGenRegistry.VOLCANO;
            if (feature != null) {
                event.getGeneration().addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, feature);
            }
        }
    }
}