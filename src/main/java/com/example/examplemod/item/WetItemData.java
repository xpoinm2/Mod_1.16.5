package com.example.examplemod.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;

import javax.annotation.Nullable;

public final class WetItemData {
    private static final String WET_TAG = "examplemod_wet";
    private static final String WET_UNTIL_TAG = "examplemod_wet_until";

    // Предмет остается мокрым 45 секунд после последнего намокания.
    public static final int DEFAULT_WET_DURATION_TICKS = 20 * 45;

    private WetItemData() {
    }

    public static void markWet(ItemStack stack, long gameTime) {
        markWet(stack, gameTime, DEFAULT_WET_DURATION_TICKS);
    }

    public static void markWet(ItemStack stack, long gameTime, int durationTicks) {
        if (stack.isEmpty()) {
            return;
        }

        CompoundNBT tag = stack.getOrCreateTag();
        tag.putBoolean(WET_TAG, true);
        tag.putLong(WET_UNTIL_TAG, gameTime + Math.max(1, durationTicks));
    }

    public static boolean isWet(ItemStack stack, long gameTime) {
        if (stack.isEmpty() || !stack.hasTag()) {
            return false;
        }

        CompoundNBT tag = stack.getTag();
        if (tag == null || !tag.getBoolean(WET_TAG)) {
            return false;
        }

        long wetUntil = tag.getLong(WET_UNTIL_TAG);
        if (wetUntil > gameTime) {
            return true;
        }

        clearWet(stack);
        return false;
    }

    public static void clearWet(ItemStack stack) {
        if (stack.isEmpty() || !stack.hasTag()) {
            return;
        }

        CompoundNBT tag = stack.getTag();
        if (tag == null) {
            return;
        }

        tag.remove(WET_TAG);
        tag.remove(WET_UNTIL_TAG);

        if (tag.isEmpty()) {
            stack.setTag(null);
        }
    }

    public static long getWetUntil(ItemStack stack) {
        if (stack.isEmpty() || !stack.hasTag()) {
            return 0L;
        }

        CompoundNBT tag = stack.getTag();
        if (tag == null || !tag.getBoolean(WET_TAG)) {
            return 0L;
        }

        return tag.getLong(WET_UNTIL_TAG);
    }

    public static void setWetUntil(ItemStack stack, long wetUntil) {
        if (stack.isEmpty()) {
            return;
        }

        CompoundNBT tag = stack.getOrCreateTag();
        tag.putBoolean(WET_TAG, true);
        tag.putLong(WET_UNTIL_TAG, wetUntil);
    }

    public static boolean canMergeIgnoringWetness(ItemStack first, ItemStack second) {
        if (first.isEmpty() || second.isEmpty()) {
            return false;
        }

        if (!first.sameItem(second)) {
            return false;
        }

        if (first.getDamageValue() != second.getDamageValue()) {
            return false;
        }

        if (first.getCount() >= first.getMaxStackSize()) {
            return false;
        }

        return sameTagExceptWetData(first.getTag(), second.getTag());
    }

    private static boolean sameTagExceptWetData(@Nullable CompoundNBT first, @Nullable CompoundNBT second) {
        CompoundNBT firstCopy = copyWithoutWetData(first);
        CompoundNBT secondCopy = copyWithoutWetData(second);
        return firstCopy.equals(secondCopy);
    }

    private static CompoundNBT copyWithoutWetData(@Nullable CompoundNBT tag) {
        if (tag == null) {
            return new CompoundNBT();
        }

        CompoundNBT copy = tag.copy();
        INBT wet = copy.get(WET_TAG);
        if (wet != null) {
            copy.remove(WET_TAG);
        }
        INBT wetUntil = copy.get(WET_UNTIL_TAG);
        if (wetUntil != null) {
            copy.remove(WET_UNTIL_TAG);
        }
        return copy;
    }

    public static int getRemainingWetTicks(ItemStack stack, long gameTime) {
        long wetUntil = getWetUntil(stack);
        if (wetUntil <= gameTime) {
            return 0;
        }
        return (int) Math.min(Integer.MAX_VALUE, wetUntil - gameTime);
    }
}
