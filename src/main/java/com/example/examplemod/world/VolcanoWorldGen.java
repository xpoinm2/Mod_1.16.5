package com.example.examplemod.world;

import com.example.examplemod.ExampleMod;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VolcanoWorldGen {

    @SubscribeEvent
    public static void onBiomeLoad(BiomeLoadingEvent event) {
        ResourceLocation name = event.getName();

        boolean isMountainsByName = name != null && (name.getPath().contains("mountains") || name.getPath().contains("mountain"));
        if (event.getCategory() == Biome.Category.EXTREME_HILLS || isMountainsByName) {
            event.getGeneration().getStructures().add(() -> ModConfiguredStructures.CONFIGURED_VOLCANO);
        }
    }
}