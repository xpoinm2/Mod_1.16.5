package com.example.examplemod.world.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Generates a sandstone pyramid structure in desert biomes.
 */
public class DesertPyramidFeature extends Feature<NoFeatureConfig> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int BASE_WIDTH = 11;
    /**
     * The maximum difference in block height allowed across the pyramid's footprint. Deserts often
     * contain rolling dunes, so a very small tolerance prevents the structure from ever finding a
     * suitable area. Relax the threshold so generation can succeed on typical terrain without
     * looking out of place.
     */
    private static final int MAX_TERRAIN_VARIATION = 6;
    private static final int FOUNDATION_DEPTH = 6;
    private static final AtomicInteger GENERATED_COUNT = new AtomicInteger();

    public DesertPyramidFeature(Codec<NoFeatureConfig> codec) {
        super(codec);
    }

    /**
     * @return The number of pyramids that have been generated since the server was started.
     */
    public static int getGeneratedCount() {
        return GENERATED_COUNT.get();
    }

    @Override
    public boolean place(@Nonnull ISeedReader world, @Nonnull ChunkGenerator generator, @Nonnull Random random,
                         @Nonnull BlockPos origin, @Nonnull NoFeatureConfig config) {
        BlockPos surface = world.getHeightmapPos(Heightmap.Type.WORLD_SURFACE_WG, origin);
        BlockPos adjustedBase = findStableGround(world, surface);
        if (adjustedBase == null) {
            return false;
        }

        BlockPos baseCenter = new BlockPos(adjustedBase.getX(), adjustedBase.getY(), adjustedBase.getZ());

        if (baseCenter.getY() <= generator.getSeaLevel()) {
            return false;
        }

        int halfWidth = BASE_WIDTH / 2;
        int structureHeight = halfWidth + 1;
        if (baseCenter.getY() + structureHeight + 2 >= generator.getGenDepth()) {
            return false;
        }

        if (!isAreaSuitable(world, baseCenter, halfWidth)) {
            return false;
        }

        buildPyramid(world, baseCenter, halfWidth, structureHeight);

        int count = GENERATED_COUNT.incrementAndGet();
        LOGGER.info("Generated desert pyramid at {} (total: {})", baseCenter, count);
        return true;
    }

    private boolean isAreaSuitable(ISeedReader world, BlockPos center, int halfWidth) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int baseY = center.getY();
        for (int dx = -halfWidth; dx <= halfWidth; dx++) {
            for (int dz = -halfWidth; dz <= halfWidth; dz++) {
                BlockPos columnTop = world.getHeightmapPos(Heightmap.Type.WORLD_SURFACE_WG, center.offset(dx, 0, dz));
                BlockPos stableGround = findStableGround(world, columnTop);
                if (stableGround == null) {
                    return false;
                }

                int columnY = stableGround.getY();
                if (Math.abs(columnY - baseY) > MAX_TERRAIN_VARIATION) {
                    return false;
                }

                if (!world.getFluidState(stableGround).isEmpty()) {
                    return false;
                }

                mutable.set(stableGround.getX(), columnY + 1, stableGround.getZ());
                if (!world.isEmptyBlock(mutable)) {
                    BlockState above = world.getBlockState(mutable);
                    if (!above.getMaterial().isReplaceable()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private BlockPos findStableGround(ISeedReader world, BlockPos start) {
        BlockPos current = start;
        BlockState state = world.getBlockState(current);
        for (int depth = 0; depth < FOUNDATION_DEPTH && current.getY() > 0; depth++) {
            if (isDesertGround(state)) {
                return current;
            }

            if (state.getMaterial().isSolid() && !state.getMaterial().isReplaceable()) {
                return null;
            }

            current = current.below();
            state = world.getBlockState(current);
        }

        return isDesertGround(state) ? current : null;
    }

    private boolean isDesertGround(BlockState state) {
        return state.is(BlockTags.SAND) || state.getBlock() == Blocks.SANDSTONE || state.getBlock() == Blocks.CUT_SANDSTONE
                || state.getBlock() == Blocks.SMOOTH_SANDSTONE || state.getBlock() == Blocks.CHISELED_SANDSTONE;
    }

    private void buildPyramid(ISeedReader world, BlockPos center, int halfWidth, int height) {
        BlockState sandstone = Blocks.SANDSTONE.defaultBlockState();
        BlockState smoothSandstone = Blocks.SMOOTH_SANDSTONE.defaultBlockState();
        BlockState chiseledSandstone = Blocks.CHISELED_SANDSTONE.defaultBlockState();

        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int baseY = center.getY();

        for (int level = 0; level <= height; level++) {
            int layerHalf = Math.max(0, halfWidth - level);
            for (int dx = -layerHalf; dx <= layerHalf; dx++) {
                for (int dz = -layerHalf; dz <= layerHalf; dz++) {
                    int x = center.getX() + dx;
                    int y = baseY + level;
                    int z = center.getZ() + dz;
                    mutable.set(x, y, z);

                    BlockState blockToPlace;
                    if (level == height) {
                        blockToPlace = chiseledSandstone;
                    } else if (level == 0) {
                        blockToPlace = smoothSandstone;
                    } else if (Math.abs(dx) == layerHalf || Math.abs(dz) == layerHalf) {
                        blockToPlace = smoothSandstone;
                    } else {
                        blockToPlace = sandstone;
                    }

                    world.setBlock(mutable, blockToPlace, 2);

                    if (level == 0) {
                        reinforceFoundation(world, x, baseY - 1, z);
                    }
                }
            }
        }

        carveEntrance(world, center, baseY, halfWidth);
        carveInterior(world, center, baseY, height);
        placeTreasure(world, center, baseY + 1);
    }

    private void reinforceFoundation(ISeedReader world, int x, int startY, int z) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int depth = 0; depth < FOUNDATION_DEPTH; depth++) {
            int y = startY - depth;
            if (y < 0) {
                break;
            }
            mutable.set(x, y, z);
            if (!world.isEmptyBlock(mutable) && world.getBlockState(mutable).getMaterial().isSolid()) {
                break;
            }
            world.setBlock(mutable, Blocks.SANDSTONE.defaultBlockState(), 2);
        }
    }

    private void carveEntrance(ISeedReader world, BlockPos center, int baseY, int halfWidth) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int entranceHeight = 3;
        int entranceDepth = 2;
        int z = center.getZ() - halfWidth;
        for (int dy = 1; dy <= entranceHeight; dy++) {
            for (int dz = 0; dz <= entranceDepth; dz++) {
                mutable.set(center.getX(), baseY + dy, z + dz);
                world.setBlock(mutable, Blocks.AIR.defaultBlockState(), 2);
            }
        }
        world.setBlock(center.offset(0, 1, -halfWidth), Blocks.CUT_SANDSTONE.defaultBlockState(), 2);
    }

    private void carveInterior(ISeedReader world, BlockPos center, int baseY, int height) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int chamberHalf = Math.max(1, height - 3);
        for (int dx = -chamberHalf; dx <= chamberHalf; dx++) {
            for (int dz = -chamberHalf; dz <= chamberHalf; dz++) {
                for (int dy = 1; dy <= chamberHalf; dy++) {
                    mutable.set(center.getX() + dx, baseY + dy, center.getZ() + dz);
                    world.setBlock(mutable, Blocks.AIR.defaultBlockState(), 2);
                }
            }
        }
    }

    private void placeTreasure(ISeedReader world, BlockPos center, int chestY) {
        BlockPos chestPos = new BlockPos(center.getX(), chestY, center.getZ());
        world.setBlock(chestPos, Blocks.CHEST.defaultBlockState(), 2);
    }
}