package com.example.examplemod.container;

import com.example.examplemod.ModBlocks;
import com.example.examplemod.ModContainers;
import com.example.examplemod.ModItems;
import com.example.examplemod.item.SpongeMetalItem;
import com.example.examplemod.tileentity.CobblestoneAnvilTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class CobblestoneAnvilContainer extends Container {
    private final CobblestoneAnvilTileEntity tileEntity;
    private final PlayerEntity player;
    private final IItemHandler playerInventory;

    public CobblestoneAnvilContainer(int windowId, PlayerInventory playerInventory, PacketBuffer buffer) {
        this(windowId, playerInventory, getTileEntity(playerInventory, buffer));
    }

    public CobblestoneAnvilContainer(int windowId, PlayerInventory playerInventory, CobblestoneAnvilTileEntity tileEntity) {
        super(ModContainers.COBBLESTONE_ANVIL.get(), windowId);
        this.tileEntity = tileEntity;
        this.player = playerInventory.player;
        this.playerInventory = new InvWrapper(playerInventory);

        // Слоты наковальни
        this.addSlot(new MetalInputSlot(tileEntity.getInventory(), CobblestoneAnvilTileEntity.METAL_SLOT, 27, 47));
        this.addSlot(new ToolInputSlot(tileEntity.getInventory(), CobblestoneAnvilTileEntity.TOOL_SLOT, 76, 47));
        this.addSlot(new OutputSlot(tileEntity.getInventory(), CobblestoneAnvilTileEntity.OUTPUT_SLOT, 134, 47));

        // Слоты инвентаря игрока
        layoutPlayerInventorySlots(8, 84);
    }

    private void layoutPlayerInventorySlots(int leftCol, int topRow) {
        // Основной инвентарь игрока
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int x = leftCol + col * 18;
                int y = topRow + row * 18;
                this.addSlot(new SlotItemHandler(playerInventory, col + row * 9 + 9, x, y));
            }
        }

        // Панель быстрого доступа
        for (int col = 0; col < 9; col++) {
            int x = leftCol + col * 18;
            int y = topRow + 58;
            this.addSlot(new SlotItemHandler(playerInventory, col, x, y));
        }
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return stillValid(IWorldPosCallable.create(tileEntity.getLevel(), tileEntity.getBlockPos()),
                player, ModBlocks.COBBLESTONE_ANVIL.get());
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();

            int containerSlots = CobblestoneAnvilTileEntity.TOTAL_SLOTS;
            if (index < containerSlots) {
                // Из слотов контейнера в инвентарь игрока
                if (!this.moveItemStackTo(stack, containerSlots, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Из инвентаря игрока в слоты контейнера
                if (!this.moveItemStackTo(stack, CobblestoneAnvilTileEntity.METAL_SLOT, CobblestoneAnvilTileEntity.TOOL_SLOT + 1, false)) {
                    return ItemStack.EMPTY;
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

    public BlockPos getBlockPos() {
        return tileEntity.getBlockPos();
    }

    private static CobblestoneAnvilTileEntity getTileEntity(PlayerInventory playerInventory, PacketBuffer buffer) {
        return (CobblestoneAnvilTileEntity) playerInventory.player.level.getBlockEntity(buffer.readBlockPos());
    }

    // Слот для металла (вход)
    private static class MetalInputSlot extends SlotItemHandler {
        public MetalInputSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            // В левый слот можно класть любые предметы
            return true;
        }
    }

    // Слот для инструмента (вход)
    private static class ToolInputSlot extends SlotItemHandler {
        public ToolInputSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            // Здесь можно добавить проверку на то, что это подходящий инструмент (молот)
            return stack.getItem() == ModItems.STONE_HAMMER.get()
                    || stack.getItem() == ModItems.BONE_HAMMER.get();
        }
    }

    // Слот для выхода (только для извлечения)
    private static class OutputSlot extends SlotItemHandler {
        public OutputSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            // В выходной слот нельзя класть предметы
            return false;
        }
    }
}