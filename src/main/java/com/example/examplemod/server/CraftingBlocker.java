// === FILE src/main/java/com/example/examplemod/server/CraftingBlocker.java
package com.example.examplemod.server;

import com.example.examplemod.ExampleMod;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.AxeItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SwordItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.player.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Отменяет все vanilla-рецепты инструментов (namespace == "minecraft") в момент крафта.
 */

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CraftingBlocker {
    @SubscribeEvent
    public static void onItemCrafted(ItemCraftedEvent ev) {
        // результат, который игрок только что скрафтил
        ItemStack result = ev.getCrafting();
        if (!isVanillaTool(result)) {
            return;
        }

        // получаем всю сетку крафта (IInventory, на деле CraftingInventory)
        IInventory inv = ev.getInventory();
        // чистим все слоты — и результат, и ингредиенты
        inv.clearContent();
        // сообщаем клиенту, чтобы GUI сразу обновился
        ev.getPlayer().containerMenu.broadcastChanges();
    }

    private static boolean isVanillaTool(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        ResourceLocation id = stack.getItem().getRegistryName();
        if (id == null || !"minecraft".equals(id.getNamespace())) {
            return false;
        }

        Item item = stack.getItem();

        return item instanceof PickaxeItem
                || item instanceof AxeItem
                || item instanceof ShovelItem
                || item instanceof HoeItem
                || item instanceof SwordItem;
    }
}