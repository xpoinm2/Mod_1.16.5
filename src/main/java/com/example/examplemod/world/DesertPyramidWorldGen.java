package com.example.examplemod.world;

import com.example.examplemod.ExampleMod;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Injects the desert pyramid feature into vanilla desert biomes.
 */
@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class DesertPyramidWorldGen {
    private static final Logger LOGGER = LogManager.getLogger();

    private DesertPyramidWorldGen() {
    }

    @SubscribeEvent
    public static void onBiomeLoad(BiomeLoadingEvent event) {
        if (event.getCategory() != Biome.Category.DESERT) {
            return;
        }

        if (WorldGenRegistry.DESERT_PYRAMID == null) {
            LOGGER.warn("Desert pyramid feature is not registered yet; skipping injection to avoid a crash.");
            return;
        }

        event.getGeneration().addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, WorldGenRegistry.DESERT_PYRAMID);
    }
}