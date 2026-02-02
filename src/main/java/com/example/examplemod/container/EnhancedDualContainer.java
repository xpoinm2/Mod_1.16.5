package com.example.examplemod.container;

import com.example.examplemod.ModContainers;
import com.example.examplemod.item.BoneTongsCapabilityProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class EnhancedDualContainer extends Container {
    private final Container mainContainer; // Основной контейнер (печь/кострище)
    private final IItemHandler tongsHandler;
    private final List<Slot> originalMainSlots = new ArrayList<>();
    private final int tongsSlotCount;

    // Константы для позиционирования мини-GUI щипцов
    public static final int TONGS_GUI_WIDTH = 48;
    public static final int TONGS_GUI_HEIGHT = 30;
    public static final int TONGS_GUI_OFFSET_Y = 12;
    public static final int TONGS_GUI_X = 6; // Позиция слотов щипцов по X
    public static final int TONGS_GUI_Y = 18; // Позиция первого слота щипцов по Y
    public static final int MAIN_GUI_OFFSET_X = TONGS_GUI_WIDTH + 10; // Сдвиг основного GUI вправо
    public static final int MAIN_GUI_OFFSET_Y = 0;

    public EnhancedDualContainer(int windowId, PlayerInventory playerInventory, PacketBuffer data) {
        super(ModContainers.ENHANCED_DUAL_CONTAINER.get(), windowId);

        // Читаем данные
        boolean isFirepit = data.readBoolean();
        boolean inOffhand = data.readBoolean();
        BlockPos blockPos = data.readBlockPos();
        ItemStack tongsStack = inOffhand
                ? playerInventory.player.getOffhandItem()
                : playerInventory.player.getMainHandItem();

        // Получаем FirepitTileEntity из мира
        com.example.examplemod.tileentity.FirepitTileEntity firepitTile = null;
        com.example.examplemod.tileentity.PechugaTileEntity pechugaTile = null;
        net.minecraft.world.World world = playerInventory.player.level;
        if (world != null) {
            net.minecraft.tileentity.TileEntity tileEntity = world.getBlockEntity(blockPos);
            if (tileEntity instanceof com.example.examplemod.tileentity.FirepitTileEntity) {
                firepitTile = (com.example.examplemod.tileentity.FirepitTileEntity) tileEntity;
            } else if (tileEntity instanceof com.example.examplemod.tileentity.PechugaTileEntity) {
                pechugaTile = (com.example.examplemod.tileentity.PechugaTileEntity) tileEntity;
            }
        }

        // Получаем обработчик щипцов
        tongsHandler = tongsStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                .orElseGet(() -> new BoneTongsCapabilityProvider(tongsStack).getHandler());

        // Создаем основной контейнер
        if (isFirepit) {
            if (firepitTile == null) {
                throw new IllegalStateException("Firepit tile entity not found at " + blockPos);
            }
            mainContainer = new com.example.examplemod.container.FirepitContainer(windowId + 1, playerInventory, firepitTile, false);
        } else {
            if (pechugaTile == null) {
                throw new IllegalStateException("Pechuga tile entity not found at " + blockPos);
            }
            mainContainer = new com.example.examplemod.container.PechugaContainer(windowId + 1, playerInventory, pechugaTile, false);
        }

        // Добавляем слоты щипцов слева
        this.tongsSlotCount = tongsHandler.getSlots();
        for (int i = 0; i < tongsHandler.getSlots(); i++) {
            addSlot(new SlotItemHandler(tongsHandler, i, TONGS_GUI_X + i * BoneTongsContainer.SLOT_SPACING, TONGS_GUI_Y));
        }

        // Копируем слоты основного контейнера со сдвигом
        if (mainContainer != null) {
            for (Slot slot : mainContainer.slots) {
                originalMainSlots.add(slot);
                // Сдвигаем слоты основного контейнера вправо
                Slot newSlot = createShiftedSlot(slot, MAIN_GUI_OFFSET_X, MAIN_GUI_OFFSET_Y);
                addSlot(newSlot);
            }
        }
    }

    private Slot createShiftedSlot(Slot originalSlot, int offsetX, int offsetY) {
        // Создаем новый слот со сдвигом, копируя свойства оригинального
        if (originalSlot instanceof net.minecraftforge.items.SlotItemHandler) {
            // Для SlotItemHandler создаем новый с правильными координатами
            net.minecraftforge.items.SlotItemHandler originalHandlerSlot = (net.minecraftforge.items.SlotItemHandler) originalSlot;
            return new net.minecraftforge.items.SlotItemHandler(originalHandlerSlot.getItemHandler(),
                                                              originalHandlerSlot.getSlotIndex(),
                                                              originalSlot.x + offsetX,
                                                              originalSlot.y + offsetY);
        } else {
            // Для обычных слотов - получаем inventory через reflection или создаем новый слот
            // Пока что просто создадим базовый слот
            return new Slot(originalSlot.container, originalSlot.getSlotIndex(),
                           originalSlot.x + offsetX, originalSlot.y + offsetY);
        }
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return mainContainer.stillValid(player);
    }

    @Override
    public ItemStack clicked(int slotId, int dragType, ClickType clickType, PlayerEntity player) {
        if (slotId >= 0 && slotId < slots.size()) {
            int mainSlotsCount = originalMainSlots.size();

            if (slotId >= tongsSlotCount && slotId < tongsSlotCount + mainSlotsCount) {
                // Слот из основного контейнера - перенаправляем
                // Индекс в оригинальном контейнере соответствует индексу в нашем списке
                return mainContainer.clicked(slotId - tongsSlotCount, dragType, clickType, player);
            } else {
                // Слот из щипцов - обрабатываем локально
                return super.clicked(slotId, dragType, clickType, player);
            }
        } else {
            return super.clicked(slotId, dragType, clickType, player);
        }
    }

    @Nonnull
    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        // Расширенная логика quick-move между контейнерами
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            int mainSlotsCount = originalMainSlots.size();
            int mainSlotsStart = tongsSlotCount;
            int mainSlotsEnd = tongsSlotCount + mainSlotsCount;

            if (index >= mainSlotsStart && index < mainSlotsEnd) {
                // Из основного контейнера в щипцы
                if (!this.moveItemStackTo(itemstack1, 0, tongsSlotCount, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 0 && index < tongsSlotCount) {
                // Из щипцов в основной контейнер
                if (!this.moveItemStackTo(itemstack1, mainSlotsStart, mainSlotsEnd, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
        }

        return itemstack;
    }

    @Override
    public void removed(PlayerEntity player) {
        super.removed(player);
        if (mainContainer != null) {
            mainContainer.removed(player);
        }
    }

    // Геттеры для доступа к контейнерам
    public Container getMainContainer() {
        return mainContainer;
    }

    public IItemHandler getTongsHandler() {
        return tongsHandler;
    }
}
