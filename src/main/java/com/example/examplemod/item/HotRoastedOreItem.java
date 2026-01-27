// === FILE src/main/java/com/example/examplemod/item/HotRoastedOreItem.java
package com.example.examplemod.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class HotRoastedOreItem extends Item {
    // Константы для состояний (как у губчатого металла)
    public static final int STATE_HOT = 1;    // Горячий
    public static final int STATE_COLD = 2;   // Холодный

    // Константы для NBT тегов
    private static final String STATE_TAG = "HotOreState";
    private static final String CREATION_TIME_TAG = "CreationTime";
    private static final String TRANSITION_TIME_TAG = "TransitionTime";
    private static final String FRESH_FROM_HOT_FURNACE_TAG = "FreshFromHotFurnace";

    // Время перехода из горячего в холодное состояние (в тиках)
    private static final int TRANSITION_TIME = 1200; // 1200 тиков = 60 секунд

    private final Item resultItem; // Предмет, в который преобразуется горячая руда

    public HotRoastedOreItem(Item resultItem, Properties properties) {
        super(properties);
        this.resultItem = resultItem;
    }

    /**
     * Получить текущее состояние предмета
     */
    public static int getState(ItemStack stack) {
        CompoundNBT nbt = stack.getOrCreateTag();
        int state = nbt.getInt(STATE_TAG);

        // Инициализируем состояние, если его нет
        if (state == 0) {
            setState(stack, STATE_HOT);
            return STATE_HOT;
        }

        return state;
    }

    /**
     * Установить состояние предмета
     */
    public static void setState(ItemStack stack, int state) {
        CompoundNBT nbt = stack.getOrCreateTag();
        nbt.putInt(STATE_TAG, state);
        nbt.putLong(CREATION_TIME_TAG, System.currentTimeMillis() / 1000);
        nbt.putLong(TRANSITION_TIME_TAG, System.currentTimeMillis() / 1000);
        stack.setTag(nbt);
    }

    /**
     * Проверить, нужно ли перейти к следующему состоянию
     */
    private static void checkStateTransition(ItemStack stack) {
        int currentState = getState(stack);
        if (currentState == STATE_COLD) {
            return; // Холодное состояние финальное
        }

        CompoundNBT nbt = stack.getOrCreateTag();
        long transitionTime = nbt.getLong(TRANSITION_TIME_TAG);
        long currentTime = System.currentTimeMillis() / 1000;

        if (currentTime - transitionTime >= TRANSITION_TIME) {
            setState(stack, STATE_COLD);
        }
    }

    /**
     * Проверить контакт с водой (для совместимости, хотя горячие руды не должны реагировать на воду)
     */
    private static void checkWaterContact(ItemStack stack, World world, Entity entity) {
        // Для горячих руд контакт с водой не влияет на состояние
        // Они просто постепенно охлаждаются со временем
    }

    // Преобразовать предмет в обычную обожженную руду
    public static ItemStack getResultItemStack(ItemStack stack) {
        if (!(stack.getItem() instanceof HotRoastedOreItem)) {
            return stack;
        }

        HotRoastedOreItem hotOreItem = (HotRoastedOreItem) stack.getItem();
        ItemStack resultStack = new ItemStack(hotOreItem.resultItem, stack.getCount());
        return resultStack;
    }

    // Не показываем полоску durability (как у губчатых металлов)
    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return false;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return 0.0;
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        // Красный цвет для горячей руды
        return 0xFFFF0000;
    }

    // Максимальный размер стака - 1 (чтобы таймеры не смешивались)
    @Override
    public int getItemStackLimit(ItemStack stack) {
        return 1;
    }

    /**
     * Обновление состояния в инвентаре игрока.
     * Если предмет охладелся — заменяем на обычную обожжённую руду.
     */
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (world.isClientSide) return;
        if (!(entity instanceof PlayerEntity)) return;

        PlayerEntity player = (PlayerEntity) entity;

        // Проверяем, был ли предмет только что взят из горячей печи/кострища
        CompoundNBT nbt = stack.getOrCreateTag();
        if (nbt.contains(FRESH_FROM_HOT_FURNACE_TAG)) {
            // Удаляем флаг через некоторое время (например, через 5 секунд)
            long freshTime = nbt.getLong(FRESH_FROM_HOT_FURNACE_TAG);
            long currentTime = System.currentTimeMillis() / 1000;
            if (currentTime - freshTime > 5) {
                nbt.remove(FRESH_FROM_HOT_FURNACE_TAG);
                stack.setTag(nbt);
            }
            return; // Не начинаем охлаждение
        }

        // Проверяем переход состояний
        checkStateTransition(stack);

        // Если предмет стал холодным, заменяем на обычную руду
        if (getState(stack) == STATE_COLD) {
            player.inventory.setItem(slot, getResultItemStack(stack));
        }
    }

    /**
     * Обновление состояния у ItemEntity (выброшенные предметы).
     */
    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (entity.level.isClientSide) return false;

        // Проверяем переход состояний
        checkStateTransition(stack);

        // Проверяем контакт с водой (для совместимости)
        checkWaterContact(stack, entity.level, entity);

        // Если предмет стал холодным, заменяем на обычную руду
        if (getState(stack) == STATE_COLD) {
            entity.setItem(getResultItemStack(stack));
        }

        return false;
    }

    // Отображаем состояние в тултипе
    @Override
    public void appendHoverText(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);

        int state = getState(stack);
        String stateText = "";
        TextFormatting color = TextFormatting.GRAY;

        switch (state) {
            case STATE_HOT:
                stateText = "Горячий";
                color = TextFormatting.RED;
                break;
            case STATE_COLD:
                stateText = "Холодный";
                color = TextFormatting.BLUE;
                break;
        }

        tooltip.add(new TranslationTextComponent("tooltip.examplemod.hot_ore.temperature", stateText)
                .withStyle(color));
    }
}
