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
    public static final int BASE_GUI_WIDTH = 140;
    public static final int BASE_GUI_HEIGHT = 140;
    public static final int BASE_SLOT_X = 61;
    public static final int BASE_SLOT_Y = 52;
    public static final int SLOT_SPACING = 18;
    public static final int GUI_HEIGHT = 166;
    public static final float GUI_SCALE = (float) GUI_HEIGHT / BASE_GUI_HEIGHT;
    public static final int GUI_WIDTH = Math.round(BASE_GUI_WIDTH * GUI_SCALE);
    public static final int SLOT_X = Math.round(BASE_SLOT_X * GUI_SCALE);
    public static final int SLOT_Y = Math.round(BASE_SLOT_Y * GUI_SCALE);
    public static final int SLOT_SPACING_SCALED = Math.round(SLOT_SPACING * GUI_SCALE);
    private final IItemHandler itemHandler;

    public BoneTongsContainer(int windowId, PlayerInventory playerInventory, PacketBuffer data) {
        this(windowId, playerInventory, data.readItem());
    }

    public BoneTongsContainer(int windowId, PlayerInventory playerInventory, ItemStack boneStack) {
        super(ModContainers.BONE_TONGS.get(), windowId);
        this.itemHandler = boneStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                .orElseGet(() -> new BoneTongsCapabilityProvider(boneStack).getHandler());

        addSlot(new SlotItemHandler(itemHandler, 0, SLOT_X, SLOT_Y));
        addSlot(new SlotItemHandler(itemHandler, 1, SLOT_X, SLOT_Y + SLOT_SPACING_SCALED));
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
