package com.example.examplemod.tileentity;

import com.example.examplemod.ModTileEntities;
import com.example.examplemod.ModFluids;
import com.example.examplemod.block.ClayPotBlock;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.inventory.InventoryHelper;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

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

    public static final int INV_SLOTS = 9;
    public static final int FLUID_INPUT_SLOT = INV_SLOTS;
    public static final int FLUID_OUTPUT_SLOT = INV_SLOTS + 1;
    public static final int TOTAL_SLOTS = FLUID_OUTPUT_SLOT + 1;
    private static final String NBT_DRAIN_MODE = "DrainMode";

    private boolean drainMode = false;

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

    private final ItemStackHandler inventory = new ItemStackHandler(TOTAL_SLOTS) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            ClayPotTileEntity.this.setChanged();
        }
    };

    private final LazyOptional<IItemHandler> inventoryCapability = LazyOptional.of(() -> inventory);

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

    public boolean isDrainMode() {
        return drainMode;
    }

    public void setDrainMode(boolean drain) {
        if (drainMode == drain) {
            return;
        }
        drainMode = drain;
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public void toggleDrainMode() {
        setDrainMode(!drainMode);
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
        for (ServerPlayerEntity player : serverWorld.getPlayers(pred -> true)) {
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
        inventory.deserializeNBT(getInventoryTagWithMinimumSize(nbt.getCompound("Inventory")));
        tank.readFromNBT(nbt.getCompound("Tank"));
        oreWashCount = nbt.getInt("WashCount");
        lastKnownFluid = tank.getFluid().getFluid();
        if (nbt.contains(NBT_DRAIN_MODE)) {
            drainMode = nbt.getBoolean(NBT_DRAIN_MODE);
        }
    }

    private CompoundNBT getInventoryTagWithMinimumSize(CompoundNBT tag) {
        CompoundNBT copy = tag.copy();
        int savedSize = copy.getInt("Size");
        if (savedSize < TOTAL_SLOTS) {
            copy.putInt("Size", TOTAL_SLOTS);
        }
        return copy;
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        super.save(nbt);
        nbt.put("Inventory", inventory.serializeNBT());
        nbt.put("Tank", tank.writeToNBT(new CompoundNBT()));
        nbt.putInt("WashCount", oreWashCount);
        nbt.putBoolean(NBT_DRAIN_MODE, drainMode);
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
        if (level != null && level.isClientSide) {
            updateFillLevel();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        super.handleUpdateTag(state, tag);
        load(state, tag);
        if (level != null) {
            updateFillLevel();
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide) {
            updateFillLevel();
        }
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull net.minecraftforge.common.capabilities.Capability<T> cap, @Nullable net.minecraft.util.Direction side) {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return fluidCapability.cast();
        }
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return inventoryCapability.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        fluidCapability.invalidate();
        inventoryCapability.invalidate();
    }

    public void dropInventoryContents() {
        if (level == null || level.isClientSide) {
            return;
        }
        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (!stack.isEmpty()) {
                InventoryHelper.dropItemStack(level,
                        worldPosition.getX() + 0.5D,
                        worldPosition.getY() + 0.5D,
                        worldPosition.getZ() + 0.5D,
                        stack);
                inventory.setStackInSlot(slot, ItemStack.EMPTY);
            }
        }
    }

    public boolean tryProcessFluidSlots() {
        if (level == null || level.isClientSide) {
            return false;
        }
        ItemStack input = inventory.getStackInSlot(FLUID_INPUT_SLOT);
        ItemStack output = inventory.getStackInSlot(FLUID_OUTPUT_SLOT);
        if (input.isEmpty() || !output.isEmpty()) {
            return false;
        }
        LazyOptional<IFluidHandlerItem> containerCap = input.getCapability(
                CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY);
        IFluidHandlerItem containerHandler = containerCap.orElse(null);
        if (containerHandler == null) {
            return false;
        }

        boolean processed = false;
        if (drainMode) {
            FluidStack simulated = containerHandler.drain(CAPACITY, FluidAction.SIMULATE);
            if (!simulated.isEmpty()) {
                int accepted = tank.fill(simulated, FluidAction.SIMULATE);
                if (accepted > 0) {
                    FluidStack drained = containerHandler.drain(accepted, FluidAction.EXECUTE);
                    tank.fill(drained, FluidAction.EXECUTE);
                    processed = drained.getAmount() > 0;
                }
            }
        } else {
            FluidStack available = tank.getFluid();
            if (!available.isEmpty()) {
                FluidStack toFill = available.copy();
                int filled = containerHandler.fill(toFill, FluidAction.SIMULATE);
                if (filled > 0) {
                    toFill.setAmount(filled);
                    containerHandler.fill(toFill, FluidAction.EXECUTE);
                    tank.drain(filled, FluidAction.EXECUTE);
                    processed = true;
                }
            }
        }

        if (!processed) {
            return false;
        }

        ItemStack moved = inventory.extractItem(FLUID_INPUT_SLOT, 1, false);
        if (!moved.isEmpty()) {
            inventory.setStackInSlot(FLUID_OUTPUT_SLOT, moved);
        }
        return true;
    }
}