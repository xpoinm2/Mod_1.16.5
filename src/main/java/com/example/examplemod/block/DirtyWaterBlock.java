package com.example.examplemod.block;

import com.example.examplemod.ModFluids;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class DirtyWaterBlock extends FlowingFluidBlock {
    public DirtyWaterBlock() {
        super(() -> (ForgeFlowingFluid) ModFluids.DIRTY_WATER.get(),
                net.minecraft.block.AbstractBlock.Properties.copy(net.minecraft.block.Blocks.WATER));
    }

    @Override
    public void entityInside(BlockState state, World world, BlockPos pos, Entity entity) {
        super.entityInside(state, world, pos, entity);

        if (entity instanceof LivingEntity && !world.isClientSide) {
            LivingEntity livingEntity = (LivingEntity) entity;

            // Apply nausea effect occasionally when in dirty water
            if (world.random.nextFloat() < 0.01F) { // 1% chance per tick
                livingEntity.addEffect(new EffectInstance(Effects.CONFUSION, 200, 0)); // Nausea for 10 seconds
            }
        }
    }
}
