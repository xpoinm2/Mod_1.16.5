// === FILE src/main/java/com/example/examplemod/ModItems.java
package com.example.examplemod;

import net.minecraft.item.AxeItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.TallBlockItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemTier;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SwordItem;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.common.ForgeSpawnEggItem;
import com.example.examplemod.item.HealingItem;
import com.example.examplemod.item.ElderberryItem;
import com.example.examplemod.item.AngelicaRootItem;
import com.example.examplemod.item.HorseradishItem;
import com.example.examplemod.item.GingerItem;
import com.example.examplemod.item.PyriteFlintItem;
import com.example.examplemod.item.HeavenTicketItem;


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

    // Лён: простой растительный ресурс
    public static final RegistryObject<Item> FLAX = ITEMS.register("flax",
            () -> new Item(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Семена льна
    public static final RegistryObject<Item> FLAX_SEEDS = ITEMS.register("flax_seeds",
            () -> new Item(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Вымоченный лён: получается при замачивании льна в воде
    public static final RegistryObject<Item> SOAKED_FLAX = ITEMS.register("soaked_flax",
            () -> new Item(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Волокна льна: получаются обработкой вымоченного льна
    public static final RegistryObject<Item> FLAX_FIBERS = ITEMS.register("flax_fibers",
            () -> new Item(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Деревянный гребень: получается из ветки и отесанного камня
    public static final RegistryObject<Item> WOODEN_COMB = ITEMS.register("wooden_comb",
            () -> new Item(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Костяной гребень: получается из большой кости и отесанного камня
    public static final RegistryObject<Item> BONE_COMB = ITEMS.register("bone_comb",
            () -> new Item(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // === Stone Tools ===
    public static final RegistryObject<Item> STONE_PICKAXE = ITEMS.register("stone_pickaxe",
            () -> new PickaxeItem(ItemTier.STONE,
                    1, -2.8f, new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    public static final RegistryObject<Item> STONE_AXE = ITEMS.register("stone_axe",
            () -> new AxeItem(ItemTier.STONE,
                    7.0f, -3.2f, new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    public static final RegistryObject<Item> STONE_SHOVEL = ITEMS.register("stone_shovel",
            () -> new ShovelItem(ItemTier.STONE,
                    1.5f, -3.0f, new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    public static final RegistryObject<Item> STONE_HOE = ITEMS.register("stone_hoe",
            () -> new HoeItem(ItemTier.STONE,
                    0, -2.0f, new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    public static final RegistryObject<Item> STONE_SWORD = ITEMS.register("stone_sword",
            () -> new SwordItem(ItemTier.STONE,
                    3, -2.4f, new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Каменный молот: простой инструмент
    public static final RegistryObject<Item> STONE_HAMMER = ITEMS.register("stone_hammer",
            () -> new Item(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)
                    .durability(ItemTier.STONE.getUses())));

    // === Bone Tools ===
    public static final RegistryObject<Item> BONE_PICKAXE = ITEMS.register("bone_pickaxe",
            () -> new PickaxeItem(ItemTier.STONE,
                    1, -2.8f, new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    public static final RegistryObject<Item> BONE_AXE = ITEMS.register("bone_axe",
            () -> new AxeItem(ItemTier.STONE,
                    7.0f, -3.2f, new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    public static final RegistryObject<Item> BONE_SHOVEL = ITEMS.register("bone_shovel",
            () -> new ShovelItem(ItemTier.STONE,
                    1.5f, -3.0f, new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    public static final RegistryObject<Item> BONE_HOE = ITEMS.register("bone_hoe",
            () -> new HoeItem(ItemTier.STONE,
                    0, -2.0f, new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    public static final RegistryObject<Item> BONE_SWORD = ITEMS.register("bone_sword",
            () -> new SwordItem(ItemTier.STONE,
                    3, -2.4f, new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Костяной молот: простой инструмент
    public static final RegistryObject<Item> BONE_HAMMER = ITEMS.register("bone_hammer",
            () -> new Item(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)
                    .durability(ItemTier.STONE.getUses())));

    // Костяной блок как предмет
    public static final RegistryObject<Item> BONE_BLOCK = ITEMS.register("bone_block",
            () -> new BlockItem(ModBlocks.BONE_BLOCK.get(),
                    new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Данилка как предмет
    public static final RegistryObject<Item> DANILKA_BLOCK = ITEMS.register("danilka_block",
            () -> new BlockItem(ModBlocks.DANILKA_BLOCK.get(),
                    new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Железная руда с примесями как предмет
    public static final RegistryObject<Item> IMPURE_IRON_ORE = ITEMS.register("impure_iron_ore",
            () -> new BlockItem(ModBlocks.IMPURE_IRON_ORE.get(),
                    new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Райский блок как предмет
    public static final RegistryObject<Item> PARADISE_BLOCK = ITEMS.register("paradise_block",
            () -> new BlockItem(ModBlocks.PARADISE_BLOCK.get(),
                    new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Райская дверь как предмет
    public static final RegistryObject<Item> PARADISE_DOOR = ITEMS.register("paradise_door",
            () -> new TallBlockItem(ModBlocks.PARADISE_DOOR.get(),
                    new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Райская ограда как предмет
    public static final RegistryObject<Item> PARADISE_FENCE = ITEMS.register("paradise_fence",
            () -> new BlockItem(ModBlocks.PARADISE_FENCE.get(),
                    new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Билет в рай: телепортирует игрока в райское измерение
    public static final RegistryObject<Item> HEAVEN_TICKET = ITEMS.register("heaven_ticket",
            () -> new HeavenTicketItem(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB).stacksTo(1)));

    // Железный кластер: результат дробления руды
    public static final RegistryObject<Item> IRON_CLUSTER = ITEMS.register("iron_cluster",
            () -> new Item(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Чистая железная руда: результат промывки кластера
    public static final RegistryObject<Item> PURE_IRON_ORE = ITEMS.register("pure_iron_ore",
            () -> new Item(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Кусочек пирита
    public static final RegistryObject<Item> PYRITE_PIECE = ITEMS.register("pyrite_piece",
            () -> new Item(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Пиритовый блок как предмет
    public static final RegistryObject<Item> PYRITE = ITEMS.register("pyrite",
            () -> new BlockItem(ModBlocks.PYRITE.get(),
                    new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Пиритовое огниво
    public static final RegistryObject<Item> PYRITE_FLINT = ITEMS.register("pyrite_flint",
            () -> new PyriteFlintItem(new Item.Properties()
                    .tab(ModCreativeTabs.EXAMPLE_TAB)
                    .durability(20)));

    // Яйцо призыва бобра
    public static final RegistryObject<Item> BEAVER_SPAWN_EGG = ITEMS.register("beaver_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.BEAVER,
                    0x6B4C2E, 0x3B2A1A,
                    new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Предмет для куста малины
    public static final RegistryObject<Item> RASPBERRY_BUSH = ITEMS.register("raspberry_bush",
            () -> new BlockItem(ModBlocks.RASPBERRY_BUSH.get(),
                    new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Предмет для куста бузины
    public static final RegistryObject<Item> ELDERBERRY_BUSH = ITEMS.register("elderberry_bush",
            () -> new BlockItem(ModBlocks.ELDERBERRY_BUSH.get(),
                    new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Предмет для куста клюквы
    public static final RegistryObject<Item> CRANBERRY_BUSH = ITEMS.register("cranberry_bush",
            () -> new BlockItem(ModBlocks.CRANBERRY_BUSH.get(),
                    new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Предмет для дягеля
    public static final RegistryObject<Item> ANGELICA = ITEMS.register("angelica",
            () -> new BlockItem(ModBlocks.ANGELICA.get(),
                    new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Предмет для растения хрена
    public static final RegistryObject<Item> HORSERADISH_PLANT = ITEMS.register("horseradish_plant",
            () -> new BlockItem(ModBlocks.HORSERADISH_PLANT.get(),
                    new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Предмет для растения имбиря
    public static final RegistryObject<Item> GINGER_PLANT = ITEMS.register("ginger_plant",
            () -> new BlockItem(ModBlocks.GINGER_PLANT.get(),
                    new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Предмет для растения льна
    public static final RegistryObject<Item> FLAX_PLANT = ITEMS.register("flax_plant",
            () -> new BlockItem(ModBlocks.FLAX_PLANT.get(),
                    new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Полублок хвороста как предмет
    public static final RegistryObject<Item> BRUSHWOOD_SLAB = ITEMS.register("brushwood_slab",
            () -> new BlockItem(ModBlocks.BRUSHWOOD_SLAB.get(),
                    new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Обгоревший полублок хвороста как предмет
    public static final RegistryObject<Item> BURNED_BRUSHWOOD_SLAB = ITEMS.register("brushwood_slab_burnt",
            () -> new BlockItem(ModBlocks.BURNED_BRUSHWOOD_SLAB.get(),
                    new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Блок кострища как предмет
    public static final RegistryObject<Item> FIREPIT_BLOCK = ITEMS.register("firepit_block",
            () -> new BlockItem(ModBlocks.FIREPIT_BLOCK.get(),
                    new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
