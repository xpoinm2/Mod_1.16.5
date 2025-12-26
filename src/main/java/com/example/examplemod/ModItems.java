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
import com.example.examplemod.item.BranchItem;
import com.example.examplemod.item.ClayCupItem;
import com.example.examplemod.item.ClayPotBlockItem;
import com.example.examplemod.item.ClayShardsBlockItem;
import com.example.examplemod.item.BoneTongsItem;
import com.example.examplemod.item.HotRoastedOreItem;
import com.example.examplemod.item.GrassBundleItem;


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
            () -> new BranchItem(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Листочек: выпадает с листвы
    public static final RegistryObject<Item> LEAF = ITEMS.register("leaf",
            () -> new Item(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Горсть песка: выпадает при разрушении блока песка
    public static final RegistryObject<Item> HANDFUL_OF_SAND = ITEMS.register("handful_of_sand",
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

    // Выскобленная кожа: простое сырьё
    public static final RegistryObject<Item> SCRAPED_HIDE = ITEMS.register("scraped_hide",
            () -> new Item(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Глиняная масса: сырьё для создания глиняных изделий
    public static final RegistryObject<Item> CLAY_MASS = ITEMS.register("clay_mass",
            () -> new Item(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Сырая глиняная чашка: заготовка перед обжигом
    public static final RegistryObject<Item> RAW_CLAY_CUP = ITEMS.register("raw_clay_cup",
            () -> new Item(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Сырой глиняный кирпич: новый строительный материал
    public static final RegistryObject<Item> RAW_CLAY_BRICK = ITEMS.register("raw_clay_brick",
            () -> new Item(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Глиняная чашка: готовое изделие после обжига
    public static final RegistryObject<Item> CLAY_CUP = ITEMS.register("clay_cup",
            () -> new ClayCupItem(new Item.Properties()
                    .tab(ModCreativeTabs.EXAMPLE_TAB)
                    .stacksTo(16)));



    // Сырой глиняный горшок: заготовка перед обжигом
    public static final RegistryObject<Item> RAW_CLAY_POT = ITEMS.register("raw_clay_pot",
            () -> new BlockItem(ModBlocks.RAW_CLAY_POT.get(),
                    new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Глиняный горшок: готовое изделие после обжига
    public static final RegistryObject<Item> CLAY_POT = ITEMS.register("clay_pot",
            () -> new ClayPotBlockItem(ModBlocks.CLAY_POT.get(),
                    new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Глиняные осколки
    public static final RegistryObject<Item> CLAY_SHARDS = ITEMS.register("clay_shards",
            () -> new ClayShardsBlockItem(ModBlocks.CLAY_SHARDS.get(),
                    new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Блок грязной воды
    public static final RegistryObject<Item> DIRTY_WATER_BLOCK_ITEM = ITEMS.register("dirty_water",
            () -> new BlockItem(ModBlocks.DIRTY_WATER_BLOCK.get(),
                    new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Деревянный гребень: получается из ветки и отесанного камня
    public static final RegistryObject<Item> WOODEN_COMB = ITEMS.register("wooden_comb",
            () -> new Item(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Костяной гребень: получается из большой кости и отесанного камня
    public static final RegistryObject<Item> BONE_COMB = ITEMS.register("bone_comb",
            () -> new Item(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Грубый каменный нож: базовый инструмент
    public static final RegistryObject<Item> ROUGH_STONE_KNIFE = ITEMS.register("rough_stone_knife",
            () -> new Item(new Item.Properties()
                    .tab(ModCreativeTabs.EXAMPLE_TAB)
                    .durability(30)));

    // Грубый костяной нож: улучшенный инструмент
    public static final RegistryObject<Item> ROUGH_BONE_KNIFE = ITEMS.register("rough_bone_knife",
            () -> new Item(new Item.Properties()
                    .tab(ModCreativeTabs.EXAMPLE_TAB)
                    .durability(50)));

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

    public static final RegistryObject<Item> BONE_TONGS = ITEMS.register("bone_tongs",
            () -> new BoneTongsItem(new Item.Properties()
                    .tab(ModCreativeTabs.EXAMPLE_TAB)
                    .stacksTo(1)));

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

    // Железный рудный гравий: результат дробления руды
    public static final RegistryObject<Item> IRON_ORE_GRAVEL = ITEMS.register("iron_ore_gravel",
            () -> new Item(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Оловянный рудный гравий
    public static final RegistryObject<Item> TIN_ORE_GRAVEL = ITEMS.register("tin_ore_gravel",
            () -> new Item(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Золотой рудный гравий
    public static final RegistryObject<Item> GOLD_ORE_GRAVEL = ITEMS.register("gold_ore_gravel",
            () -> new Item(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Шлак: побочный продукт дробления
    public static final RegistryObject<Item> SLAG = ITEMS.register("slag",
            () -> new Item(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Очищенная гравийная железная руда: результат промывки кластера
    public static final RegistryObject<Item> PURE_IRON_ORE = ITEMS.register("pure_iron_ore",
            () -> new Item(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Обожжённая руда: результат прогрева чистой руды в кострище
    public static final RegistryObject<Item> CALCINED_IRON_ORE = ITEMS.register("roasted_iron_ore",
            () -> new Item(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Обожжённая оловянная руда
    public static final RegistryObject<Item> CALCINED_TIN_ORE = ITEMS.register("roasted_tin_ore",
            () -> new Item(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Обожжённая золотая руда
    public static final RegistryObject<Item> CALCINED_GOLD_ORE = ITEMS.register("roasted_gold_ore",
            () -> new Item(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Горячая обожжённая оловянная руда
    public static final RegistryObject<Item> HOT_TIN_ROASTED_ORE = ITEMS.register("hot_tin_roasted_ore",
            () -> new HotRoastedOreItem(CALCINED_TIN_ORE.get(), new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Горячая обожжённая золотая руда
    public static final RegistryObject<Item> HOT_GOLD_ROASTED_ORE = ITEMS.register("hot_gold_roasted_ore",
            () -> new HotRoastedOreItem(CALCINED_GOLD_ORE.get(), new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Горячая обожжённая железная руда
    public static final RegistryObject<Item> HOT_IRON_ROASTED_ORE = ITEMS.register("hot_iron_roasted_ore",
            () -> new HotRoastedOreItem(CALCINED_IRON_ORE.get(), new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Кусочек пирита
    public static final RegistryObject<Item> PYRITE_PIECE = ITEMS.register("pyrite_piece",
            () -> new Item(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Пиритовый блок как предмет
    public static final RegistryObject<Item> PYRITE = ITEMS.register("pyrite",
            () -> new BlockItem(ModBlocks.PYRITE.get(),
                    new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Грязная гравийная оловянная руда
    public static final RegistryObject<Item> DIRTY_GRAVEL_TIN_ORE = ITEMS.register("dirty_gravel_tin_ore",
            () -> new BlockItem(ModBlocks.TIN_GRAVEL_ORE.get(),
                    new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Неочищенная оловянная руда
    public static final RegistryObject<Item> UNREFINED_TIN_ORE = ITEMS.register("unrefined_tin_ore",
            () -> new BlockItem(ModBlocks.UNREFINED_TIN_ORE.get(),
                    new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Грязная гравийная золотая руда
    public static final RegistryObject<Item> DIRTY_GRAVEL_GOLD_ORE = ITEMS.register("dirty_gravel_gold_ore",
            () -> new BlockItem(ModBlocks.GOLD_GRAVEL_ORE.get(),
                    new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Неочищенная золотая руда
    public static final RegistryObject<Item> UNREFINED_GOLD_ORE = ITEMS.register("unrefined_gold_ore",
            () -> new BlockItem(ModBlocks.UNREFINED_GOLD_ORE.get(),
                    new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));
    
    // Очищённая гравийная оловянная руда
    public static final RegistryObject<Item> CLEANED_GRAVEL_TIN_ORE = ITEMS.register("cleaned_gravel_tin_ore",
            () -> new Item(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Очищённая гравийная золотая руда
    public static final RegistryObject<Item> CLEANED_GRAVEL_GOLD_ORE = ITEMS.register("cleaned_gravel_gold_ore",
            () -> new Item(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Губчатое железо: результат восстановления железной руды
    public static final RegistryObject<Item> SPONGE_IRON = ITEMS.register("sponge_iron",
            () -> new Item(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Губчатое олово: результат восстановления оловянной руды
    public static final RegistryObject<Item> SPONGE_TIN = ITEMS.register("sponge_tin",
            () -> new Item(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Губчатое золото: результат восстановления золотой руды
    public static final RegistryObject<Item> SPONGE_GOLD = ITEMS.register("sponge_gold",
            () -> new Item(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Оловянная руда как предмет
    public static final RegistryObject<Item> TIN_ORE = ITEMS.register("tin_ore",
            () -> new BlockItem(ModBlocks.TIN_ORE.get(),
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

    // Предмет для пучка трав
    public static final RegistryObject<Item> BUNCH_OF_GRASS = ITEMS.register("bunch_of_grass",
    () -> new GrassBundleItem(ModBlocks.BUNCH_OF_GRASS.get(),
                    new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Полублок хвороста как предмет
    public static final RegistryObject<Item> BRUSHWOOD_SLAB = ITEMS.register("brushwood_slab",
            () -> new BlockItem(ModBlocks.BRUSHWOOD_SLAB.get(),
                    new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Обгоревший полублок хвороста как предмет
    public static final RegistryObject<Item> BURNED_BRUSHWOOD_SLAB = ITEMS.register("brushwood_slab_burnt",
            () -> new BlockItem(ModBlocks.BURNED_BRUSHWOOD_SLAB.get(),
                    new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Полублок дуба как предмет
    public static final RegistryObject<Item> OAK_SLAB = ITEMS.register("oak_slab",
            () -> new BlockItem(ModBlocks.OAK_SLAB.get(),
                    new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Полублок березы как предмет
    public static final RegistryObject<Item> BIRCH_SLAB = ITEMS.register("birch_slab",
            () -> new BlockItem(ModBlocks.BIRCH_SLAB.get(),
                    new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Полублок ели как предмет
    public static final RegistryObject<Item> SPRUCE_SLAB = ITEMS.register("spruce_slab",
            () -> new BlockItem(ModBlocks.SPRUCE_SLAB.get(),
                    new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Полублок тропического дерева как предмет
    public static final RegistryObject<Item> JUNGLE_SLAB = ITEMS.register("jungle_slab",
            () -> new BlockItem(ModBlocks.JUNGLE_SLAB.get(),
                    new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Полублок акации как предмет
    public static final RegistryObject<Item> ACACIA_SLAB = ITEMS.register("acacia_slab",
            () -> new BlockItem(ModBlocks.ACACIA_SLAB.get(),
                    new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Полублок темного дуба как предмет
    public static final RegistryObject<Item> DARK_OAK_SLAB = ITEMS.register("dark_oak_slab",
            () -> new BlockItem(ModBlocks.DARK_OAK_SLAB.get(),
                    new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Полублок багрового дерева как предмет
    public static final RegistryObject<Item> CRIMSON_SLAB = ITEMS.register("crimson_slab",
            () -> new BlockItem(ModBlocks.CRIMSON_SLAB.get(),
                    new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Полублок искаженного дерева как предмет
    public static final RegistryObject<Item> WARPED_SLAB = ITEMS.register("warped_slab",
            () -> new BlockItem(ModBlocks.WARPED_SLAB.get(),
                    new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Полублок камня как предмет
    public static final RegistryObject<Item> STONE_SLAB = ITEMS.register("stone_slab",
            () -> new BlockItem(ModBlocks.STONE_SLAB.get(),
                    new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));

    // Блок кострища как предмет
    public static final RegistryObject<Item> FIREPIT_BLOCK = ITEMS.register("firepit_block",
            () -> new BlockItem(ModBlocks.FIREPIT_BLOCK.get(),
                    new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));




    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
