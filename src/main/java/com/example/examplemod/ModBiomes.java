package com.example.examplemod;

import com.example.examplemod.world.WorldGenRegistry;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.*;
import net.minecraft.world.gen.GenerationStage;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Registry for custom biomes.
 */
public class ModBiomes {
    public static final DeferredRegister<Biome> BIOMES =
            DeferredRegister.create(ForgeRegistries.BIOMES, ExampleMod.MODID);

    public static final RegistryKey<Biome> VOLCANOES_KEY = RegistryKey.create(Registry.BIOME_REGISTRY,
            new ResourceLocation(ExampleMod.MODID, "volcanoes"));

    public static final RegistryObject<Biome> VOLCANOES = BIOMES.register("volcanoes", ModBiomes::createVolcanoes);

    public static void register(IEventBus bus) {
        BIOMES.register(bus);
    }

    private static Biome createVolcanoes() {
        BiomeGenerationSettings.Builder generation = new BiomeGenerationSettings.Builder()
                .surfaceBuilder(SurfaceBuilder.DEFAULT.configured(SurfaceBuilder.CONFIG_STONE));
        // Add default overworld features similar to mountains
        DefaultBiomeFeatures.addDefaultCarvers(generation);
        DefaultBiomeFeatures.addDefaultLakes(generation);
        DefaultBiomeFeatures.addDefaultOres(generation);
        DefaultBiomeFeatures.addDefaultMonsterRoom(generation);
        DefaultBiomeFeatures.addSurfaceFreezing(generation);
        // Add our volcano feature
        generation.addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, WorldGenRegistry.VOLCANO);

        MobSpawnInfo.Builder spawns = new MobSpawnInfo.Builder();
        DefaultBiomeFeatures.farmAnimals(spawns);
        DefaultBiomeFeatures.commonSpawns(spawns);

        BiomeAmbience effects = new BiomeAmbience.Builder()
                .waterColor(4159204).waterFogColor(329011)
                .fogColor(12638463)
                .skyColor(calculateSkyColor(0.2F))
                .build();

        return new Biome.Builder()
                .precipitation(Biome.RainType.RAIN)
                .biomeCategory(Biome.Category.EXTREME_HILLS)
                .depth(1.0F).scale(0.5F)
                .temperature(0.2F).downfall(0.3F)
                .specialEffects(effects)
                .mobSpawnSettings(spawns.build())
                .generationSettings(generation.build())
                .build();
    }

    private static int calculateSkyColor(float temperature) {
        float f = temperature / 3.0F;
        f = MathHelper.clamp(f, -1.0F, 1.0F);
        return MathHelper.hsvToRgb(0.62222224F - f * 0.05F, 0.5F + f * 0.1F, 1.0F);
    }
}