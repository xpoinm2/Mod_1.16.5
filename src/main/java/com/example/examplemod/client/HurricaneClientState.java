package com.example.examplemod.client;

import com.example.examplemod.ExampleMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class HurricaneClientState {
    private static final float FADE_TICKS = 100.0F;
    private static final float FADE_STEP = 1.0F / FADE_TICKS;

    private static volatile boolean active;
    private static volatile float intensity;

    private HurricaneClientState() {
    }

    public static boolean isActive() {
        return active;
    }

    public static boolean shouldRenderEffects() {
        return active || intensity > 0.0F;
    }

    public static float getIntensity() {
        return intensity;
    }

    public static void setActive(boolean active) {
        HurricaneClientState.active = active;
    }

    public static void tickFade() {
        if (active) {
            intensity = Math.min(1.0F, intensity + FADE_STEP);
            return;
        }
        intensity = Math.max(0.0F, intensity - FADE_STEP);
    }

    @SubscribeEvent
    public static void onClientLogout(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        active = false;
        intensity = 0.0F;
    }
}
