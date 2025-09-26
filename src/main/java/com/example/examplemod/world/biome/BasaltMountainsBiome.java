package com.example.examplemod.world.biome;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeAmbience;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.biome.BiomeGenerationSettings;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.biome.MoodSoundAmbience;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Features;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilders;

/**
 * Factory for the basalt mountains biome.
 */
public final class BasaltMountainsBiome {
    private static final float TEMPERATURE = 2.0F;
    private BasaltMountainsBiome() {
    }

    public static Biome create() {
        BiomeAmbience effects = new BiomeAmbience.Builder()
                .fogColor(0x1b1b21)
                .waterColor(0x3f0f0f)
                .waterFogColor(0x160a0a)
                .skyColor(calculateSkyColor(TEMPERATURE))
                .ambientMoodSound(MoodSoundAmbience.LEGACY_CAVE_SETTINGS)
                .build();

        MobSpawnInfo.Builder mobSpawnInfo = new MobSpawnInfo.Builder();
        mobSpawnInfo.setPlayerCanSpawn();
        mobSpawnInfo.addSpawn(EntityClassification.MONSTER,
                new MobSpawnInfo.Spawners(EntityType.MAGMA_CUBE, 20, 1, 3));
        mobSpawnInfo.addSpawn(EntityClassification.MONSTER,
                new MobSpawnInfo.Spawners(EntityType.BLAZE, 8, 1, 2));

        BiomeGenerationSettings.Builder generation = new BiomeGenerationSettings.Builder();
        generation.surfaceBuilder(() -> ConfiguredSurfaceBuilders.BASALT_DELTAS);
        DefaultBiomeFeatures.addDefaultCarvers(generation);
        DefaultBiomeFeatures.addDefaultMonsterRoom(generation);
        DefaultBiomeFeatures.addDefaultUndergroundVariety(generation);
        DefaultBiomeFeatures.addDefaultOres(generation);
        DefaultBiomeFeatures.addDefaultSprings(generation);
        generation.addFeature(GenerationStage.Decoration.LAKES, Features.LAKE_LAVA);
        generation.addFeature(GenerationStage.Decoration.LOCAL_MODIFICATIONS, Features.SPRING_LAVA);

        return new Biome.Builder()
                .precipitation(Biome.RainType.NONE)
                .biomeCategory(Biome.Category.EXTREME_HILLS)
                .depth(1.6F)
                .scale(0.8F)
                .temperature(TEMPERATURE)
                .downfall(0.0F)
                .specialEffects(effects)
                .mobSpawnSettings(mobSpawnInfo.build())
                .generationSettings(generation.build())
                .build();
    }

    private static int calculateSkyColor(float temperature) {
        float f = MathHelper.clamp(temperature / 3.0F, -1.0F, 1.0F);
        return MathHelper.hsvToRgb(0.62222224F - f * 0.05F, 0.5F + f * 0.1F, 1.0F);
    }
}