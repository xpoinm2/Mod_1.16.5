package com.example.examplemod.world;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import javax.annotation.Nonnull;

import java.util.Random;

/**
 * Procedurally builds a basalt volcano with a lava filled crater.
 */
public class VolcanoFeature extends Feature<NoFeatureConfig> {
    private static final int MIN_WORLD_HEIGHT = 0;
    private static final int MIN_HEIGHT = 24;
    private static final int MAX_HEIGHT = 38;
    private static final int MIN_RADIUS = 9;
    private static final int MAX_RADIUS = 15;

    public VolcanoFeature(Codec<NoFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(@Nonnull ISeedReader world, @Nonnull ChunkGenerator generator, @Nonnull Random random,
                         @Nonnull BlockPos origin, @Nonnull NoFeatureConfig config) {
        int surfaceY = generator.getBaseHeight(origin.getX(), origin.getZ(), Heightmap.Type.WORLD_SURFACE_WG);
        if (surfaceY <= MIN_WORLD_HEIGHT + 4) {
            return false;
        }

        BlockPos base = new BlockPos(origin.getX(), surfaceY, origin.getZ());
        int height = MIN_HEIGHT + random.nextInt(MAX_HEIGHT - MIN_HEIGHT + 1);
        int baseRadius = MIN_RADIUS + random.nextInt(MAX_RADIUS - MIN_RADIUS + 1);
        int craterRadius = Math.max(3, baseRadius / 3);

        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (int y = 0; y < height; y++) {
            int radius = Math.max(craterRadius, (int) Math.ceil(baseRadius * (1.0 - (double) y / height)));
            double craterCheck = craterRadius - 0.75;
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    double dist = Math.sqrt(dx * dx + dz * dz);
                    if (dist > radius + 0.5) {
                        continue;
                    }

                    mutable.set(base.getX() + dx, base.getY() + y, base.getZ() + dz);
                    if (mutable.getY() < MIN_WORLD_HEIGHT || mutable.getY() >= generator.getGenDepth()) {
                        continue;
                    }

                    boolean shell = dist >= radius - 1.25;
                    boolean crater = y >= height - 4 && dist <= craterCheck;
                    BlockState state;
                    if (crater) {
                        if (y >= height - 2) {
                            state = Blocks.LAVA.defaultBlockState();
                        } else {
                            state = Blocks.AIR.defaultBlockState();
                        }
                    } else if (shell) {
                        state = Blocks.BASALT.defaultBlockState();
                    } else {
                        state = Blocks.STONE.defaultBlockState();
                    }

                    world.setBlock(mutable, state, 2);
                }
            }
        }

        reinforceBase(world, base, baseRadius);

        return true;
    }

    private void reinforceBase(ISeedReader world, BlockPos base, int baseRadius) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        BlockPos.Mutable fillPos = new BlockPos.Mutable();
        int minBuild = MIN_WORLD_HEIGHT;

        for (int dx = -baseRadius; dx <= baseRadius; dx++) {
            for (int dz = -baseRadius; dz <= baseRadius; dz++) {
                double dist = Math.sqrt(dx * dx + dz * dz);
                if (dist > baseRadius + 0.5) {
                    continue;
                }

                mutable.set(base.getX() + dx, base.getY() - 1, base.getZ() + dz);
                while (mutable.getY() > minBuild && world.isEmptyBlock(mutable)) {
                    mutable.move(0, -1, 0);
                }

                int fillY = mutable.getY() + 1;
                while (fillY <= base.getY()) {
                    fillPos.set(base.getX() + dx, fillY, base.getZ() + dz);
                    world.setBlock(fillPos, Blocks.BASALT.defaultBlockState(), 2);
                    fillY++;
                }
            }
        }
    }
}