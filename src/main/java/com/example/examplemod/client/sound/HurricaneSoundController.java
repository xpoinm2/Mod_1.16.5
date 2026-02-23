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
    private static HurricaneLoopSound loopSound;

    private HurricaneSoundController() {
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) {
            stopLoop();
            return;
        }

        boolean shouldPlay = HurricaneClientState.isActive();
        if (!shouldPlay) {
            stopLoop();
            return;
        }

        ensureLoopIsPlaying(minecraft);
    }

    private static void ensureLoopIsPlaying(Minecraft minecraft) {
        if (loopSound != null && !loopSound.isStopped()) {
            return;
        }

        loopSound = new HurricaneLoopSound(minecraft);
        if (!loopSound.canStart()) {
            loopSound = null;
            return;
        }
        minecraft.getSoundManager().play(loopSound);
    }

    private static void stopLoop() {
        if (loopSound == null) {
            return;
        }

        loopSound.stopLoop();
        loopSound = null;
    }
}
