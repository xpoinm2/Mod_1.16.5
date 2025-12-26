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
            // Генераторы для серверной части
            generator.addProvider(new ModRecipeProvider(generator));
        }

        if (event.includeClient()) {
            // Генераторы для клиентской части
            generator.addProvider(new ModBlockStateProvider(generator, event.getExistingFileHelper()));
        }
    }
}
