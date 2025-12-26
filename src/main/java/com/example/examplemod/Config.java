// === FILE src\main\java\com\example\examplemod\Config.java

package com.example.examplemod;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class Config {
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.IntValue FATIGUE;
    public static final ForgeConfigSpec.IntValue THIRST;
    public static final ForgeConfigSpec.IntValue DISEASE;

    // Mechanics scheduler / profiling
    public static final ForgeConfigSpec.BooleanValue MECHANICS_PROFILING;
    public static final ForgeConfigSpec.IntValue MECHANICS_SLOW_CALL_THRESHOLD_MS;
    public static final ForgeConfigSpec.IntValue MECHANICS_PROFILE_LOG_EVERY_TICKS;

    // Ленивая инициализация: включение/выключение групп механик
    public static final ForgeConfigSpec.BooleanValue ENABLE_SURVIVAL_MECHANICS;
    public static final ForgeConfigSpec.BooleanValue ENABLE_TEMPERATURE_MECHANICS;
    public static final ForgeConfigSpec.BooleanValue ENABLE_DISEASE_MECHANICS;
    public static final ForgeConfigSpec.BooleanValue ENABLE_DEBUG_COMMANDS;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment("Настройки ExampleMod")
                .push("general");

        FATIGUE = builder
                .comment("Усталость (0–100)")
                .defineInRange("fatigue", 40, 0, 100);

        THIRST = builder
                .comment("Жажда (0–100)")
                .defineInRange("thirst", 40, 0, 100);

        DISEASE = builder
                .comment("Болезнь (0–100)")
                .defineInRange("disease", 0, 0, 100);

        builder.comment("Менеджер механик (профилирование)")
                .push("mechanics");

        MECHANICS_PROFILING = builder
                .comment("Логировать время выполнения механик (может чуть замедлять сервер).")
                .define("profiling", false);

        MECHANICS_SLOW_CALL_THRESHOLD_MS = builder
                .comment("Порог (мс) для warn-логов по отдельным вызовам механик. 0 = выключено.")
                .defineInRange("slow_call_threshold_ms", 10, 0, 10_000);

        MECHANICS_PROFILE_LOG_EVERY_TICKS = builder
                .comment("Как часто печатать perf summary (в тиках). Рекомендация: 1200 = раз в минуту.")
                .defineInRange("profile_log_every_ticks", 1200, 20, 20 * 60 * 60);

        builder.pop();

        builder.comment("Ленивая инициализация: отключайте ненужные системы для ускорения загрузки и экономии памяти")
                .push("mechanics_toggle");

        ENABLE_SURVIVAL_MECHANICS = builder
                .comment("Механики выживания: жажда, усталость, отдых. Отключение сэкономит память.")
                .define("enable_survival_mechanics", true);

        ENABLE_TEMPERATURE_MECHANICS = builder
                .comment("Механики температуры: холод, переохлаждение. Отключение отключит кэш температур.")
                .define("enable_temperature_mechanics", true);

        ENABLE_DISEASE_MECHANICS = builder
                .comment("Механики болезней: вирусы, яды. Отключение отключит проверки при еде/ломании блоков.")
                .define("enable_disease_mechanics", true);

        ENABLE_DEBUG_COMMANDS = builder
                .comment("Отладочные команды: биом телепорт, пирамида, и т.д. Рекомендуется отключить на публичных серверах.")
                .define("enable_debug_commands", true);

        builder.pop();

        builder.pop();
        SPEC = builder.build();
    }

    public static void register(IEventBus bus) {
        ModLoadingContext.get()
                .registerConfig(ModConfig.Type.COMMON, SPEC);
    }
}
