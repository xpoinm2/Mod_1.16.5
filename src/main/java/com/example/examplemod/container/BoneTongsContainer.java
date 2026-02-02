package com.example.examplemod.container;

import com.example.examplemod.ModContainers;
import com.example.examplemod.item.BoneTongsCapabilityProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class BoneTongsContainer extends Container {
    public static final int TONGS_SLOT_COUNT = 2;
    public static final int TONGS_ROWS = 1;
    public static final int GUI_WIDTH = 176;
    public static final int GUI_HEIGHT = 114 + TONGS_ROWS * 18;
    public static final int SLOT_SPACING = 18;
    public static final int SLOT_X = (GUI_WIDTH - (TONGS_SLOT_COUNT * SLOT_SPACING)) / 2;
    public static final int SLOT_Y = 18;
    public static final int PLAYER_INV_X = 8;
    public static final int PLAYER_INV_Y = TONGS_ROWS * 18 + 30;
    public static final int HOTBAR_Y = PLAYER_INV_Y + 58;
    private final IItemHandler itemHandler;

    public BoneTongsContainer(int windowId, PlayerInventory playerInventory, PacketBuffer data) {
        this(windowId, playerInventory, data.readItem());
    }

    public BoneTongsContainer(int windowId, PlayerInventory playerInventory, ItemStack boneStack) {
        super(ModContainers.BONE_TONGS.get(), windowId);
        this.itemHandler = boneStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                .orElseGet(() -> new BoneTongsCapabilityProvider(boneStack).getHandler());

        addSlot(new SlotItemHandler(itemHandler, 0, SLOT_X, SLOT_Y));
        addSlot(new SlotItemHandler(itemHandler, 1, SLOT_X + SLOT_SPACING, SLOT_Y));

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, PLAYER_INV_X + col * 18,
                        PLAYER_INV_Y + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, PLAYER_INV_X + col * 18, HOTBAR_Y));
        }
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return true;
    }

    @Nonnull
    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();

            int playerInvStart = TONGS_SLOT_COUNT;
            int playerInvEnd = playerInvStart + 27;
            int hotbarStart = playerInvEnd;
            int hotbarEnd = hotbarStart + 9;

            if (index < playerInvStart) {
                if (!this.moveItemStackTo(stack, playerInvStart, hotbarEnd, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= playerInvStart && index < playerInvEnd) {
                if (!this.moveItemStackTo(stack, 0, TONGS_SLOT_COUNT, false)) {
                    if (!this.moveItemStackTo(stack, hotbarStart, hotbarEnd, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (index >= hotbarStart && index < hotbarEnd) {
                if (!this.moveItemStackTo(stack, 0, TONGS_SLOT_COUNT, false)) {
                    if (!this.moveItemStackTo(stack, playerInvStart, playerInvEnd, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stack.getCount() == result.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stack);
        }
        return result;
    }
}
