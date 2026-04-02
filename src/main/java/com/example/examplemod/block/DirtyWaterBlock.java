package com.example.examplemod.block;

import com.example.examplemod.ModFluids;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import java.util.Random;

public class DirtyWaterBlock extends FlowingFluidBlock {
    private static final RedstoneParticleData BLACK_DOT = new RedstoneParticleData(0.02F, 0.02F, 0.02F, 0.16F);
    private static final RedstoneParticleData BLUE_PIXEL = new RedstoneParticleData(0.12F, 0.55F, 1.0F, 0.28F);

    public DirtyWaterBlock() {
        super(() -> (ForgeFlowingFluid) ModFluids.DIRTY_WATER.get(),
                AbstractBlock.Properties.copy(net.minecraft.block.Blocks.WATER));
    }

    @Override
    public void animateTick(BlockState state, World world, BlockPos pos, Random random) {
        super.animateTick(state, world, pos, random);

        if (random.nextFloat() < 0.8F) {
            int particles = 2 + random.nextInt(3); // 2-4 dots to keep them dense
            for (int i = 0; i < particles; i++) {
                double offsetX = random.nextDouble();
                double offsetY = random.nextDouble() * 0.25D + 0.2D;
                double offsetZ = random.nextDouble();
                // keep dots mostly still so they appear longer
                world.addParticle(BLACK_DOT,
                        pos.getX() + offsetX,
                        pos.getY() + offsetY,
                        pos.getZ() + offsetZ,
                        0, 0.002, 0);
            }
        }

        spawnFlowOnNeighborFaces(world, pos, random);
    }

    private static void spawnFlowOnNeighborFaces(World world, BlockPos fluidPos, Random random) {
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = fluidPos.relative(direction);
            BlockState neighborState = world.getBlockState(neighborPos);
            if (!neighborState.getMaterial().isSolidBlocking()) {
                continue;
            }

            if (random.nextFloat() > 0.6F) {
                continue;
            }

            int droplets = 1 + random.nextInt(2);
            for (int i = 0; i < droplets; i++) {
                spawnFaceDroplet(world, neighborPos, direction.getOpposite(), random);
            }
        }
    }

    private static void spawnFaceDroplet(World world, BlockPos blockPos, Direction face, Random random) {
        double inset = 0.003D;
        double u = random.nextDouble();
        double v = random.nextDouble();

        double x = blockPos.getX() + 0.5D;
        double y = blockPos.getY() + 0.5D;
        double z = blockPos.getZ() + 0.5D;

        double dx = 0.0D;
        double dy = 0.0D;
        double dz = 0.0D;

        long t = world.getGameTime() + (blockPos.asLong() & 31L);
        double phase = (t % 40L) / 40.0D;

        switch (face) {
            case UP:
                x = blockPos.getX() + u;
                y = blockPos.getY() + 1.0D - inset;
                z = blockPos.getZ() + v;
                dx = (phase - 0.5D) * 0.01D;
                dz = (0.5D - phase) * 0.01D;
                break;
            case DOWN:
                x = blockPos.getX() + u;
                y = blockPos.getY() + inset;
                z = blockPos.getZ() + v;
                dx = (0.5D - phase) * 0.01D;
                dz = (phase - 0.5D) * 0.01D;
                break;
            case NORTH:
                x = blockPos.getX() + u;
                y = blockPos.getY() + v;
                z = blockPos.getZ() + inset;
                dx = (phase - 0.5D) * 0.008D;
                dy = -0.012D;
                break;
            case SOUTH:
                x = blockPos.getX() + u;
                y = blockPos.getY() + v;
                z = blockPos.getZ() + 1.0D - inset;
                dx = (0.5D - phase) * 0.008D;
                dy = -0.012D;
                break;
            case WEST:
                x = blockPos.getX() + inset;
                y = blockPos.getY() + v;
                z = blockPos.getZ() + u;
                dz = (phase - 0.5D) * 0.008D;
                dy = -0.012D;
                break;
            case EAST:
            default:
                x = blockPos.getX() + 1.0D - inset;
                y = blockPos.getY() + v;
                z = blockPos.getZ() + u;
                dz = (0.5D - phase) * 0.008D;
                dy = -0.012D;
                break;
        }

        world.addParticle(BLUE_PIXEL, x, y, z, dx, dy, dz);
    }
}
