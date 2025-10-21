package com.example.examplemod.tileentity;

import com.example.examplemod.ModTileEntities;
import com.example.examplemod.block.ClayPotBlock;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ClayPotTileEntity extends TileEntity {
    public static final int CAPACITY = 8000;

    private final FluidTank tank = new FluidTank(CAPACITY) {
        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();
            ClayPotTileEntity.this.setChanged();
            if (level != null && !level.isClientSide) {
                BlockState previous = getBlockState();
                updateFillLevel();
                level.sendBlockUpdated(worldPosition, previous, getBlockState(), 3);
            }
        }

        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid().isSame(Fluids.WATER);
        }
    };

    private final LazyOptional<IFluidHandler> fluidCapability = LazyOptional.of(() -> tank);

    public ClayPotTileEntity() {
        super(ModTileEntities.CLAY_POT.get());
    }

    public FluidTank getTank() {
        return tank;
    }

    public void clear() {
        tank.setFluid(FluidStack.EMPTY);
        setChanged();
    }

    private void updateFillLevel() {
        if (level == null) {
            return;
        }

        BlockState state = getBlockState();
        if (!state.hasProperty(ClayPotBlock.FILL_LEVEL)) {
            return;
        }

        int fill = MathHelper.clamp((tank.getFluidAmount() * 8) / CAPACITY, 0, 8);
        if (state.getValue(ClayPotBlock.FILL_LEVEL) != fill) {
            level.setBlock(worldPosition, state.setValue(ClayPotBlock.FILL_LEVEL, fill), 3);
        }
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        tank.readFromNBT(nbt.getCompound("Tank"));
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        super.save(nbt);
        nbt.put("Tank", tank.writeToNBT(new CompoundNBT()));
        return nbt;
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(worldPosition, 0, getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return save(new CompoundNBT());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        load(getBlockState(), pkt.getTag());
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide) {
            updateFillLevel();
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull net.minecraftforge.common.capabilities.Capability<T> cap, @Nullable net.minecraft.util.Direction side) {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return fluidCapability.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        fluidCapability.invalidate();
    }
}