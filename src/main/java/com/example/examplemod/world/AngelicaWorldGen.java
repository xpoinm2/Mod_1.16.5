package com.example.examplemod.world;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.world.WorldGenRegistry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AngelicaWorldGen {


    @SubscribeEvent
    public static void onBiomeLoad(BiomeLoadingEvent event) {
        if (event.getCategory() == Biome.Category.SWAMP) {
            ConfiguredFeature<?, ?> feature = WorldGenRegistry.ANGELICA_PATCH.get();
            event.getGeneration().addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, feature);
        }
    }
}