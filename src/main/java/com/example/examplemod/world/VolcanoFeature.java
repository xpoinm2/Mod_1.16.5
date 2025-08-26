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
 * Feature that generates a lava-filled basalt volcano above the terrain without
 * filling the entire world from bedrock.
 */
public class VolcanoFeature extends Feature<NoFeatureConfig> {
    public VolcanoFeature(Codec<NoFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig cfg) {
        int x = pos.getX() + rand.nextInt(16);
        int z = pos.getZ() + rand.nextInt(16);

        int groundY = world.getHeight(Heightmap.Type.WORLD_SURFACE_WG, x, z);
        int maxHeight = world.getMaxBuildHeight();
        int height = Math.min(20 + rand.nextInt(20), maxHeight - groundY);
        if (height <= 0) {
            return false;
        }

        buildVolcano(world, new BlockPos(x, groundY, z), height, rand);
        return true;
            }

            private void buildVolcano(ISeedReader world, BlockPos base, int height, Random rand) {
                int maxRadius = 8 + rand.nextInt(8);
                int buildLimit = world.getMaxBuildHeight();

                for (int y = 0; y < height && base.getY() + y < buildLimit; y++) {
                    int radius = (int) (maxRadius * (1.0 - (double) y / height));
                    for (int dx = -radius; dx <= radius; dx++) {
                        for (int dz = -radius; dz <= radius; dz++) {
                            double dist = Math.sqrt(dx * dx + dz * dz);
                            BlockPos p = base.offset(dx, y, dz);
                            if (dist <= radius) {
                                if (dist >= radius - 1) {
                                    world.setBlock(p, Blocks.BASALT.defaultBlockState(), 2);
                                } else {
                                    world.setBlock(p, Blocks.LAVA.defaultBlockState(), 2);
                                }
                            }
                        }
                    }
            }
        }
    }