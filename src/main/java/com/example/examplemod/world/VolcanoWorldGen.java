package com.example.examplemod.world;

import com.example.examplemod.ExampleMod;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VolcanoWorldGen {

    @SubscribeEvent
    public static void onBiomeLoad(BiomeLoadingEvent event) {
        boolean isMountainsByName = event.getName() != null &&
                (event.getName().getPath().contains("mountains") || event.getName().getPath().contains("mountain"));

        if (event.getCategory() == Biome.Category.EXTREME_HILLS || isMountainsByName) {
            StructureFeature<?, ?> structure = ModConfiguredStructures.CONFIGURED_VOLCANO;
            if (structure != null) {
                event.getGeneration().getStructures().add(() -> structure);
            }
        }
    }
}