package com.example.examplemod.util;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public final class FluidTextUtil {
    private static final DecimalFormat LITER_FORMAT;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.ROOT);
        LITER_FORMAT = new DecimalFormat("#.##", symbols);
    }

    private FluidTextUtil() {
    }

    public static ITextComponent formatAmount(int milliBuckets) {
        if (milliBuckets >= 1000) {
            double liters = milliBuckets / 1000.0D;
            return new TranslationTextComponent("tooltip.examplemod.fluid.liters", LITER_FORMAT.format(liters));
        }
        return new TranslationTextComponent("tooltip.examplemod.fluid.milliliters", milliBuckets);
    }
}