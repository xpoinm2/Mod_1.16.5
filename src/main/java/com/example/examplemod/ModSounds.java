package com.example.examplemod;

import com.example.examplemod.util.RegistryHelper;
import net.minecraftforge.fml.RegistryObject;

public class ModSounds {

    public static final RegistryObject<net.minecraft.util.SoundEvent> DANILKA_BLOCK_TAP =
            ModRegistries.SOUND_EVENTS.register("block.danilka.tap",
                    () -> new net.minecraft.util.SoundEvent(new net.minecraft.util.ResourceLocation(ExampleMod.MODID, "block.danilka.tap")));
}