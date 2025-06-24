package com.example.examplemod.client;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.client.screen.ActivityScreen;
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
public class ActivityKeyHandler {
    public static final KeyBinding ACTIVITY_KEY = new KeyBinding(
            "key.examplemod.activity",
            GLFW.GLFW_KEY_Z,
            "key.categories.gameplay"
    );

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        ClientRegistry.registerKeyBinding(ACTIVITY_KEY);
    }
}

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
class ActivityKeyPressHandler {
    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        if (ActivityKeyHandler.ACTIVITY_KEY.consumeClick()) {
            Minecraft.getInstance().setScreen(new ActivityScreen());
        }
    }
}