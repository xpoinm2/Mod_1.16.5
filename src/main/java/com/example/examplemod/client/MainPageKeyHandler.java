package com.example.examplemod.client;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.client.screen.PlayerInterfaceScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
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
public class MainPageKeyHandler {
    public static final KeyBinding MAIN_PAGE_KEY = new KeyBinding(
            "key.examplemod.main_page",
            GLFW.GLFW_KEY_X,
            "key.categories.gameplay"
    );

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        ClientRegistry.registerKeyBinding(MAIN_PAGE_KEY);
    }
}

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
class MainPageKeyPressHandler {
    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        if (MainPageKeyHandler.MAIN_PAGE_KEY.consumeClick()) {
            Minecraft mc = Minecraft.getInstance();
            mc.setScreen(new PlayerInterfaceScreen(new InventoryScreen(mc.player)));
        }
    }
}