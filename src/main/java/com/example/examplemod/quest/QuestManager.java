package com.example.examplemod.quest;

public class QuestManager {
    private static boolean planksCompleted = false;
    private static boolean slabsCompleted = false;
    private static boolean hewnStonesCompleted = false;

    public static boolean isPlanksCompleted() {
        return planksCompleted;
    }

    public static void setPlanksCompleted(boolean value) {
        planksCompleted = value;
    }


    public static boolean isSlabsCompleted() {
        return slabsCompleted;
    }

    public static void setSlabsCompleted(boolean value) {
        slabsCompleted = value;
    }

    public static boolean isHewnStonesCompleted() {
        return hewnStonesCompleted;
    }

    public static void setHewnStonesCompleted(boolean value) {
        hewnStonesCompleted = value;
    }

    public static void resetAll() {
        planksCompleted = false;
        slabsCompleted = false;
        hewnStonesCompleted = false;
    }
}