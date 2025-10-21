package com.example.examplemod.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.util.IWorldPosCallable;

/**
 * A crafting table container that stays valid even without a physical block.
 * This allows the portable crafting menu opened from the X menu to work like a
 * normal workbench.
 */
public class PortableWorkbenchContainer extends WorkbenchContainer {

    public PortableWorkbenchContainer(int windowId, PlayerInventory inventory) {
        super(windowId, inventory, IWorldPosCallable.NULL);
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        // Keep the container open regardless of player position or block state.
        return true;
    }
}