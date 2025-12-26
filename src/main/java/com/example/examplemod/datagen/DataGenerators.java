package com.example.examplemod.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

/**
 * Регистратор data generators для автоматической генерации рецептов, моделей и других данных
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();

        if (event.includeServer()) {
            // === СЕРВЕРНЫЕ ГЕНЕРАТОРЫ ===
            
            // Рецепты крафта
            generator.addProvider(new ModRecipeProvider(generator));
            
            // Loot tables (дроп с блоков)
            generator.addProvider(new ModLootTableProvider(generator));
        }

        if (event.includeClient()) {
            // === КЛИЕНТСКИЕ ГЕНЕРАТОРЫ ===
            
            // Модели и текстуры блоков
            generator.addProvider(new ModBlockStateProvider(generator, event.getExistingFileHelper()));
            
            // Модели предметов
            generator.addProvider(new ModItemModelProvider(generator, event.getExistingFileHelper()));
        }
    }
}
