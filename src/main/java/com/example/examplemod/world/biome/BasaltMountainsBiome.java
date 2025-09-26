package com.example.examplemod.world.biome;

import net.minecraft.block.Blocks;
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
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.Features;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import com.example.examplemod.world.ModSurfaceBuilders;
import net.minecraft.world.gen.feature.template.BlockMatchRuleTest;

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

        BiomeGenerationSettings.Builder generation = new BiomeGenerationSettings.Builder();
        generation.surfaceBuilder(() -> ModSurfaceBuilders.PURE_BASALT);
        DefaultBiomeFeatures.addDefaultCarvers(generation);
        DefaultBiomeFeatures.addDefaultMonsterRoom(generation);
        DefaultBiomeFeatures.addDefaultUndergroundVariety(generation);
        DefaultBiomeFeatures.addDefaultOres(generation);
        DefaultBiomeFeatures.addDefaultSprings(generation);

        ConfiguredFeature<?, ?> replaceStoneWithBasalt = Feature.ORE
                .configured(new OreFeatureConfig(
                        OreFeatureConfig.FillerBlockType.NATURAL_STONE,
                        Blocks.BASALT.defaultBlockState(),
                        64))
                .range(256).squared().count(128);
        generation.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, replaceStoneWithBasalt);

        ConfiguredFeature<?, ?> replaceGrassWithBasalt = Feature.ORE
                .configured(new OreFeatureConfig(
                        new BlockMatchRuleTest(Blocks.GRASS_BLOCK),
                        Blocks.BASALT.defaultBlockState(),
                        48))
                .range(256).squared().count(96);
        generation.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, replaceGrassWithBasalt);

        ConfiguredFeature<?, ?> replaceDirtWithBasalt = Feature.ORE
                .configured(new OreFeatureConfig(
                        new BlockMatchRuleTest(Blocks.DIRT),
                        Blocks.BASALT.defaultBlockState(),
                        48))
                .range(256).squared().count(96);
        generation.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, replaceDirtWithBasalt);

        ConfiguredFeature<?, ?> replaceCoarseDirtWithBasalt = Feature.ORE
                .configured(new OreFeatureConfig(
                        new BlockMatchRuleTest(Blocks.COARSE_DIRT),
                        Blocks.BASALT.defaultBlockState(),
                        48))
                .range(256).squared().count(64);
        generation.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, replaceCoarseDirtWithBasalt);

        ConfiguredFeature<?, ?> replaceGravelWithBasalt = Feature.ORE
                .configured(new OreFeatureConfig(
                        new BlockMatchRuleTest(Blocks.GRAVEL),
                        Blocks.BASALT.defaultBlockState(),
                        48))
                .range(256).squared().count(64);
        generation.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, replaceGravelWithBasalt);

        ConfiguredFeature<?, ?> replaceCobblestoneWithBasalt = Feature.ORE
                .configured(new OreFeatureConfig(
                        new BlockMatchRuleTest(Blocks.COBBLESTONE),
                        Blocks.BASALT.defaultBlockState(),
                        32))
                .range(256).squared().count(48);
        generation.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, replaceCobblestoneWithBasalt);

        for (int i = 0; i < 180; i++) {
            generation.addFeature(GenerationStage.Decoration.LAKES, Features.LAKE_LAVA);
        }
        for (int i = 0; i < 120; i++) {
            generation.addFeature(GenerationStage.Decoration.LOCAL_MODIFICATIONS, Features.SPRING_LAVA);
        }

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