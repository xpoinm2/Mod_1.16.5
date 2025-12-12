package com.example.examplemod.tileentity;

import com.example.examplemod.ModTileEntities;
import com.example.examplemod.ModFluids;
import com.example.examplemod.block.ClayPotBlock;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.server.level.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
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
    private static final int WASHES_BEFORE_DIRTY = 12;
    private int oreWashCount = 0;
    private Fluid lastKnownFluid = Fluids.EMPTY;

       // Добавлено: getter для TESR (строки 45-48)
               public int getWaterLevel() {
               return MathHelper.clamp((tank.getFluidAmount() * 8) / CAPACITY, 0, 8);
           }

    private final FluidTank tank = new FluidTank(CAPACITY) {
        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();
            ClayPotTileEntity.this.handleTankContentsChanged();
            ClayPotTileEntity.this.setChanged();
            if (level != null && !level.isClientSide) {
                BlockState previous = getBlockState();
                updateFillLevel();
                level.sendBlockUpdated(worldPosition, previous, getBlockState(), 3);
                // Добавлено: extra update для TESR (строки 60-61)
                level.blockUpdated(worldPosition, getBlockState().getBlock());
            }
        }

        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid().isSame(Fluids.WATER) || stack.getFluid().isSame(ModFluids.DIRTY_WATER.get());
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

        int fill = getWaterLevel();  // Добавлено: через getter (строка 86)
        if (state.getValue(ClayPotBlock.FILL_LEVEL) != fill) {
            level.setBlock(worldPosition, state.setValue(ClayPotBlock.FILL_LEVEL, fill), 3);
        }
    }

    private void handleTankContentsChanged() {
        Fluid current = tank.getFluid().getFluid();
        if (!current.isSame(lastKnownFluid)) {
            lastKnownFluid = current;
            oreWashCount = 0;
        }
    }

    public boolean canWashOre() {
        FluidStack fluid = tank.getFluid();
        return fluid.getAmount() >= CAPACITY && fluid.getFluid().isSame(Fluids.WATER);
    }

    public void recordOreWash() {
        if (!canWashOre()) {
            return;
        }
        oreWashCount++;
        if (oreWashCount >= WASHES_BEFORE_DIRTY) {
            int amount = tank.getFluidAmount();
            if (amount > 0) {
                tank.setFluid(new FluidStack(ModFluids.DIRTY_WATER.get(), amount));
                notifyFluidTypeChanged();
            }
            oreWashCount = 0;
        }
    }

    private void notifyFluidTypeChanged() {
        if (level == null || level.isClientSide) {
            return;
        }
        setChanged();
        BlockState previous = getBlockState();
        updateFillLevel();
        level.sendBlockUpdated(worldPosition, previous, getBlockState(), 3);
        level.blockUpdated(worldPosition, getBlockState().getBlock());
        broadcastFluidUpdate();
    }

    private void broadcastFluidUpdate() {
        if (!(level instanceof ServerWorld)) {
            return;
        }
        ServerWorld serverWorld = (ServerWorld) level;
        SUpdateTileEntityPacket packet = getUpdatePacket();
        if (packet == null) {
            return;
        }
        for (ServerPlayerEntity player : serverWorld.getPlayers()) {
            if (player.distanceToSqr(worldPosition.getX() + 0.5D,
                    worldPosition.getY() + 0.5D,
                    worldPosition.getZ() + 0.5D) <= 4096D) {
                player.connection.send(packet);
            }
        }
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        tank.readFromNBT(nbt.getCompound("Tank"));
        oreWashCount = nbt.getInt("WashCount");
        lastKnownFluid = tank.getFluid().getFluid();
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        super.save(nbt);
        nbt.put("Tank", tank.writeToNBT(new CompoundNBT()));
        nbt.putInt("WashCount", oreWashCount);
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