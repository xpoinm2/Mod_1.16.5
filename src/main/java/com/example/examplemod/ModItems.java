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
import com.example.examplemod.item.ElderberryItem;
import com.example.examplemod.item.AngelicaRootItem;
import com.example.examplemod.item.HorseradishItem;
import com.example.examplemod.item.GingerItem;


public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ExampleMod.MODID);

    // Иконка для вкладки: простой предмет
    public static final RegistryObject<Item> EXAMPLE_ICON = ITEMS.register("example_icon",
            () -> new Item(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Целебный предмет: при использовании лечит 2 сердца
    public static final RegistryObject<Item> HEALING_ITEM = ITEMS.register("healing_item",
            () -> new HealingItem(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Отесанный камень: можно найти в речках
    public static final RegistryObject<Item> HEWN_STONE = ITEMS.register("hewn_stone",
            () -> new Item(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Ветка: выпадает с листвы
    public static final RegistryObject<Item> BRANCH = ITEMS.register("branch",
            () -> new Item(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Листочек: выпадает с листвы
    public static final RegistryObject<Item> LEAF = ITEMS.register("leaf",
            () -> new Item(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Большая кость: трофей, который можно получить с некоторых животных
    public static final RegistryObject<Item> BIG_BONE = ITEMS.register("big_bone",
            () -> new Item(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Заостренная кость: получается заточкой большой кости
    public static final RegistryObject<Item> SHARPENED_BONE = ITEMS.register("sharpened_bone",
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

    // Бузина: снижает простуду на 1%% при поедании
    public static final RegistryObject<Item> ELDERBERRY = ITEMS.register("elderberry",
            () -> new ElderberryItem(new Item.Properties()
                    .tab(ModCreativeTabs.EXAMPLE_TAB)
                    .food(new net.minecraft.item.Food.Builder()
                            .nutrition(1)
                            .saturationMod(0.1f)
                            .build())));

    // Клюква: при поедании даёт эффект ускорения добычи
    public static final RegistryObject<Item> CRANBERRY = ITEMS.register("cranberry",
            () -> new Item(new Item.Properties()
                    .tab(ModCreativeTabs.EXAMPLE_TAB)
                    .food(new net.minecraft.item.Food.Builder()
                            .nutrition(1)
                            .saturationMod(0.2f)
                            .effect(() -> new EffectInstance(Effects.DIG_SPEED, 40, 0), 1.0f)
                            .build())));

    // Корень дягеля: снижает длительность отравления на 5 секунд, голод не восстанавливает
    public static final RegistryObject<Item> ANGELICA_ROOT = ITEMS.register("angelica_root",
            () -> new AngelicaRootItem(new Item.Properties()
                    .tab(ModCreativeTabs.EXAMPLE_TAB)
                    .food(new net.minecraft.item.Food.Builder()
                            .nutrition(0)
                            .saturationMod(0.0f)
                            .alwaysEat()
                            .build())));

    // Хрен: снижает показатель вирусов на 5%, голод не восстанавливает
    public static final RegistryObject<Item> HORSERADISH = ITEMS.register("horseradish",
            () -> new HorseradishItem(new Item.Properties()
                    .tab(ModCreativeTabs.EXAMPLE_TAB)
                    .food(new net.minecraft.item.Food.Builder()
                            .nutrition(0)
                            .saturationMod(0.0f)
                            .alwaysEat()
                            .build())));

    // Имбирь: снижает переохлаждение на 1%, голод не восстанавливает
    public static final RegistryObject<Item> GINGER = ITEMS.register("ginger",
            () -> new GingerItem(new Item.Properties()
                    .tab(ModCreativeTabs.EXAMPLE_TAB)
                    .food(new net.minecraft.item.Food.Builder()
                            .nutrition(0)
                            .saturationMod(0.0f)
                            .alwaysEat()
                            .build())));

    // Предмет для куста малины
    public static final RegistryObject<Item> RASPBERRY_BUSH = ITEMS.register("raspberry_bush",
            () -> new net.minecraft.item.BlockItem(ModBlocks.RASPBERRY_BUSH.get(),
                    new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Предмет для куста бузины
    public static final RegistryObject<Item> ELDERBERRY_BUSH = ITEMS.register("elderberry_bush",
            () -> new net.minecraft.item.BlockItem(ModBlocks.ELDERBERRY_BUSH.get(),
                    new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Предмет для куста клюквы
    public static final RegistryObject<Item> CRANBERRY_BUSH = ITEMS.register("cranberry_bush",
            () -> new net.minecraft.item.BlockItem(ModBlocks.CRANBERRY_BUSH.get(),
                    new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Предмет для дягеля
    public static final RegistryObject<Item> ANGELICA = ITEMS.register("angelica",
            () -> new net.minecraft.item.BlockItem(ModBlocks.ANGELICA.get(),
                    new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Предмет для растения хрена
    public static final RegistryObject<Item> HORSERADISH_PLANT = ITEMS.register("horseradish_plant",
            () -> new net.minecraft.item.BlockItem(ModBlocks.HORSERADISH_PLANT.get(),
                    new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Предмет для растения имбиря
    public static final RegistryObject<Item> GINGER_PLANT = ITEMS.register("ginger_plant",
            () -> new net.minecraft.item.BlockItem(ModBlocks.GINGER_PLANT.get(),
                    new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
