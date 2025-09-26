package com.example.examplemod.world.biome;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.world.WorldGenRegistry;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.GenerationStage;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Injects custom features into the basalt mountains biome when it loads.
 */
@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class BasaltMountainsBiomeEvents {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceLocation BASALT_MOUNTAINS =
            new ResourceLocation(ExampleMod.MODID, "basalt_mountains");

    private BasaltMountainsBiomeEvents() {
    }

    @SubscribeEvent
    public static void onBiomeLoad(BiomeLoadingEvent event) {
        if (event.getName() == null || !event.getName().equals(BASALT_MOUNTAINS)) {
            return;
        }

        if (WorldGenRegistry.BASALT_MOUNTAIN == null) {
            LOGGER.warn("Basalt mountain feature has not been registered yet; skipping injection to avoid a crash.");
            return;
        }


        event.getGeneration().getFeatures(GenerationStage.Decoration.SURFACE_STRUCTURES)
                .add(() -> WorldGenRegistry.BASALT_MOUNTAIN);
    }
}