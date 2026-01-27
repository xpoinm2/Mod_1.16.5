// === FILE src/main/java/com/example/examplemod/server/CraftingBlocker.java
package com.example.examplemod.server;

import com.example.examplemod.ExampleMod;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.AxeItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SwordItem;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.player.PlayerEvent.ItemCraftedEvent;

/**
 * Отменяет все vanilla-рецепты инструментов и досок (namespace == "minecraft") в момент крафта.
 */

public class CraftingBlocker {
    public static void onItemCrafted(ItemCraftedEvent ev) {
        // результат, который игрок только что скрафтил
        ItemStack result = ev.getCrafting();
        if (!isVanillaTool(result) && !isVanillaPlanks(result) && !isVanillaFurnace(result)) {
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

    private static boolean isVanillaPlanks(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        ResourceLocation id = stack.getItem().getRegistryName();
        if (id == null || !"minecraft".equals(id.getNamespace())) {
            return false;
        }

        // Проверяем, является ли это ванильной доской
        Item item = stack.getItem();
        if (item.is(ItemTags.PLANKS)) {
            // Проверяем, что это именно ванильная доска, а не из мода
            String path = id.getPath();
            return path.equals("oak_planks")
                    || path.equals("spruce_planks")
                    || path.equals("birch_planks")
                    || path.equals("jungle_planks")
                    || path.equals("acacia_planks")
                    || path.equals("dark_oak_planks")
                    || path.equals("crimson_planks")
                    || path.equals("warped_planks");
        }

        return false;
    }

    private static boolean isVanillaFurnace(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        ResourceLocation id = stack.getItem().getRegistryName();
        if (id == null || !"minecraft".equals(id.getNamespace())) {
            return false;
        }
        return stack.getItem() == Items.FURNACE;
    }
}
