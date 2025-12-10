package com.example.examplemod.fluid;

import com.example.examplemod.ModBlocks;
import com.example.examplemod.ModFluids;
import com.example.examplemod.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public abstract class DirtyWaterFluid extends ForgeFlowingFluid {
    private static final Properties PROPERTIES = new Properties(
            ModFluids.DIRTY_WATER::get,
            ModFluids.DIRTY_WATER_FLOWING::get,
            FluidAttributes.builder(
                    new ResourceLocation("minecraft", "block/water_still"),
                    new ResourceLocation("minecraft", "block/water_flow"))
                    .translationKey("fluid.examplemod.dirty_water")
                    .color(0xFF8B8B8B)
                    .temperature(300)
                    .viscosity(1000)
                    .density(1000)
                    .luminosity(0))
            .block(() -> (FlowingFluidBlock) ModBlocks.DIRTY_WATER_BLOCK.get());

    public DirtyWaterFluid() {
        super(PROPERTIES);
    }

    @Override
    public Fluid getFlowing() {
        return ModFluids.DIRTY_WATER_FLOWING.get();
    }

    @Override
    public Fluid getSource() {
        return ModFluids.DIRTY_WATER.get();
    }


    @Override
    protected boolean canConvertToSource() {
        return true;
    }

    @Override
    protected void beforeDestroyingBlock(IWorld world, BlockPos pos, BlockState state) {
        // Convert to water block when destroying blocks like grass
        world.setBlock(pos, Blocks.WATER.defaultBlockState(), 3);
    }

    @Override
    protected int getSlopeFindDistance(IWorldReader world) {
        return 4;
    }

    @Override
    public int getTickDelay(IWorldReader world) {
        return 5;
    }

    @Override
    protected float getExplosionResistance() {
        return 100.0F;
    }

    public static class Source extends DirtyWaterFluid {
        public Source() {
            super();
        }

        @Override
        public boolean isSource(FluidState state) {
            return true;
        }

        @Override
        public int getAmount(FluidState state) {
            return 8;
        }
    }

    public static class Flowing extends DirtyWaterFluid {
        public Flowing() {
            super();
        }

        @Override
        protected void createFluidStateDefinition(StateContainer.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        @Override
        public boolean isSource(FluidState state) {
            return false;
        }

        @Override
        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }
    }
}
