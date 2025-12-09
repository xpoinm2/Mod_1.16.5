package com.example.examplemod.client;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.item.BoneTongsItem;
import com.example.examplemod.network.ModNetworkHandler;
import com.example.examplemod.network.ToggleTongsModePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TongsModeKeyHandler {
    public static final KeyBinding TONGS_MODE_KEY = new KeyBinding(
            "key.examplemod.mode_switch",
            GLFW.GLFW_KEY_V,
            ExampleMod.KEY_CATEGORY
    );

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        ClientRegistry.registerKeyBinding(TONGS_MODE_KEY);
    }
}

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
class TongsModeKeyPressHandler {
    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        if (!TongsModeKeyHandler.TONGS_MODE_KEY.consumeClick()) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        ItemStack held = mc.player.getMainHandItem();
        if (!(held.getItem() instanceof BoneTongsItem)) return;

        ModNetworkHandler.CHANNEL.sendToServer(new ToggleTongsModePacket());
    }
}
