package com.example.examplemod;

import com.example.examplemod.util.RegistryHelper;
import net.minecraftforge.fml.RegistryObject;

public class ModSounds {

    public static final RegistryObject<net.minecraft.util.SoundEvent> DANILKA_BLOCK_TAP =
            RegistryHelper.registerSound("block.danilka.tap", ModRegistries.SOUND_EVENTS);
}