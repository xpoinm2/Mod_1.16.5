package com.example.examplemod.world;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.world.biome.BasaltMountainsBiome;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

/**
 * Handles biome registration for the mod.
 */
public final class ModBiomes {
    private ModBiomes() {
    }

    private static final DeferredRegister<Biome> BIOMES =
            DeferredRegister.create(ForgeRegistries.BIOMES, ExampleMod.MODID);

    public static final RegistryObject<Biome> BASALT_MOUNTAINS = BIOMES.register(
            "basalt_mountains", BasaltMountainsBiome::create
    );

    public static void register(IEventBus bus) {
        BIOMES.register(bus);
    }

    public static void setupBiomes() {
        RegistryKey<Biome> key = Objects.requireNonNull(
                BASALT_MOUNTAINS.getKey(),
                "Basalt Mountains biome registry key has not been registered yet"
        );
        BiomeDictionary.addTypes(key,
                BiomeDictionary.Type.MOUNTAIN,
                BiomeDictionary.Type.HOT,
                BiomeDictionary.Type.OVERWORLD,
                BiomeDictionary.Type.RARE);
        BiomeManager.addBiome(BiomeManager.BiomeType.WARM, new BiomeManager.BiomeEntry(key, 1));
        BiomeManager.addAdditionalOverworldBiomes(key);
    }
}