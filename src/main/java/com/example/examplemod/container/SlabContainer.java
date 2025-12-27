package com.example.examplemod.container;

import com.example.examplemod.ModBlocks;
import com.example.examplemod.ModContainers;
import com.example.examplemod.tileentity.SlabTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.block.Block;
import net.minecraft.item.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlabContainer extends Container {
    public static final int GRID_SIZE = 3;
    public static final int GRID_START_X = 62;
    public static final int GRID_START_Y = 17;

    private final SlabTileEntity tileEntity;
    private final IWorldPosCallable canInteract;
    private final IItemHandler inventory;

    public SlabContainer(int windowId, PlayerInventory playerInventory, PacketBuffer buffer) {
        this(windowId, playerInventory, getTileEntity(playerInventory, buffer));
    }

    public SlabContainer(int windowId, PlayerInventory playerInventory, SlabTileEntity tileEntity) {
        super(ModContainers.SLAB.get(), windowId);
        this.tileEntity = tileEntity;
        this.inventory = tileEntity.getInventory();
        this.canInteract = IWorldPosCallable.create(tileEntity.getLevel(), tileEntity.getBlockPos());

        // Добавляем 9 слотов инвентаря полублока (3x3 сетка)
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                int index = col + row * GRID_SIZE;
                this.addSlot(new SlabSlot(inventory, index,
                        GRID_START_X + col * 18,
                        GRID_START_Y + row * 18));
            }
        }

        // Добавляем слоты инвентаря игрока
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // Добавляем слоты хотбара
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    private static SlabTileEntity getTileEntity(PlayerInventory playerInventory, PacketBuffer buffer) {
        BlockPos pos = buffer.readBlockPos();
        if (playerInventory.player.level != null && playerInventory.player.level.getBlockEntity(pos) instanceof SlabTileEntity) {
            return (SlabTileEntity) playerInventory.player.level.getBlockEntity(pos);
        }
        throw new IllegalStateException("Slab tile entity not found at " + pos);
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return canInteract.evaluate((world, pos) -> {
            Block block = world.getBlockState(pos).getBlock();
            // Проверяем, что блок является полублоком с GUI
            return block == ModBlocks.BRUSHWOOD_SLAB.get() 
                || block == ModBlocks.BURNED_BRUSHWOOD_SLAB.get()
                || block == ModBlocks.OAK_SLAB.get()
                || block == ModBlocks.BIRCH_SLAB.get()
                || block == ModBlocks.SPRUCE_SLAB.get()
                || block == ModBlocks.JUNGLE_SLAB.get()
                || block == ModBlocks.ACACIA_SLAB.get()
                || block == ModBlocks.DARK_OAK_SLAB.get()
                || block == ModBlocks.CRIMSON_SLAB.get()
                || block == ModBlocks.WARPED_SLAB.get()
                || block == ModBlocks.STONE_SLAB.get();
        }, true);
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();

            int containerSlots = SlabTileEntity.INV_SLOTS;
            int playerInvStart = containerSlots;
            int playerInvEnd = playerInvStart + 27;
            int hotbarStart = playerInvEnd;
            int hotbarEnd = hotbarStart + 9;

            if (index < containerSlots) {
                // Из контейнера в инвентарь игрока
                if (!this.moveItemStackTo(stack, playerInvStart, hotbarEnd, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Из инвентаря игрока в контейнер
                // Проверяем, что предмет можно положить в контейнер (только обычные предметы)
                if (canPlaceItemInSlab(stack) && !this.moveItemStackTo(stack, 0, containerSlots, false)) {
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

    /**
     * Проверяет, можно ли положить предмет в полублок.
     * Разрешаются только обычные предметы, блоки и инструменты запрещены.
     */
    private boolean canPlaceItemInSlab(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        
        Item item = stack.getItem();
        
        // Запрещаем блоки
        if (item instanceof BlockItem) {
            return false;
        }
        
        // Запрещаем инструменты
        if (item instanceof PickaxeItem
                || item instanceof AxeItem
                || item instanceof ShovelItem
                || item instanceof HoeItem
                || item instanceof SwordItem) {
            return false;
        }
        
        // Разрешаем все остальные предметы
        return true;
    }

    private final class SlabSlot extends SlotItemHandler {
        public SlabSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            // Разрешаем только обычные предметы, блоки и инструменты запрещены
            return canPlaceItemInSlab(stack);
        }
    }
}
