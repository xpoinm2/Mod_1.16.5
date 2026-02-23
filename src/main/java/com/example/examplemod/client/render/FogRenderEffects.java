package com.example.examplemod.client.render;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.client.FogClientState;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class FogRenderEffects {
    private static final float FOG_DISTANCE_BLOCKS = 10.0F;

    private FogRenderEffects() {
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (Minecraft.getInstance().isPaused()) {
            return;
        }
        FogClientState.tickFade();
    }

    @SubscribeEvent
    public static void onFogRender(EntityViewRenderEvent.RenderFogEvent event) {
        if (!FogClientState.shouldRenderFog()) {
            return;
        }

        float intensity = FogClientState.getIntensity();
        float baseNear = event.getNearPlaneDistance();
        float baseFar = event.getFarPlaneDistance();

        float fogNear = lerp(baseNear, 1.0F, intensity);
        float fogFar = lerp(baseFar, FOG_DISTANCE_BLOCKS, intensity);
        if (fogFar <= fogNear) {
            fogNear = Math.max(0.0F, fogFar - 0.1F);
        }

        event.setNearPlaneDistance(fogNear);
        event.setFarPlaneDistance(fogFar);
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onFogColors(EntityViewRenderEvent.FogColors event) {
        if (!FogClientState.shouldRenderFog()) {
            return;
        }

        float intensity = FogClientState.getIntensity();
        event.setRed(lerp(event.getRed(), 0.72F, intensity));
        event.setGreen(lerp(event.getGreen(), 0.74F, intensity));
        event.setBlue(lerp(event.getBlue(), 0.76F, intensity));
    }

    private static float lerp(float from, float to, float alpha) {
        return from + (to - from) * alpha;
    }
}
