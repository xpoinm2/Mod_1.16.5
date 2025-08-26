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
 * Feature that generates lava-filled basalt volcanoes with bedrock foundations.
 */
public class VolcanoFeature extends Feature<NoFeatureConfig> {
    public VolcanoFeature(Codec<NoFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig cfg) {
        // only attempt a volcano in roughly half of the chunks to reduce density
        if (rand.nextInt(2) != 0) {
            return false;
        }
        int x = pos.getX() + rand.nextInt(16);
        int z = pos.getZ() + rand.nextInt(16);
        int height = (30 + rand.nextInt(15)) * 3; // triple the height for larger structures
        buildVolcano(world, new BlockPos(x, 2, z), height, rand);
        return true;
    }

    private void buildVolcano(ISeedReader world, BlockPos base, int height, Random rand) {
        int baseRadius = 25 * 3;
        // reinforce the foundation with bedrock from the world bottom
        for (int y = 0; y <= base.getY(); y++) {
            for (int dx = -baseRadius; dx <= baseRadius; dx++) {
                for (int dz = -baseRadius; dz <= baseRadius; dz++) {
                    if (Math.sqrt(dx * dx + dz * dz) <= baseRadius) {
                        world.setBlock(new BlockPos(base.getX() + dx, y, base.getZ() + dz), Blocks.BEDROCK.defaultBlockState(), 2);
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
                        } else if (y < height - 4) {
                            world.setBlock(p, Blocks.LAVA.defaultBlockState(), 2);
                        }
                    }
                }
            }
        }
        // carve a crater at the top without expanding the silhouette
        int topRadius = Math.max(1, baseRadius - (baseRadius * (height - 4) / height));
        int craterRadius = Math.max(1, topRadius - 1);
        for (int y = height - 4; y <= height; y++) {
            for (int dx = -craterRadius; dx <= craterRadius; dx++) {
                for (int dz = -craterRadius; dz <= craterRadius; dz++) {
                    double dist = Math.sqrt(dx * dx + dz * dz);
                    BlockPos p = base.offset(dx, y, dz);
                    if (dist < craterRadius) {
                        if (y == height - 4) {
                            world.setBlock(p, Blocks.LAVA.defaultBlockState(), 2);
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