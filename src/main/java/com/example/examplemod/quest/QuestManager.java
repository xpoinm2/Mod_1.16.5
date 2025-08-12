package com.example.examplemod.quest;

public class QuestManager {
    private static boolean planksCompleted = false;
    private static boolean slabsCompleted = false;

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

    public static void resetAll() {
        planksCompleted = false;
        slabsCompleted = false;
    }
}