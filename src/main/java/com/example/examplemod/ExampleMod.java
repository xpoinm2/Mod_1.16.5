package com.example.examplemod;

import com.example.examplemod.world.WorldGenRegistry;
import com.example.examplemod.world.ModBiomes;
import com.example.examplemod.network.ModNetworkHandler;
import com.example.examplemod.ModItems;
import com.example.examplemod.ModBlocks;
import com.example.examplemod.ModContainers;
import com.example.examplemod.ModEntities;
import com.example.examplemod.ModTileEntities;
import com.example.examplemod.ModFluids;
import com.example.examplemod.ModRegistries;
import com.example.examplemod.server.mechanics.ModMechanics;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


// GeckoLib удален - не используется в проекте



@Mod(ExampleMod.MODID)
public class ExampleMod {
    public static final String MODID = "examplemod";
    public static final String KEY_CATEGORY = "key.categories.examplemod";
    private static final Logger LOGGER = LogManager.getLogger();

    public ExampleMod() {

        // GeckoLib.initialize(); // GeckoLib удален

        ModNetworkHandler.register();


        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Важно: форсируем загрузку классов с RegistryObject'ами ДО того,
        // как Forge начнёт RegistryEvent.Register (иначе DeferredRegister не примет новые записи).
        ModBlocks.init();
        ModItems.init();
        ModContainers.init();
        ModTileEntities.init();
        ModEntities.init();

        // Унифицированная регистрация всех компонентов
        ModRegistries.register(modBus);

        // Инициализация реестра механик (можно добавлять модули постепенно со временем)
        ModMechanics.init();

        modBus.addListener(this::commonSetup);
    }


    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("ExampleMod common setup");
        // ВАЖНО: обработчики Forge-событий уже регистрируются через @Mod.EventBusSubscriber(bus = FORGE),
        // поэтому их НЕ нужно дополнительно регистрировать в MinecraftForge.EVENT_BUS (иначе будут двойные вызовы).

        event.enqueueWork(() -> {
            WorldGenRegistry.register();
            ModBiomes.setupBiomes();
            
            // ОПЦИОНАЛЬНО: Анализ текстур (раскомментируй когда будет 200+ текстур)
            // TextureOrganizer.analyzeTextures();
            // Проверь логи для рекомендаций по оптимизации
        });
    }

}
