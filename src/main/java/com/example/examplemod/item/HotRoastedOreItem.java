// === FILE src/main/java/com/example/examplemod/item/HotRoastedOreItem.java
package com.example.examplemod.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.registries.ForgeRegistries;

public class HotRoastedOreItem extends Item {
    private static final String TIMER_TAG = "HotTimer";
    private static final int MAX_TIMER = 180; // 180 секунд = 3 минуты

    private final Item resultItem; // Предмет, в который преобразуется горячая руда

    public HotRoastedOreItem(Item resultItem, Properties properties) {
        super(properties);
        this.resultItem = resultItem;
    }

    // Получить оставшееся время таймера в секундах
    public static int getRemainingTime(ItemStack stack) {
        return getRemainingTime(stack, 1.0f); // По умолчанию скорость 1.0 (нормальная)
    }

    // Получить оставшееся время таймера в секундах с множителем скорости
    public static int getRemainingTime(ItemStack stack, float speedMultiplier) {
        CompoundNBT nbt = stack.getOrCreateTag();
        long creationTime = nbt.getLong(TIMER_TAG);
        if (creationTime == 0) {
            // Если время создания не установлено, устанавливаем текущее время
            setCreationTime(stack, System.currentTimeMillis() / 1000);
            return MAX_TIMER;
        }

        long currentTime = System.currentTimeMillis() / 1000;
        int elapsedSeconds = (int) ((currentTime - creationTime) * speedMultiplier);
        return Math.max(0, MAX_TIMER - elapsedSeconds);
    }

    // Установить время создания предмета
    public static void setCreationTime(ItemStack stack, long timestamp) {
        CompoundNBT nbt = stack.getOrCreateTag();
        nbt.putLong(TIMER_TAG, timestamp);
        stack.setTag(nbt);
    }

    // Проверить, истек ли таймер
    public static boolean isTimerExpired(ItemStack stack) {
        return getRemainingTime(stack, 1.0f) <= 0;
    }

    // Проверить, истек ли таймер с множителем скорости
    public static boolean isTimerExpired(ItemStack stack, float speedMultiplier) {
        return getRemainingTime(stack, speedMultiplier) <= 0;
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

    // Отображение шкалы таймера (как durability bar)
    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        int remainingTime = getRemainingTime(stack);
        return 1.0 - ((double) remainingTime / MAX_TIMER);
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
}