package com.example.examplemod.datagen;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.ModBlocks;
import com.example.examplemod.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.RegistryObject;

/**
 * Автоматическая генерация моделей предметов.
 * 
 * МАСШТАБИРУЕМОСТЬ: При добавлении новых предметов просто добавь их в соответствующий метод.
 * DataGen сгенерирует JSON автоматически.
 */
public class ModItemModelProvider extends ItemModelProvider {

    public ModItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, ExampleMod.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        // === ПРОСТЫЕ ПРЕДМЕТЫ (generated модель) ===
        registerSimpleItems();
        
        // === ИНСТРУМЕНТЫ (handheld модель) ===
        registerHandheldItems();
        
        // === BLOCK ITEMS (наследуют модель блока) ===
        registerBlockItems();
        
        // === СПЕЦИАЛЬНЫЕ ПРЕДМЕТЫ (кастомная логика) ===
        registerSpecialItems();
    }

    /**
     * Простые предметы с текстурой (generated модель).
     * Для 90% предметов достаточно этого.
     */
    private void registerSimpleItems() {
        // Базовые ресурсы
        simpleItem(ModItems.EXAMPLE_ICON);
        simpleItem(ModItems.HEWN_STONE);
        simpleItem(ModItems.BRANCH);
        simpleItem(ModItems.LEAF);
        simpleItem(ModItems.HANDFUL_OF_SAND);
        simpleItem(ModItems.BIG_BONE);
        simpleItem(ModItems.SHARPENED_BONE);
        
        // Еда
        simpleItem(ModItems.RASPBERRY);
        simpleItem(ModItems.ELDERBERRY);
        simpleItem(ModItems.CRANBERRY);
        simpleItem(ModItems.ANGELICA_ROOT);
        simpleItem(ModItems.HORSERADISH);
        simpleItem(ModItems.GINGER);
        
        // Лён
        simpleItem(ModItems.FLAX);
        simpleItem(ModItems.FLAX_SEEDS);
        simpleItem(ModItems.SOAKED_FLAX);
        simpleItem(ModItems.FLAX_FIBERS);
        
        // Крафт материалы
        simpleItem(ModItems.SCRAPED_HIDE);
        simpleItem(ModItems.CLAY_MASS);
        simpleItem(ModItems.RAW_CLAY_CUP);
        simpleItem(ModItems.RAW_CLAY_BRICK);
        simpleItem(ModItems.DRIED_CLAY_BRICK);
        simpleItem(ModItems.WOODEN_COMB);
        simpleItem(ModItems.BONE_COMB);
        
        // Руды и металлургия
        simpleItem(ModItems.IRON_ORE_GRAVEL);
        simpleItem(ModItems.TIN_ORE_GRAVEL);
        simpleItem(ModItems.GOLD_ORE_GRAVEL);
        simpleItem(ModItems.SLAG);
        simpleItem(ModItems.PURE_IRON_ORE);
        simpleItem(ModItems.CALCINED_IRON_ORE);
        simpleItem(ModItems.CALCINED_TIN_ORE);
        simpleItem(ModItems.CALCINED_GOLD_ORE);
        simpleItem(ModItems.PYRITE_PIECE);
        simpleItem(ModItems.CLEANED_GRAVEL_TIN_ORE);
        simpleItem(ModItems.CLEANED_GRAVEL_GOLD_ORE);
        simpleItem(ModItems.SPONGE_IRON);
        simpleItem(ModItems.SPONGE_TIN);
        simpleItem(ModItems.SPONGE_GOLD);
        
        // Специальные
        simpleItem(ModItems.HEALING_ITEM);
        simpleItem(ModItems.HEAVEN_TICKET);
    }

    /**
     * Инструменты и оружие (handheld модель).
     */
    private void registerHandheldItems() {
        // Каменные инструменты
        handheldItem(ModItems.STONE_PICKAXE);
        handheldItem(ModItems.STONE_AXE);
        handheldItem(ModItems.STONE_SHOVEL);
        handheldItem(ModItems.STONE_HOE);
        handheldItem(ModItems.STONE_SWORD);
        handheldItem(ModItems.STONE_HAMMER);
        
        // Костяные инструменты
        handheldItem(ModItems.BONE_PICKAXE);
        handheldItem(ModItems.BONE_AXE);
        handheldItem(ModItems.BONE_SHOVEL);
        handheldItem(ModItems.BONE_HOE);
        handheldItem(ModItems.BONE_SWORD);
        handheldItem(ModItems.BONE_HAMMER);
        handheldItem(ModItems.BONE_TONGS);
        
        // Простые ножи и инструменты
        handheldItem(ModItems.ROUGH_STONE_KNIFE);
        handheldItem(ModItems.ROUGH_BONE_KNIFE);
        handheldItem(ModItems.PYRITE_FLINT);
    }

    /**
     * Предметы-блоки (используют модель родительского блока).
     */
    private void registerBlockItems() {
        // Используем модель блока напрямую
        blockItem(ModItems.BONE_BLOCK, "bone_block");
        blockItem(ModItems.DANILKA_BLOCK, "danilka_block");
        blockItem(ModItems.IMPURE_IRON_ORE, "impure_iron_ore");
        blockItem(ModItems.PARADISE_BLOCK, "paradise_block");
        blockItem(ModItems.PARADISE_FENCE, "paradise_fence");
        blockItem(ModItems.PYRITE, "pyrite");
        blockItem(ModItems.TIN_ORE, "tin_ore");
        blockItem(ModItems.DIRTY_GRAVEL_TIN_ORE, "tin_gravel_ore");
        blockItem(ModItems.UNREFINED_TIN_ORE, "unrefined_tin_ore");
        blockItem(ModItems.DIRTY_GRAVEL_GOLD_ORE, "gold_gravel_ore");
        blockItem(ModItems.UNREFINED_GOLD_ORE, "unrefined_gold_ore");
        blockItem(ModItems.FIREPIT_BLOCK, "firepit_block");
        
        // Растения
        blockItem(ModItems.RASPBERRY_BUSH, "raspberry_bush");
        blockItem(ModItems.ELDERBERRY_BUSH, "elderberry_bush");
        blockItem(ModItems.CRANBERRY_BUSH, "cranberry_bush");
        blockItem(ModItems.ANGELICA, "angelica");
        blockItem(ModItems.HORSERADISH_PLANT, "horseradish_plant");
        blockItem(ModItems.GINGER_PLANT, "ginger_plant");
        blockItem(ModItems.FLAX_PLANT, "flax_plant");
        
        // Полублоки
        blockItem(ModItems.BRUSHWOOD, "brushwood");
        blockItem(ModItems.OAK_SLAB, "oak_slab");
        blockItem(ModItems.BIRCH_SLAB, "birch_slab");
        blockItem(ModItems.SPRUCE_SLAB, "spruce_slab");
        blockItem(ModItems.JUNGLE_SLAB, "jungle_slab");
        blockItem(ModItems.ACACIA_SLAB, "acacia_slab");
        blockItem(ModItems.DARK_OAK_SLAB, "dark_oak_slab");
        blockItem(ModItems.CRIMSON_SLAB, "crimson_slab");
        blockItem(ModItems.WARPED_SLAB, "warped_slab");
        blockItem(ModItems.COBBLESTONE_SLAB, "cobblestone_slab");
    }

    /**
     * Специальные предметы с кастомной логикой.
     * Для предметов с состояниями, NBT данными, и т.д.
     */
    private void registerSpecialItems() {
        // Clay Cup с 3 состояниями (пустой, вода, грязная вода)
        // Модель определена в ItemModelsProperties в ExampleMod.java
        getBuilder(ModItems.CLAY_CUP.getId().getPath())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", modLoc("item/clay_cup"))
                .override()
                    .predicate(modLoc("fluid_state"), 1.0f)
                    .model(getBuilder("clay_cup_water")
                            .parent(new ModelFile.UncheckedModelFile("item/generated"))
                            .texture("layer0", modLoc("item/clay_cup_water")))
                    .end()
                .override()
                    .predicate(modLoc("fluid_state"), 2.0f)
                    .model(getBuilder("clay_cup_dirty_water")
                            .parent(new ModelFile.UncheckedModelFile("item/generated"))
                            .texture("layer0", modLoc("item/clay_cup_dirty_water")))
                    .end();
        
        // Clay Pot (кастомная модель)
        simpleItem(ModItems.CLAY_POT);
        simpleItem(ModItems.RAW_CLAY_POT);
        simpleItem(ModItems.CLAY_SHARDS);
        
        // Paradise Door (TallBlockItem)
        simpleItem(ModItems.PARADISE_DOOR);
        
        // Bunch of Grass
        simpleItem(ModItems.BUNCH_OF_GRASS);
        
        // Dirty Water Block Item
        simpleItem(ModItems.DIRTY_WATER_BLOCK_ITEM);
    }

    // === ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ===

    /**
     * Простой предмет с generated моделью.
     */
    private void simpleItem(RegistryObject<Item> item) {
        String path = item.getId().getPath();
        singleTexture(path,
                new ResourceLocation("item/generated"),
                "layer0",
                modLoc("item/" + path));
    }

    /**
     * Инструмент с handheld моделью.
     */
    private void handheldItem(RegistryObject<Item> item) {
        String path = item.getId().getPath();
        singleTexture(path,
                new ResourceLocation("item/handheld"),
                "layer0",
                modLoc("item/" + path));
    }

    /**
     * Предмет-блок (использует модель блока).
     */
    private void blockItem(RegistryObject<Item> item, String blockModelName) {
        String path = item.getId().getPath();
        getBuilder(path)
                .parent(new ModelFile.UncheckedModelFile(modLoc("block/" + blockModelName)));
    }

    // modLoc() уже есть в родительском классе ItemModelProvider, не нужно переопределять

    // === ДЛЯ БУДУЩЕГО: МАССОВАЯ ГЕНЕРАЦИЯ ===
    
    /**
     * ПРИМЕР: Массовая генерация схожих предметов через SmartItemRegistry.
     * 
     * Раскомментируй когда начнёшь использовать SmartItemRegistry активно.
     */
    /*
    private void generateCategoryItems(String category, String[] variants) {
        for (String variant : variants) {
            String itemName = variant + "_" + category;
            singleTexture(itemName,
                    new ResourceLocation("item/generated"),
                    "layer0",
                    modLoc("item/" + itemName));
        }
    }
    
    // Использование:
    // generateCategoryItems("ingot", new String[]{"copper", "tin", "bronze", "steel"});
    // Создаст модели для copper_ingot, tin_ingot, bronze_ingot, steel_ingot
    */
}

