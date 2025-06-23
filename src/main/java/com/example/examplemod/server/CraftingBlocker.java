// === FILE src/main/java/com/example/examplemod/server/CraftingBlocker.java
package com.example.examplemod.server;

import com.example.examplemod.ExampleMod;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.player.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Отменяет все vanilla-рецепты (namespace == "minecraft") в момент крафта.
 */

public class CraftingBlocker {
    @SubscribeEvent
    public static void onItemCrafted(ItemCraftedEvent ev) {
        // результат, который игрок только что скрафтил
        ItemStack result = ev.getCrafting();
        ResourceLocation id = result.getItem().getRegistryName();
        if (id != null && "minecraft".equals(id.getNamespace())) {
            // получаем всю сетку крафта (IInventory, на деле CraftingInventory)
            IInventory inv = ev.getInventory();
            // чистим все слоты — и результат, и ингредиенты
            inv.clearContent();
            // сообщаем клиенту, чтобы GUI сразу обновился
            ev.getPlayer().containerMenu.broadcastChanges();
        }
    }
}
