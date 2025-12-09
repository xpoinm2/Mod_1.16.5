package com.example.examplemod.client;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.client.screen.ProgressScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ProgressKeyHandler {
    public static final KeyBinding PROGRESS_KEY = new KeyBinding(
            "key.examplemod.progress",
            GLFW.GLFW_KEY_C,
            ExampleMod.KEY_CATEGORY
    );

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        ClientRegistry.registerKeyBinding(PROGRESS_KEY);
    }
}

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
class ProgressKeyPressHandler {
    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        if (ProgressKeyHandler.PROGRESS_KEY.consumeClick()) {
            Minecraft.getInstance().setScreen(new ProgressScreen());
        }
    }
}