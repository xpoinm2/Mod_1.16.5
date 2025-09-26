package com.example.examplemod.world;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.world.biome.BasaltMountainsBiome;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Handles biome registration for the mod.
 */
public final class ModBiomes {
    private static final Logger LOGGER = LogManager.getLogger();
    private ModBiomes() {
    }

    private static final DeferredRegister<Biome> BIOMES =
            DeferredRegister.create(ForgeRegistries.BIOMES, ExampleMod.MODID);

    public static final RegistryKey<Biome> BASALT_MOUNTAINS_KEY = RegistryKey.create(
            Registry.BIOME_REGISTRY,
            new ResourceLocation(ExampleMod.MODID, "basalt_mountains")
    );


    public static final RegistryObject<Biome> BASALT_MOUNTAINS = BIOMES.register(
            BASALT_MOUNTAINS_KEY.location().getPath(), BasaltMountainsBiome::create
    );

    public static void register(IEventBus bus) {
        BIOMES.register(bus);
        bus.addGenericListener(Biome.class, EventPriority.LOW, ModBiomes::registerWorldGenBiomes);
    }

    private static void registerWorldGenBiomes(final RegistryEvent.Register<Biome> event) {
        if (event.getRegistry() != ForgeRegistries.BIOMES) {
            return;
        }

        Biome biome = BASALT_MOUNTAINS.orElse(null);
        if (biome == null) {
            LOGGER.error("Skipping WorldGen registry hookup for Basalt Mountains because the biome is not registered yet.");
            return;
        }

        if (!WorldGenRegistries.BIOME.containsKey(BASALT_MOUNTAINS_KEY.location())) {
            Registry.register(WorldGenRegistries.BIOME, BASALT_MOUNTAINS_KEY.location(), biome);
        }
    }

    public static void setupBiomes() {
        Biome biome = BASALT_MOUNTAINS.orElse(null);
        if (biome == null) {
            LOGGER.error("Skipping Basalt Mountains biome setup because the biome failed to register.");
            return;
        }

        if (!WorldGenRegistries.BIOME.getOptional(BASALT_MOUNTAINS_KEY).isPresent()) {
            Registry.register(WorldGenRegistries.BIOME, BASALT_MOUNTAINS_KEY.location(), biome);
        }

        BiomeDictionary.addTypes(BASALT_MOUNTAINS_KEY,
                BiomeDictionary.Type.MOUNTAIN,
                BiomeDictionary.Type.HOT,
                BiomeDictionary.Type.OVERWORLD,
                BiomeDictionary.Type.RARE);
        BiomeManager.addBiome(BiomeManager.BiomeType.WARM, new BiomeManager.BiomeEntry(BASALT_MOUNTAINS_KEY, 1));
        BiomeManager.addAdditionalOverworldBiomes(BASALT_MOUNTAINS_KEY);
    }
}