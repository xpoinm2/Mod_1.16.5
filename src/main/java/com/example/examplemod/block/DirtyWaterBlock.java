package com.example.examplemod.block;

import com.example.examplemod.ModFluids;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import java.util.Random;

public class DirtyWaterBlock extends FlowingFluidBlock {
    private static final RedstoneParticleData BLACK_DOT = new RedstoneParticleData(0.02F, 0.02F, 0.02F, 0.16F);

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
    }
}
