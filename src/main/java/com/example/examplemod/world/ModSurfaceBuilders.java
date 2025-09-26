package com.example.examplemod.world;

import com.example.examplemod.ExampleMod;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.ISurfaceBuilderConfig;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

/**
 * Holds configured surface builders used by the mod.
 */
public final class ModSurfaceBuilders {
    private ModSurfaceBuilders() {
    }

    public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> PURE_BASALT = register(
            "pure_basalt",
            SurfaceBuilder.DEFAULT.configured(new SurfaceBuilderConfig(
                    Blocks.BASALT.defaultBlockState(),
                    Blocks.BASALT.defaultBlockState(),
                    Blocks.BASALT.defaultBlockState()
            ))
    );

    private static <SC extends ISurfaceBuilderConfig> ConfiguredSurfaceBuilder<SC> register(
            String name,
            ConfiguredSurfaceBuilder<SC> builder
    ) {
        return Registry.register(
                WorldGenRegistries.CONFIGURED_SURFACE_BUILDER,
                new ResourceLocation(ExampleMod.MODID, name),
                builder
        );
    }
}