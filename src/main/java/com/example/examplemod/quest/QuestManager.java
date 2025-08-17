package com.example.examplemod.quest;

public class QuestManager {
    private static boolean planksCompleted = false;
    private static boolean slabsCompleted = false;
    private static boolean hewnStonesCompleted = false;
    private static boolean bigBonesCompleted = false;
    private static boolean sharpenedBoneCompleted = false;
    private static boolean stoneToolsCompleted = false;
    private static boolean boneToolsCompleted = false;

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

    public static boolean isBigBonesCompleted() {
        return bigBonesCompleted;
    }

    public static void setBigBonesCompleted(boolean value) {
        bigBonesCompleted = value;
    }

    public static boolean isSharpenedBoneCompleted() {
        return sharpenedBoneCompleted;
    }

    public static void setSharpenedBoneCompleted(boolean value) {
        sharpenedBoneCompleted = value;
    }

    public static boolean isStoneToolsCompleted() {
        return stoneToolsCompleted;
    }

    public static void setStoneToolsCompleted(boolean value) {
        stoneToolsCompleted = value;
    }

    public static boolean isBoneToolsCompleted() {
        return boneToolsCompleted;
    }

    public static void setBoneToolsCompleted(boolean value) {
        boneToolsCompleted = value;
    }

    public static void resetAll() {
        planksCompleted = false;
        slabsCompleted = false;
        hewnStonesCompleted = false;
        bigBonesCompleted = false;
        sharpenedBoneCompleted = false;
        stoneToolsCompleted = false;
        boneToolsCompleted = false;
    }
}