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
}
