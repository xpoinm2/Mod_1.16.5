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

        builder.pop();
        SPEC = builder.build();
    }

    public static void register(IEventBus bus) {
        ModLoadingContext.get()
                .registerConfig(ModConfig.Type.COMMON, SPEC);
    }
}
