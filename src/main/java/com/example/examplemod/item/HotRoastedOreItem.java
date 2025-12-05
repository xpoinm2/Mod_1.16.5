// === FILE src/main/java/com/example/examplemod/item/HotRoastedOreItem.java
package com.example.examplemod.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
public class HotRoastedOreItem extends Item {
    private static final String TIMER_TAG = "HotTimer";
    private static final int MAX_TIMER = 180; // 180 секунд = 3 минуты
    private static final String WATER_BONUS_TAG = "HotWaterBonus";
    private static final String WATER_LAST_TAG = "HotWaterLast";

    private final Item resultItem; // Предмет, в который преобразуется горячая руда

    public HotRoastedOreItem(Item resultItem, Properties properties) {
        super(properties);
        this.resultItem = resultItem;
    }

    // Получить оставшееся время таймера в секундах
    public static int getRemainingTime(ItemStack stack) {
        CompoundNBT nbt = stack.getOrCreateTag();
        long creationTime = nbt.getLong(TIMER_TAG);
        if (creationTime == 0) {
            // Если время создания не установлено, устанавливаем текущее время и сбрасываем бонусы
            setCreationTime(stack, System.currentTimeMillis() / 1000);
            return MAX_TIMER;
        }

        long currentTime = System.currentTimeMillis() / 1000;
        int elapsedSeconds = (int) (currentTime - creationTime);
        int waterBonus = Math.max(0, nbt.getInt(WATER_BONUS_TAG));
        int remaining = MAX_TIMER - elapsedSeconds - waterBonus;
        return Math.max(0, remaining);
    }

    // Установить время создания предмета
    public static void setCreationTime(ItemStack stack, long timestamp) {
        CompoundNBT nbt = stack.getOrCreateTag();
        nbt.putLong(TIMER_TAG, timestamp);
        nbt.putInt(WATER_BONUS_TAG, 0);
        nbt.putLong(WATER_LAST_TAG, 0);
        stack.setTag(nbt);
    }

    // Проверить, истек ли таймер
    public static boolean isTimerExpired(ItemStack stack) {
        return isTimerExpired(stack, 1.0f, false);
    }

    // Проверить, истек ли таймер с множителем скорости и информацией об ускоренном охлаждении
    public static boolean isTimerExpired(ItemStack stack, float speedMultiplier, boolean inWater) {
        if (inWater && speedMultiplier > 1.0f) {
            applyWaterAcceleration(stack, speedMultiplier);
        } else {
            resetWaterTracking(stack);
        }
        return getRemainingTime(stack) <= 0;
    }

    private static void applyWaterAcceleration(ItemStack stack, float speedMultiplier) {
        CompoundNBT nbt = stack.getOrCreateTag();
        long now = System.currentTimeMillis() / 1000;
        long lastWaterTime = nbt.getLong(WATER_LAST_TAG);
        if (lastWaterTime == 0) {
            nbt.putLong(WATER_LAST_TAG, now);
            return;
        }

        long deltaSeconds = now - lastWaterTime;
        if (deltaSeconds > 0) {
            int extraSeconds = (int) (deltaSeconds * (speedMultiplier - 1.0f));
            if (extraSeconds > 0) {
                int bonus = Math.min(MAX_TIMER, nbt.getInt(WATER_BONUS_TAG) + extraSeconds);
                nbt.putInt(WATER_BONUS_TAG, bonus);
            }
        }

        nbt.putLong(WATER_LAST_TAG, now);
        stack.setTag(nbt);
    }

    private static void resetWaterTracking(ItemStack stack) {
        CompoundNBT nbt = stack.getOrCreateTag();
        nbt.putLong(WATER_LAST_TAG, 0);
        stack.setTag(nbt);
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