package com.example.examplemod.quest;

public class QuestManager {
    private static boolean planksCompleted = false;

    public static boolean isPlanksCompleted() {
        return planksCompleted;
    }

    public static void setPlanksCompleted(boolean value) {
        planksCompleted = value;
    }

    public static void resetAll() {
        planksCompleted = false;
    }
}