package com.example.examplemod.world;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.ModBlocks;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.BlockClusterFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.blockplacer.SimpleBlockPlacer;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.ChanceConfig;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GingerPlantWorldGen {
    private static ConfiguredFeature<?, ?> PATCH;

    private static void ensureFeature() {
        if (PATCH != null) return;
        BlockClusterFeatureConfig config = (new BlockClusterFeatureConfig.Builder(
                new SimpleBlockStateProvider(ModBlocks.GINGER_PLANT.get().defaultBlockState()),
                SimpleBlockPlacer.INSTANCE)).tries(8).build();
        PATCH = Feature.RANDOM_PATCH.configured(config)
                .decorated(Placement.CHANCE.configured(new ChanceConfig(1)));
        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, ExampleMod.MODID + ":ginger_patch", PATCH);
    }

    @SubscribeEvent
    public static void onBiomeLoad(BiomeLoadingEvent event) {
        Biome.Category cat = event.getCategory();
        if (cat == Biome.Category.JUNGLE || cat == Biome.Category.SAVANNA) {
            ensureFeature();
            event.getGeneration().addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, PATCH);
        }
    }
}