package com.example.examplemod.world.feature;

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
 * Generates a compact basalt mountain range with lava pools and falls.
 */
public class BasaltMountainFeature extends Feature<NoFeatureConfig> {
    private static final int MIN_RADIUS = 60;
    private static final int MAX_RADIUS = 100;
    private static final int MIN_HEIGHT = 48;
    private static final int MAX_HEIGHT = 76;
    private static final int DUPLICATE_SAMPLE_STEP = 8;
    private static final int BASE_FILL_DEPTH = 40;
    private static final float RIM_MAGMA_CHANCE = 0.2F;
    private static final float RANDOM_BLACKSTONE_CHANCE = 0.03F;
    private static final double CRATER_LAVA_START = 0.88D;
    private static final int MIN_LAVA_POOL_ATTEMPTS = 8;
    private static final int EXTRA_LAVA_POOL_ATTEMPTS = 5;
    private static final int BASIN_MAX_DEPTH = 4;
    private static final int MIN_LAVA_FALLS = 4;
    private static final int EXTRA_LAVA_FALLS = 4;

    public BasaltMountainFeature(Codec<NoFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(@Nonnull ISeedReader world, @Nonnull ChunkGenerator generator, @Nonnull Random random,
                         @Nonnull BlockPos origin, @Nonnull NoFeatureConfig config) {
        BlockPos surface = world.getHeightmapPos(Heightmap.Type.WORLD_SURFACE_WG, origin);
        BlockPos base = new BlockPos(surface.getX(), surface.getY(), surface.getZ());

        if (base.getY() <= generator.getSeaLevel()) {
            return false;
        }

        int radius = MIN_RADIUS + random.nextInt(MAX_RADIUS - MIN_RADIUS + 1);
        int height = MIN_HEIGHT + random.nextInt(MAX_HEIGHT - MIN_HEIGHT + 1);
        int craterRadius = Math.max(6, radius / 6);

        if (base.getY() + height >= generator.getGenDepth()) {
            return false;
        }

        if (hasExistingBasalt(world, base, radius)) {
            return false;
        }

        buildMass(world, generator, base, radius, height, craterRadius, random);
        reinforceBase(world, base, radius);
        createLavaPools(world, base, radius, random);
        createLavaFalls(world, base, radius, height, random);
        return true;
    }

    private boolean hasExistingBasalt(ISeedReader world, BlockPos center, int radius) {
        BlockPos.Mutable sample = new BlockPos.Mutable();
        for (int dx = -radius; dx <= radius; dx += DUPLICATE_SAMPLE_STEP) {
            for (int dz = -radius; dz <= radius; dz += DUPLICATE_SAMPLE_STEP) {
                if (dx * dx + dz * dz > radius * radius) {
                    continue;
                }
                sample.set(center.getX() + dx, center.getY(), center.getZ() + dz);
                BlockPos top = world.getHeightmapPos(Heightmap.Type.WORLD_SURFACE_WG, sample);
                if (top.getY() <= 0) {
                    continue;
                }
                BlockState below = world.getBlockState(top.below());
                if (isBasaltBlock(below)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void buildMass(ISeedReader world, ChunkGenerator generator, BlockPos base, int radius, int height,
                           int craterRadius, Random random) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int y = 0; y <= height; y++) {
            double progress = (double) y / (double) height;
            double radiusAtY = Math.cos(progress * Math.PI / 2.0) * radius;
            int layerRadius = Math.max(craterRadius, (int) Math.round(radiusAtY));
            for (int dx = -layerRadius; dx <= layerRadius; dx++) {
                for (int dz = -layerRadius; dz <= layerRadius; dz++) {
                    double dist = Math.sqrt(dx * dx + dz * dz);
                    if (dist > layerRadius + 0.3) {
                        continue;
                    }
                    int x = base.getX() + dx;
                    int yPos = base.getY() + y;
                    int z = base.getZ() + dz;
                    if (yPos < 0 || yPos >= generator.getGenDepth()) {
                        continue;
                    }
                    mutable.set(x, yPos, z);

                    boolean rim = dist >= layerRadius - 1.6;
                    boolean crater = progress > 0.8 && dist <= craterRadius - 0.5;
                    BlockState state;

                    if (crater) {
                        if (progress > CRATER_LAVA_START) {
                            state = Blocks.LAVA.defaultBlockState();
                        } else {
                            state = Blocks.BASALT.defaultBlockState();
                        }
                    } else if (rim && random.nextFloat() < RIM_MAGMA_CHANCE) {
                        state = Blocks.MAGMA_BLOCK.defaultBlockState();
                    } else {
                        float variantChance = random.nextFloat();
                        if (variantChance < 0.08F) {
                            state = Blocks.POLISHED_BASALT.defaultBlockState();
                        } else if (variantChance < 0.08F + RANDOM_BLACKSTONE_CHANCE) {
                            state = Blocks.BLACKSTONE.defaultBlockState();
                        } else {
                            state = Blocks.BASALT.defaultBlockState();
                        }
                    }

                    world.setBlock(mutable, state, 2);
                }
            }
        }
    }

    private void reinforceBase(ISeedReader world, BlockPos base, int radius) {
        BlockPos.Mutable checkPos = new BlockPos.Mutable();
        BlockPos.Mutable fillPos = new BlockPos.Mutable();
        int minY = Math.max(0, base.getY() - BASE_FILL_DEPTH);

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                double dist = Math.sqrt(dx * dx + dz * dz);
                if (dist > radius + 0.5) {
                    continue;
                }
                checkPos.set(base.getX() + dx, base.getY() - 1, base.getZ() + dz);
                while (checkPos.getY() > minY && world.isEmptyBlock(checkPos)) {
                    checkPos.move(0, -1, 0);
                }

                int fillY = checkPos.getY() + 1;
                while (fillY < base.getY()) {
                    fillPos.set(base.getX() + dx, fillY, base.getZ() + dz);
                    world.setBlock(fillPos, Blocks.BASALT.defaultBlockState(), 2);
                    fillY++;
                }
            }
        }
    }

    private void createLavaPools(ISeedReader world, BlockPos base, int radius, Random random) {
        int attempts = MIN_LAVA_POOL_ATTEMPTS + random.nextInt(EXTRA_LAVA_POOL_ATTEMPTS + 1);
        for (int i = 0; i < attempts; i++) {
            int dx = random.nextInt(radius) - random.nextInt(radius);
            int dz = random.nextInt(radius) - random.nextInt(radius);
            if (dx * dx + dz * dz > radius * radius) {
                continue;
            }
            BlockPos surface = world.getHeightmapPos(Heightmap.Type.WORLD_SURFACE_WG, base.offset(dx, 0, dz));
            BlockPos lavaPos = new BlockPos(surface.getX(), surface.getY(), surface.getZ());
            world.setBlock(lavaPos, Blocks.LAVA.defaultBlockState(), 2);
            carveBasin(world, lavaPos, random);
        }
    }

    private void carveBasin(ISeedReader world, BlockPos center, Random random) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int basinRadius = 3 + random.nextInt(3);
        for (int dx = -basinRadius; dx <= basinRadius; dx++) {
            for (int dz = -basinRadius; dz <= basinRadius; dz++) {
                double dist = Math.sqrt(dx * dx + dz * dz);
                if (dist > basinRadius + 0.3) {
                    continue;
                }
                for (int dy = 0; dy <= BASIN_MAX_DEPTH; dy++) {
                    int y = center.getY() - dy;
                    if (y < 0) {
                        continue;
                    }
                    mutable.set(center.getX() + dx, y, center.getZ() + dz);
                    if (dy == 0) {
                        world.setBlock(mutable, Blocks.LAVA.defaultBlockState(), 2);
                    } else if (world.getBlockState(mutable).getBlock() != Blocks.LAVA) {
                        world.setBlock(mutable, Blocks.BASALT.defaultBlockState(), 2);
                    }
                }
            }
        }
    }

    private void createLavaFalls(ISeedReader world, BlockPos base, int radius, int height, Random random) {
        int lavaY = base.getY() + height - 4;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int falls = MIN_LAVA_FALLS + random.nextInt(EXTRA_LAVA_FALLS + 1);
        for (int i = 0; i < falls; i++) {
            double angle = random.nextDouble() * Math.PI * 2.0;
            int offsetRadius = (int) (radius * 0.6 + random.nextDouble() * radius * 0.25);
            int x = base.getX() + (int) Math.round(Math.cos(angle) * offsetRadius);
            int z = base.getZ() + (int) Math.round(Math.sin(angle) * offsetRadius);

            for (int y = lavaY; y >= base.getY() - 10; y--) {
                if (y < 0) {
                    break;
                }
                mutable.set(x, y, z);
                BlockState state = world.getBlockState(mutable);
                if (world.isEmptyBlock(mutable) || state.getBlock() == Blocks.AIR || state.getBlock() == Blocks.LAVA) {
                    world.setBlock(mutable, Blocks.LAVA.defaultBlockState(), 2);
                    continue;
                }

                if (!state.getMaterial().isSolid()) {
                    world.setBlock(mutable, Blocks.LAVA.defaultBlockState(), 2);
                    continue;
                }

                world.setBlock(mutable, Blocks.BASALT.defaultBlockState(), 2);
                mutable.move(0, 1, 0);
                if (mutable.getY() < world.getMaxBuildHeight()) {
                    world.setBlock(mutable, Blocks.LAVA.defaultBlockState(), 2);
                }
                break;
            }
        }
    }

    private boolean isBasaltBlock(BlockState state) {
        return state.getBlock() == Blocks.BASALT
                || state.getBlock() == Blocks.BLACKSTONE
                || state.getBlock() == Blocks.MAGMA_BLOCK;
    }
}