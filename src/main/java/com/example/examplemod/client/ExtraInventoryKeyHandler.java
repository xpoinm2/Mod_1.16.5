package com.example.examplemod.client;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.ModItems;
import com.example.examplemod.network.ModNetworkHandler;
import com.example.examplemod.network.OpenBoneTongsItemPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ExtraInventoryKeyHandler {
    public static final KeyBinding EXTRA_INVENTORY_KEY = new KeyBinding(
            "key.examplemod.extra_inventory",
            GLFW.GLFW_KEY_B,
            ExampleMod.KEY_CATEGORY
    );

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        ClientRegistry.registerKeyBinding(EXTRA_INVENTORY_KEY);
    }
}

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
class ExtraInventoryKeyPressHandler {
    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        if (!ExtraInventoryKeyHandler.EXTRA_INVENTORY_KEY.consumeClick()) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }

        Entity target = mc.crosshairPickEntity;
        if (target instanceof ItemEntity) {
            ItemEntity itemEntity = (ItemEntity) target;
            ItemStack stack = itemEntity.getItem();
            if (!stack.isEmpty() && itemEntity.isAlive() && stack.getItem() == ModItems.BONE_TONGS.get()) {
                ModNetworkHandler.CHANNEL.sendToServer(new OpenBoneTongsItemPacket(itemEntity.getId()));
                return;
            }
        }

        if (tryOpenHeldBoneTongs(mc.player)) {
            return;
        }
    }

    private static boolean tryOpenHeldBoneTongs(PlayerEntity player) {
        ItemStack mainHand = player.getMainHandItem();
        if (!mainHand.isEmpty() && mainHand.getItem() == ModItems.BONE_TONGS.get()) {
            ModNetworkHandler.CHANNEL.sendToServer(new OpenBoneTongsItemPacket(-1));
            return true;
        }

        ItemStack offHand = player.getOffhandItem();
        if (!offHand.isEmpty() && offHand.getItem() == ModItems.BONE_TONGS.get()) {
            ModNetworkHandler.CHANNEL.sendToServer(new OpenBoneTongsItemPacket(-1));
            return true;
        }

        return false;
    }
}

