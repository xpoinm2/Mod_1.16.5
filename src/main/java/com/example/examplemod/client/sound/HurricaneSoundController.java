package com.example.examplemod.client.sound;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.client.HurricaneClientState;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class HurricaneSoundController {
    private static HurricaneLoopSound activeSound;

    private HurricaneSoundController() {
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) {
            if (activeSound != null) {
                activeSound.stopLoop();
            }
            activeSound = null;
            return;
        }

        boolean shouldPlay = HurricaneClientState.isActive() || HurricaneClientState.getIntensity() > 0.0F;
        if (!shouldPlay) {
            if (activeSound != null) {
                activeSound.stopLoop();
            }
            activeSound = null;
            return;
        }

        if (activeSound == null || activeSound.isStopped()) {
            activeSound = new HurricaneLoopSound(minecraft);
            minecraft.getSoundManager().play(activeSound);
        }
    }
}
