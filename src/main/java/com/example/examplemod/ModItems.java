// === FILE src/main/java/com/example/examplemod/ModItems.java
package com.example.examplemod;


import net.minecraft.item.Item;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import com.example.examplemod.item.HealingItem;


public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ExampleMod.MODID);

    // Иконка для вкладки: простой предмет
    public static final RegistryObject<Item> EXAMPLE_ICON = ITEMS.register("example_icon",
            () -> new Item(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Целебный предмет: при использовании лечит 2 сердца
    public static final RegistryObject<Item> HEALING_ITEM = ITEMS.register("healing_item",
            () -> new HealingItem(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Камешек: простой предмет
    public static final RegistryObject<Item> PEBBLE = ITEMS.register("pebble",
            () -> new Item(new Item.Properties()
                    .tab(ModCreativeTabs.EXAMPLE_TAB)
                    .durability(20)));

    // Острый камешек: получается при обработке обычного камешка
    public static final RegistryObject<Item> SHARP_PEBBLE = ITEMS.register("sharp_pebble",
            () -> new Item(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Кора: получается при обработке бревна острым камнем
    public static final RegistryObject<Item> BARK = ITEMS.register("bark",
            () -> new Item(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Малина: еда, восстанавливающая небольшое количество голода
    public static final RegistryObject<Item> RASPBERRY = ITEMS.register("raspberry",
            () -> new Item(new Item.Properties()
                    .tab(ModCreativeTabs.EXAMPLE_TAB)
                    .food(new net.minecraft.item.Food.Builder()
                            .nutrition(1) // 0.5 icons; closest to 0.2
                            .saturationMod(0.2f)
                            .effect(() -> new EffectInstance(Effects.MOVEMENT_SPEED, 40, 0), 1.0f)
                            .build())));

    // Предмет для куста малины
    public static final RegistryObject<Item> RASPBERRY_BUSH = ITEMS.register("raspberry_bush",
            () -> new net.minecraft.item.BlockItem(ModBlocks.RASPBERRY_BUSH.get(),
                    new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
