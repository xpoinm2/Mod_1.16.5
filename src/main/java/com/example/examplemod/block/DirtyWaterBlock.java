package com.example.examplemod.block;

import com.example.examplemod.ModFluids;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import java.util.Random;

public class DirtyWaterBlock extends FlowingFluidBlock {
    public DirtyWaterBlock() {
        super(() -> (ForgeFlowingFluid) ModFluids.DIRTY_WATER.get(),
                AbstractBlock.Properties.copy(net.minecraft.block.Blocks.WATER));
    }

    @Override
    public void animateTick(BlockState state, World world, BlockPos pos, Random random) {
        super.animateTick(state, world, pos, random);

        if (random.nextFloat() < 0.3F) {
            double offsetX = random.nextDouble();
            double offsetY = random.nextDouble() * 0.3D + 0.2D;
            double offsetZ = random.nextDouble();
            // very small black dots drifting up slowly
            world.addParticle(ParticleTypes.SQUID_INK,
                    pos.getX() + offsetX,
                    pos.getY() + offsetY,
                    pos.getZ() + offsetZ,
                    0, 0.01, 0);
        }
    }
}
