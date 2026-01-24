package com.example.examplemod.tileentity;

import com.example.examplemod.ModTileEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CobblestoneAnvilTileEntity extends TileEntity {
    public static final int METAL_SLOT = 0;
    public static final int TOOL_SLOT = 1;
    public static final int OUTPUT_SLOT = 2;
    public static final int TOTAL_SLOTS = 3;
    public static final int MAX_PROGRESS = 8;

    private int progress = 0;

    private final ItemStackHandler inventory = new ItemStackHandler(TOTAL_SLOTS) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            CobblestoneAnvilTileEntity.this.setChanged();
            // Отправляем обновление клиенту
            if (CobblestoneAnvilTileEntity.this.level != null && !CobblestoneAnvilTileEntity.this.level.isClientSide) {
                CobblestoneAnvilTileEntity.this.level.sendBlockUpdated(
                    CobblestoneAnvilTileEntity.this.worldPosition,
                    CobblestoneAnvilTileEntity.this.getBlockState(),
                    CobblestoneAnvilTileEntity.this.getBlockState(), 3);
            }
        }
    };

    private final LazyOptional<IItemHandler> inventoryCapability = LazyOptional.of(() -> inventory);

    public CobblestoneAnvilTileEntity() {
        super(ModTileEntities.COBBLESTONE_ANVIL.get());
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        inventory.deserializeNBT(nbt.getCompound("Inventory"));
        progress = nbt.getInt("Progress");
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        super.save(nbt);
        nbt.put("Inventory", inventory.serializeNBT());
        nbt.putInt("Progress", progress);
        return nbt;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = Math.max(0, Math.min(MAX_PROGRESS, progress));
        setChanged();
    }

    public void incrementProgress() {
        if (progress < MAX_PROGRESS) {
            progress++;
            setChanged();
        }
    }

    public boolean isComplete() {
        return progress >= MAX_PROGRESS;
    }

    public void resetProgress() {
        progress = 0;
        setChanged();
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
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        super.handleUpdateTag(state, tag);
        load(state, tag);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull net.minecraftforge.common.capabilities.Capability<T> cap, @Nullable net.minecraft.util.Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return inventoryCapability.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        inventoryCapability.invalidate();
    }

    public void dropInventoryContents() {
        if (level == null || level.isClientSide) {
            return;
        }
        double dropY = worldPosition.getY() + 1.1D;
        BlockState state = getBlockState();
        VoxelShape shape = state.getShape(level, worldPosition);
        if (!shape.isEmpty()) {
            dropY = worldPosition.getY() + shape.max(Direction.Axis.Y) + 0.6D;
        }
        double dropX = worldPosition.getX() + 0.5D;
        double dropZ = worldPosition.getZ() + 0.5D;
        if (state.hasProperty(HorizontalBlock.FACING)) {
            Direction facing = state.getValue(HorizontalBlock.FACING);
            dropX += facing.getStepX() * 0.35D;
            dropZ += facing.getStepZ() * 0.35D;
        }
        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (!stack.isEmpty()) {
                InventoryHelper.dropItemStack(level,
                        dropX,
                        dropY,
                        dropZ,
                        stack);
                inventory.setStackInSlot(slot, ItemStack.EMPTY);
            }
        }
    }
}