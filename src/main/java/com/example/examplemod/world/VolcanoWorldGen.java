package com.example.examplemod.world;

import com.example.examplemod.ExampleMod;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VolcanoWorldGen {

    @SubscribeEvent
    public static void onBiomeLoad(BiomeLoadingEvent event) {
        ResourceLocation biomeName = event.getName();
        // Добавляем фичу только в биомы, у которых в имени есть "mountains"
        if (biomeName != null && biomeName.getPath().contains("mountains")) {
            ConfiguredFeature<?, ?> feature = WorldGenRegistry.VOLCANO;
            // Подойдёт и SURFACE_STRUCTURES, но можно попробовать LOCAL_MODIFICATIONS, если что
            event.getGeneration().addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, feature);
        }
    }
}