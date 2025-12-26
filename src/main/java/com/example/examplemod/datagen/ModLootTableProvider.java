package com.example.examplemod.datagen;

import com.example.examplemod.ModBlocks;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.LootTableProvider;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.SurvivesExplosion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Автоматическая генерация loot tables для блоков.
 * 
 * МАСШТАБИРУЕМОСТЬ: 
 * - При добавлении блока он автоматически получит loot table
 * - dropsSelf() для обычных блоков
 * - Кастомные правила для особых блоков (руды, растения)
 */
public class ModLootTableProvider extends LootTableProvider {

    public ModLootTableProvider(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables() {
        return ImmutableList.of(
                Pair.of(ModBlockLootTables::new, LootParameterSets.BLOCK)
        );
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationTracker validationtracker) {
        // Можно добавить валидацию, но обычно не требуется
    }

    /**
     * Генератор loot tables для блоков.
     */
    public static class ModBlockLootTables extends BlockLootTables {
        
        @Override
        protected void addTables() {
            // === ПРОСТЫЕ БЛОКИ (drop themselves) ===
            registerSimpleBlocks();
            
            // === РУДЫ (с кастомными дропами) ===
            registerOreBlocks();
            
            // === СПЕЦИАЛЬНЫЕ БЛОКИ ===
            registerSpecialBlocks();
        }

        /**
         * Простые блоки которые дропают сами себя.
         */
        private void registerSimpleBlocks() {
            dropsSelf(ModBlocks.BONE_BLOCK);
            dropsSelf(ModBlocks.DANILKA_BLOCK);
            dropsSelf(ModBlocks.PARADISE_BLOCK);
            dropsSelf(ModBlocks.PARADISE_FENCE);
            dropsSelf(ModBlocks.PYRITE);
            dropsSelf(ModBlocks.TIN_ORE);
            dropsSelf(ModBlocks.TIN_GRAVEL_ORE);
            dropsSelf(ModBlocks.UNREFINED_TIN_ORE);
            dropsSelf(ModBlocks.GOLD_GRAVEL_ORE);
            dropsSelf(ModBlocks.UNREFINED_GOLD_ORE);
            dropsSelf(ModBlocks.FIREPIT_BLOCK);
            dropsSelf(ModBlocks.RAW_CLAY_POT);
            dropsSelf(ModBlocks.DIRTY_WATER_BLOCK);
            
            // Полублоки
            dropsSelf(ModBlocks.BRUSHWOOD_SLAB);
            dropsSelf(ModBlocks.BURNED_BRUSHWOOD_SLAB);
            dropsSelf(ModBlocks.OAK_SLAB);
            dropsSelf(ModBlocks.BIRCH_SLAB);
            dropsSelf(ModBlocks.SPRUCE_SLAB);
            dropsSelf(ModBlocks.JUNGLE_SLAB);
            dropsSelf(ModBlocks.ACACIA_SLAB);
            dropsSelf(ModBlocks.DARK_OAK_SLAB);
            dropsSelf(ModBlocks.CRIMSON_SLAB);
            dropsSelf(ModBlocks.WARPED_SLAB);
            dropsSelf(ModBlocks.STONE_SLAB);
        }

        /**
         * Руды с кастомными правилами дропа.
         * Примечание: BlockBreakMechanic уже обрабатывает логику дропа,
         * здесь генерируем базовые loot tables на случай если механика отключена.
         */
        private void registerOreBlocks() {
            // Impure Iron Ore - дропает себя (или обрабатывается механикой)
            dropsSelf(ModBlocks.IMPURE_IRON_ORE);
        }

        /**
         * Специальные блоки с кастомной логикой.
         */
        private void registerSpecialBlocks() {
            // Clay Pot - кастомная логика через BlockBreakMechanic
            // Здесь базовый loot table
            dropsSelf(ModBlocks.CLAY_POT);
            
            // Clay Shards - дропает себя
            dropsSelf(ModBlocks.CLAY_SHARDS);
            
            // Paradise Door - двойной блок, требует особой логики
            add(ModBlocks.PARADISE_DOOR.get(), 
                    (block) -> createSinglePropConditionTable(
                            block, 
                            net.minecraft.block.DoorBlock.HALF, 
                            net.minecraft.state.properties.DoubleBlockHalf.LOWER));
            
            // Растения - дропают себя
            dropsSelf(ModBlocks.RASPBERRY_BUSH);
            dropsSelf(ModBlocks.ELDERBERRY_BUSH);
            dropsSelf(ModBlocks.CRANBERRY_BUSH);
            dropsSelf(ModBlocks.ANGELICA);
            dropsSelf(ModBlocks.HORSERADISH_PLANT);
            dropsSelf(ModBlocks.GINGER_PLANT);
            dropsSelf(ModBlocks.FLAX_PLANT);
            dropsSelf(ModBlocks.DRIED_FLAX);
            
            // Bunch of Grass
            dropsSelf(ModBlocks.BUNCH_OF_GRASS);
            
            // Hanging Flax
            dropsSelf(ModBlocks.HANGING_FLAX);
        }

        /**
         * Хелпер: блок дропает сам себя.
         */
        private void dropsSelf(RegistryObject<Block> blockReg) {
            Block block = blockReg.get();
            // Используем dropSelf() из родительского класса
            dropSelf(block);
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            // Возвращаем все зарегистрированные блоки мода из ModRegistries
            return com.example.examplemod.ModRegistries.BLOCKS.getEntries().stream()
                    .map(RegistryObject::get)
                    .collect(Collectors.toList());
        }
    }
}

