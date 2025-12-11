package com.example.examplemod.client;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.ModItems;
import com.example.examplemod.network.ModNetworkHandler;
import com.example.examplemod.network.OpenBoneTongsItemPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
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

        HitResult hit = mc.hitResult;
        if (!(hit instanceof EntityHitResult entityHit)) {
            return;
        }

        if (!(entityHit.getEntity() instanceof ItemEntity itemEntity)) {
            return;
        }

        ItemStack stack = itemEntity.getItem();
        if (stack.isEmpty() || !itemEntity.isAlive()) {
            return;
        }

        if (stack.getItem() != ModItems.BONE_TONGS.get()) {
            return;
        }

        ModNetworkHandler.CHANNEL.sendToServer(new OpenBoneTongsItemPacket(itemEntity.getId()));
    }
}

