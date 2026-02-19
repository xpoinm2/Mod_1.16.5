package com.example.examplemod.util;

public final class TimeOfDayUtil {
    public static final long TICKS_PER_DAY = 24000L;
    public static final long DAWN_FIRST_LIGHT_TICK = 22200L;

    private TimeOfDayUtil() {
    }

    public static long normalizeDayTime(long dayTime) {
        long normalized = dayTime % TICKS_PER_DAY;
        return normalized < 0 ? normalized + TICKS_PER_DAY : normalized;
    }

    public static int getHour24(long dayTime) {
        long normalized = normalizeDayTime(dayTime);
        return (int) ((normalized / 1000L + 6L) % 24L);
    }

    public static int getMinute(long dayTime) {
        long normalized = normalizeDayTime(dayTime);
        return (int) ((normalized % 1000L) * 60L / 1000L);
    }

    /**
     * Первое визуальное осветление неба после полной ночи.
     * Для vanilla/Forge 1.16.5 это тик 22200 (примерно 04:12 по 24-часовым часам).
     */
    public static boolean isFirstLightTick(long dayTime) {
        return normalizeDayTime(dayTime) == DAWN_FIRST_LIGHT_TICK;
    }
}
