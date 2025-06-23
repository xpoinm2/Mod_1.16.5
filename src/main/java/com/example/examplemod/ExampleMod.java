package com.example.examplemod;

import com.example.examplemod.client.ClientInteractionHandler;
import com.example.examplemod.network.ModNetworkHandler;
import com.example.examplemod.server.ThirstHandler;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ExampleMod.MODID)
public class ExampleMod {
    public static final String MODID = "examplemod";
    private static final Logger LOGGER = LogManager.getLogger();

    public ExampleMod() {
        // 1) Регистрируем наш канал + пакеты
        ModNetworkHandler.register();

        // 2) Подписываемся на жизненный цикл загрузки модов
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::commonSetup);
        modBus.addListener(this::clientSetup);
    }

    /** Серверная и общая инициализация (регистрируем ThirstHandler) */
    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("ExampleMod common setup");
        MinecraftForge.EVENT_BUS.register(ThirstHandler.class);
    }

    /** Клиентская инициализация (регистрируем обработку кликов по воде) */
    private void clientSetup(final FMLClientSetupEvent event) {
        LOGGER.info("ExampleMod client setup");
        MinecraftForge.EVENT_BUS.register(ClientInteractionHandler.class);
    }
}
