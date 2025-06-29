package com.example.examplemod.world;

import com.example.examplemod.ExampleMod;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.function.Supplier;

/**
 * Removes vanilla brown and red mushroom patches from biome generation.
 */
@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VanillaMushroomRemoval {
    @SubscribeEvent
    public static void onBiomeLoad(BiomeLoadingEvent event) {
        List<Supplier<ConfiguredFeature<?, ?>>> features =
                event.getGeneration().getFeatures(GenerationStage.Decoration.VEGETAL_DECORATION);
        features.removeIf(supplier -> {
            ConfiguredFeature<?, ?> feature = supplier.get();
            ResourceLocation name = WorldGenRegistries.CONFIGURED_FEATURE.getKey(feature);
            if (name == null) return false;
            String path = name.getPath();
            return path.contains("brown_mushroom") || path.contains("red_mushroom");
        });
    }
}