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
 * Simple feature that generates three large basalt volcanoes reaching from bedrock to the sky.
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
            buildVolcano(world, new BlockPos(x, 0, z), surface + 40);
        }
        return true;
    }

    private void buildVolcano(ISeedReader world, BlockPos base, int height) {
        int baseRadius = 15;
        for (int y = 0; y <= height; y++) {
            int radius = Math.max(1, (int)((1.0 - (double)y / height) * baseRadius));
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    double dist = Math.sqrt(dx * dx + dz * dz);
                    BlockPos p = base.offset(dx, y, dz);
                    if (dist <= radius) {
                        if (dist >= radius - 1 || y == 0) {
                            world.setBlock(p, Blocks.BASALT.defaultBlockState(), 2);
                        } else {
                            world.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
                        }
                    }
                }
            }
        }
        // crater with lava
        int craterHeight = height - 5;
        int craterRadius = 5;
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
        // bedrock core
        for (int y = 0; y < craterHeight; y++) {
            world.setBlock(base.offset(0, y, 0), Blocks.BEDROCK.defaultBlockState(), 2);
        }
    }
}