package com.example.examplemod.client.render;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.client.HurricaneClientState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.world.DimensionRenderInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ISkyRenderHandler;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class HurricaneSkyEffects {
    private static final ISkyRenderHandler HURRICANE_SKY = new HurricaneSkyRenderer();

    private static DimensionRenderInfo activeEffects;
    private static ISkyRenderHandler originalSky;
    private static boolean customSkyActive;

    private HurricaneSkyEffects() {
    }

    public static boolean isCustomSkyActive() {
        return customSkyActive;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        HurricaneClientState.tickFade();
        ClientWorld world = minecraft.level;
        if (world == null) {
            clearHandlers();
            return;
        }

        DimensionRenderInfo effects = world.effects();
        if (HurricaneClientState.shouldRenderEffects()) {
            applyHandlers(effects);
        } else {
            restoreHandlers(effects);
        }
    }

    private static void applyHandlers(DimensionRenderInfo effects) {
        if (customSkyActive && effects == activeEffects) {
            return;
        }

        storeOriginalHandlers(effects);
        effects.setSkyRenderHandler(HURRICANE_SKY);
        activeEffects = effects;
        customSkyActive = true;
    }

    private static void restoreHandlers(DimensionRenderInfo effects) {
        if (!customSkyActive) {
            return;
        }

        if (effects == activeEffects) {
            effects.setSkyRenderHandler(originalSky);
        }
        clearHandlers();
    }

    private static void storeOriginalHandlers(DimensionRenderInfo effects) {
        if (effects == activeEffects && originalSky != null) {
            return;
        }

        originalSky = effects.getSkyRenderHandler();
    }

    private static void clearHandlers() {
        activeEffects = null;
        originalSky = null;
        customSkyActive = false;
    }
}
