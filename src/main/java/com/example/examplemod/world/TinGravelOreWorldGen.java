package com.example.examplemod.world;

import com.example.examplemod.ExampleMod;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TinGravelOreWorldGen {

    @SubscribeEvent
    public static void onBiomeLoad(BiomeLoadingEvent event) {
        Biome.Category cat = event.getCategory();
        if (cat == Biome.Category.RIVER) {
            ConfiguredFeature<?, ?> feature = WorldGenRegistry.TIN_GRAVEL_ORE;
            event.getGeneration().addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, feature);
        }
    }
}
