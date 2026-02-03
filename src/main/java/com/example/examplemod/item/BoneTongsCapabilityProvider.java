package com.example.examplemod.item;

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
import java.util.Random;

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
        private final Random random = new Random();

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
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (stack.isEmpty()) {
                return super.insertItem(slot, stack, simulate);
            }
            boolean isHot = isHotItem(stack);
            ItemStack remaining = super.insertItem(slot, stack, simulate);
            if (!simulate && isHot && remaining.getCount() != stack.getCount()) {
                damageTongs();
            }
            return remaining;
        }

        private boolean isHotItem(ItemStack stack) {
            if (stack.isEmpty()) {
                return false;
            }
            if (stack.getItem() instanceof RoastedOreItem) {
                return RoastedOreItem.getState(stack) == RoastedOreItem.STATE_HOT;
            }
            if (stack.getItem() instanceof HotRoastedOreItem) {
                return HotRoastedOreItem.getState(stack) == HotRoastedOreItem.STATE_HOT;
            }
            if (stack.getItem() instanceof SpongeMetalItem) {
                return SpongeMetalItem.getState(stack) == SpongeMetalItem.STATE_HOT;
            }
            if (stack.getItem() instanceof MetalChunkItem) {
                return MetalChunkItem.getTemperature(stack) == MetalChunkItem.TEMP_HOT;
            }
            return false;
        }

        private void damageTongs() {
            if (parentStack.isEmpty() || !parentStack.isDamageableItem()) {
                return;
            }
            if (parentStack.hurt(1, random, null)) {
                parentStack.shrink(1);
            }
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
