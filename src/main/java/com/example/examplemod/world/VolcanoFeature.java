package com.example.examplemod.world;

import com.mojang.serialization.Codec;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import java.util.Random;

/**
 * Feature that generates a lava-filled basalt volcano rising from bedrock.
 */
public class VolcanoFeature extends Feature<NoFeatureConfig> {
    public VolcanoFeature(Codec<NoFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig cfg) {
    int x = pos.getX() + rand.nextInt(16);
    int z = pos.getZ() + rand.nextInt(16);
    int height = 40 + rand.nextInt(20);
    buildVolcano(world, new BlockPos(x, 0, z), height);
        return true;
}
                private void buildVolcano(ISeedReader world, BlockPos base, int height) {
                    int radius = 25;
                    for (int y = 0; y < height; y++) {
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