// === FILE src/main/java/com/example/examplemod/ModEffects.java
package com.example.examplemod;

import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEffects {
    public static final DeferredRegister<Effect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.POTIONS, ExampleMod.MODID);

    // Простые эффекты без дополнительной логики - используем встроенные эффекты
    // Мы будем применять MOVEMENT_SLOWDOWN и DIG_SLOWDOWN эффекты напрямую

    public static void register(IEventBus eventBus) {
        EFFECTS.register(eventBus);
    }
}