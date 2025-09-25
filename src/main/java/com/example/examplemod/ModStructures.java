package com.example.examplemod;

import com.example.examplemod.world.VolcanoStructure;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.FlatChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.util.registry.WorldGenRegistries;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Centralises registration and spacing setup for custom structures.
 */
public class ModStructures {
    public static final DeferredRegister<Structure<?>> STRUCTURES =
            DeferredRegister.create(ForgeRegistries.STRUCTURE_FEATURES, ExampleMod.MODID);

    public static final RegistryObject<Structure<NoFeatureConfig>> VOLCANO = STRUCTURES.register(
            "volcano", () -> new VolcanoStructure(NoFeatureConfig.CODEC)
    );

    private static final StructureSeparationSettings VOLCANO_SPACING = new StructureSeparationSettings(16, 8, 0x4C6F7661);

    public static final IStructurePieceType VOLCANO_PIECE = IStructurePieceType.setPieceId(
            VolcanoStructure.VolcanoPiece::new, ExampleMod.MODID + ":volcano_piece");

    private static final Field STRUCTURE_CONFIG_FIELD = ObfuscationReflectionHelper.findField(
            DimensionStructuresSettings.class, "structureConfig");

    static {
        STRUCTURE_CONFIG_FIELD.setAccessible(true);
    }

    public static void register(IEventBus bus) {
        STRUCTURES.register(bus);
    }

    /**
     * Hooked from common setup to configure spacing/separation similar to vanilla structures.
     */
    public static void setupStructures() {
        Structure<?> volcano = VOLCANO.get();
        Structure.STRUCTURES_REGISTRY.put(volcano.getRegistryName().toString(), volcano);
        WorldGenRegistries.NOISE_GENERATOR_SETTINGS.entrySet().forEach(entry -> {
            Map<Structure<?>, StructureSeparationSettings> config = entry.getValue().structureSettings().structureConfig();
            if (!(config instanceof HashMap)) {
                config = new HashMap<>(config);
                setStructureConfig(entry.getValue().structureSettings(), config);
            }
            config.put(volcano, VOLCANO_SPACING);
        });
    }

    /**
     * Ensures custom spacing survives dimension datapacks and non-standard chunk generators.
     */
    @Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class WorldEvents {
        @SubscribeEvent
        public static void addDimensionSpacing(WorldEvent.Load event) {
            if (!(event.getWorld() instanceof ServerWorld)) return;
            ServerWorld serverWorld = (ServerWorld) event.getWorld();

            ChunkGenerator generator = serverWorld.getChunkSource().getGenerator();
            if (generator instanceof FlatChunkGenerator && serverWorld.dimension().equals(World.OVERWORLD)) {
                return;
            }

            Map<Structure<?>, StructureSeparationSettings> config = generator.getSettings().structureConfig();
            if (config.containsKey(VOLCANO.get())) return;

            Map<Structure<?>, StructureSeparationSettings> mutable = new HashMap<>(config);
            mutable.put(VOLCANO.get(), VOLCANO_SPACING);
            setStructureConfig(generator.getSettings(), mutable);
        }
    }

    private static void setStructureConfig(DimensionStructuresSettings settings,
                                           Map<Structure<?>, StructureSeparationSettings> map) {
        try {
            STRUCTURE_CONFIG_FIELD.set(settings, map);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to set structure configuration map", e);
        }
    }
}