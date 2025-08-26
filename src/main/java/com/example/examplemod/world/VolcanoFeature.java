package com.example.examplemod.world;

import com.mojang.serialization.Codec;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Feature that generates a single lava-filled basalt volcano with a bedrock foundation
 * per biome.
 */
public class VolcanoFeature extends Feature<NoFeatureConfig> {
    public VolcanoFeature(Codec<NoFeatureConfig> codec) {
        super(codec);
    }

    /**
     * Tracks how many volcanoes have been generated per biome to limit to one.
     */
    private static final Map<ResourceLocation, Integer> VOLCANO_COUNTS = new HashMap<>();

    @Override
    public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig cfg) {
        ResourceLocation biome = world.getBiome(pos).getRegistryName();
        if (biome == null) {
            return false;
        }
        int count = VOLCANO_COUNTS.getOrDefault(biome, 0);
            if (count >= 1) {
                return false; // already generated a volcano for this biome
            }
            int x = pos.getX() + rand.nextInt(16);
            int z = pos.getZ() + rand.nextInt(16);
            int surface = world.getHeight(Heightmap.Type.WORLD_SURFACE_WG, x, z);
            int height = 20 + rand.nextInt(15);
            buildVolcano(world, new BlockPos(x, surface - 5, z), height, rand);
            VOLCANO_COUNTS.put(biome, count + 1);
            return true;
        }

        private void buildVolcano(ISeedReader world, BlockPos base, int height, Random rand) {
            int baseRadius = 25;
            int lavaHeight = Math.min(20, height - 4);
            // reinforce the foundation with bedrock below the surface
            for (int y = -5; y <= 0; y++) {
                for (int dx = -baseRadius; dx <= baseRadius; dx++) {
                    for (int dz = -baseRadius; dz <= baseRadius; dz++) {
                        if (Math.sqrt(dx * dx + dz * dz) <= baseRadius) {
                            world.setBlock(base.offset(dx, y, dz), Blocks.BEDROCK.defaultBlockState(), 2);
                        }
                    }
                }
            }
            // build tapered basalt cone with lava interior
            for (int y = 0; y <= height; y++) {
                int radius = Math.max(1, baseRadius - (baseRadius * y / height));
                for (int dx = -radius; dx <= radius; dx++) {
                    for (int dz = -radius; dz <= radius; dz++) {
                        double dist = Math.sqrt(dx * dx + dz * dz);
                        BlockPos p = base.offset(dx, y, dz);
                        if (dist <= radius) {
                            if (dist >= radius - 1 || y == 0) {
                                world.setBlock(p, Blocks.BASALT.defaultBlockState(), 2);
                            } else if (y < lavaHeight) {
                                world.setBlock(p, Blocks.LAVA.defaultBlockState(), 2);
                            }
                        }
                    }
                }
            }
            // carve and rim the crater at the top
            int craterRadius = baseRadius / 3;
            for (int y = height - 4; y <= height; y++) {
                for (int dx = -craterRadius; dx <= craterRadius; dx++) {
                    for (int dz = -craterRadius; dz <= craterRadius; dz++) {
                        double dist = Math.sqrt(dx * dx + dz * dz);
                        BlockPos p = base.offset(dx, y, dz);
                        if (dist <= craterRadius) {
                                if (dist >= craterRadius - 1) {
                                    world.setBlock(p, Blocks.BASALT.defaultBlockState(), 2);
                                } else {
                                    world.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
                                }
                            }
                        }
                    }
                }
                // scatter additional lava holes around the volcano
                for (int i = 0; i < 5; i++) {
                    int hx = base.getX() + rand.nextInt(baseRadius * 2) - baseRadius;
                    int hz = base.getZ() + rand.nextInt(baseRadius * 2) - baseRadius;
                    int surface = world.getHeight(Heightmap.Type.WORLD_SURFACE_WG, hx, hz);
                    world.setBlock(new BlockPos(hx, surface, hz), Blocks.LAVA.defaultBlockState(), 2);
                }
            }
        }