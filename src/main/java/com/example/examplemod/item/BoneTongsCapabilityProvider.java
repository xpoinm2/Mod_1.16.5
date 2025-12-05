package com.example.examplemod.item;

import com.example.examplemod.item.HotRoastedOreItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BoneTongsCapabilityProvider implements ICapabilitySerializable<CompoundNBT> {
    public static final String INVENTORY_TAG = "BoneTongsInventory";

    private final ItemStack stack;
    private final BoneTongsItemHandler handler;
    private final LazyOptional<IItemHandler> optional;

    public BoneTongsCapabilityProvider(ItemStack stack) {
        this.stack = stack;
        this.handler = new BoneTongsItemHandler(stack);
        this.optional = LazyOptional.of(() -> this.handler);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(cap, optional);
    }

    @Override
    public CompoundNBT serializeNBT() {
        return handler.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        handler.deserializeNBT(nbt);
    }

    public BoneTongsItemHandler getHandler() {
        return handler;
    }

    private static class BoneTongsItemHandler extends ItemStackHandler {
        private final ItemStack parentStack;

        private BoneTongsItemHandler(ItemStack stack) {
            super(2);
            this.parentStack = stack;
            CompoundNBT tag = stack.getOrCreateTag();
            if (tag.contains(INVENTORY_TAG, Constants.NBT.TAG_COMPOUND)) {
                super.deserializeNBT(tag.getCompound(INVENTORY_TAG));
            }
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            CompoundNBT tag = parentStack.getOrCreateTag();
            tag.put(INVENTORY_TAG, serializeNBT());
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.getItem() instanceof HotRoastedOreItem;
        }

        @Override
        public CompoundNBT serializeNBT() {
            return super.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            if (nbt != null) {
                super.deserializeNBT(nbt);
            }
        }
    }
}
