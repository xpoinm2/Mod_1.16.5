package com.example.examplemod.world;

import com.mojang.serialization.Codec;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import java.util.Random;

/**
 * Feature that generates exactly three lava-filled basalt volcanoes with bedrock foundations.
 */
public class VolcanoFeature extends Feature<NoFeatureConfig> {
    public VolcanoFeature(Codec<NoFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig cfg) {
        for (int i = 0; i < 3; i++) {
            int x = pos.getX() + rand.nextInt(16);
            int z = pos.getZ() + rand.nextInt(16);
            int surface = world.getHeight(Heightmap.Type.WORLD_SURFACE_WG, x, z);
            buildVolcano(world, new BlockPos(x, 0, z), surface + 40, rand);
        }
        return true;
    }

    private void buildVolcano(ISeedReader world, BlockPos base, int height, Random rand) {
        int baseRadius = 15;
        // reinforce the foundation with bedrock
        for (int y = 0; y <= 5; y++) {
            for (int dx = -baseRadius; dx <= baseRadius; dx++) {
                for (int dz = -baseRadius; dz <= baseRadius; dz++) {
                    if (Math.sqrt(dx * dx + dz * dz) <= baseRadius) {
                        world.setBlock(base.offset(dx, y, dz), Blocks.BEDROCK.defaultBlockState(), 2);
                    }
                }
            }
        }/// build basalt shell filled with lava
        for (int y = 0; y <= height; y++) {
            int radius = Math.max(1, (int) ((1.0 - (double) y / height) * baseRadius));
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    double dist = Math.sqrt(dx * dx + dz * dz);
                    BlockPos p = base.offset(dx, y, dz);
                    if (dist <= radius) {
                        if (dist >= radius - 1 || y == 0) {
                            world.setBlock(p, Blocks.BASALT.defaultBlockState(), 2);
                        } else {
                            world.setBlock(p, Blocks.LAVA.defaultBlockState(), 2);
                        }
                    }
                }
            }
        }
        // enlarged crater brim filled with lava
        int craterHeight = height - 3;
        int craterRadius = 6;
        for (int y = craterHeight; y < height; y++) {
            for (int dx = -craterRadius; dx <= craterRadius; dx++) {
                for (int dz = -craterRadius; dz <= craterRadius; dz++) {
                    double dist = Math.sqrt(dx * dx + dz * dz);
                    BlockPos p = base.offset(dx, y, dz);
                    if (dist <= craterRadius) {
                        if (y == craterHeight || dist >= craterRadius - 1) {
                            world.setBlock(p, Blocks.BASALT.defaultBlockState(), 2);
                        } else {
                            world.setBlock(p, Blocks.LAVA.defaultBlockState(), 2);
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