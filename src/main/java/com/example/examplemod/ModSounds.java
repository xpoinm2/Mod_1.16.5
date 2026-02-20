package com.example.examplemod;

import net.minecraftforge.fml.RegistryObject;

public class ModSounds {

    public static final RegistryObject<net.minecraft.util.SoundEvent> DANILKA_BLOCK_TAP =
            ModRegistries.SOUND_EVENTS.register("block.danilka.tap",
                    () -> new net.minecraft.util.SoundEvent(new net.minecraft.util.ResourceLocation(ExampleMod.MODID, "block.danilka.tap")));

    public static final RegistryObject<net.minecraft.util.SoundEvent> HURRICANE_LOOP =
            ModRegistries.SOUND_EVENTS.register("weather.hurricane_loop",
                    () -> new net.minecraft.util.SoundEvent(new net.minecraft.util.ResourceLocation(ExampleMod.MODID, "weather.hurricane_loop")));

    public static void register() {
        // no-op; вызов нужен, чтобы гарантировать загрузку класса и регистрацию SoundEvent'ов.
    }
}
