package com.example.examplemod;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ExampleMod.MODID);

    public static final RegistryObject<SoundEvent> DANILKA_BLOCK_TAP = SOUND_EVENTS.register(
            "block.danilka.tap",
            () -> new SoundEvent(new ResourceLocation(ExampleMod.MODID, "block.danilka.tap")));

    public static void register(IEventBus bus) {
        SOUND_EVENTS.register(bus);
    }
}