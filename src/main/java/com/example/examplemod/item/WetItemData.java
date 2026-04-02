package com.example.examplemod.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

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
}
