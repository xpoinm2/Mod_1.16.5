package com.example.examplemod.container;

import com.example.examplemod.ModContainers;
import com.example.examplemod.item.BoneTongsCapabilityProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class BoneTongsContainer extends Container {
    public static final int TONGS_SLOT_COUNT = 2;
    private final IItemHandler itemHandler;

    public BoneTongsContainer(int windowId, PlayerInventory playerInventory, PacketBuffer data) {
        this(windowId, playerInventory, data.readItem());
    }

    public BoneTongsContainer(int windowId, PlayerInventory playerInventory, ItemStack boneStack) {
        super(ModContainers.BONE_TONGS.get(), windowId);
        this.itemHandler = boneStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                .orElseGet(() -> new BoneTongsCapabilityProvider(boneStack).getHandler());

        addSlot(new SlotItemHandler(itemHandler, 0, 30, 18));
        addSlot(new SlotItemHandler(itemHandler, 1, 30, 38));
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return true;
    }

    @Nonnull
    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        return ItemStack.EMPTY;
    }
}
