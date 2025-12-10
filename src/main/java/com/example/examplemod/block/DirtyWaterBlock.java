package com.example.examplemod.block;

import com.example.examplemod.ModFluids;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.particles.DustParticleData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import java.util.Random;

public class DirtyWaterBlock extends FlowingFluidBlock {
    private static final DustParticleData BLACK_DOT = new DustParticleData(0.0F, 0.0F, 0.0F, 0.05F);

    public DirtyWaterBlock() {
        super(() -> (ForgeFlowingFluid) ModFluids.DIRTY_WATER.get(),
                AbstractBlock.Properties.copy(net.minecraft.block.Blocks.WATER));
    }

    @Override
    public void animateTick(BlockState state, World world, BlockPos pos, Random random) {
        super.animateTick(state, world, pos, random);

        if (random.nextFloat() < 0.4F) {
            double offsetX = random.nextDouble();
            double offsetY = random.nextDouble() * 0.2D + 0.3D;
            double offsetZ = random.nextDouble();
            // spawn a single pixel-sized dot that floats slightly upward
            world.addParticle(BLACK_DOT,
                    pos.getX() + offsetX,
                    pos.getY() + offsetY,
                    pos.getZ() + offsetZ,
                    0, 0.005, 0);
        }
    }
}
