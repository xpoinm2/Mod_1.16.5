package com.example.examplemod;

import com.example.examplemod.client.ClientInteractionHandler;
import com.example.examplemod.ModItems;
import com.example.examplemod.ModCreativeTabs;
import com.example.examplemod.ModBlocks;
import com.example.examplemod.world.WorldGenRegistry;
import com.example.examplemod.network.ModNetworkHandler;
import com.example.examplemod.server.ThirstHandler;
import com.example.examplemod.server.RestHandler;
import com.example.examplemod.server.BlockBreakHandler;
import com.example.examplemod.server.PebbleSharpenHandler;
import com.example.examplemod.server.ColdHandler;
import com.example.examplemod.server.HypothermiaHandler;
import com.example.examplemod.server.VirusHandler;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import com.example.examplemod.server.PebbleSpawnHandler;
import com.example.examplemod.server.BarkStripHandler;


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
        ModItems.register(modBus);
        ModBlocks.register(modBus);
        ModCreativeTabs.register(modBus);
        WorldGenRegistry.register(modBus);
        modBus.addListener(this::commonSetup);
        modBus.addListener(this::clientSetup);
    }

    /** Серверная и общая инициализация (регистрируем ThirstHandler) */
    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("ExampleMod common setup");
        MinecraftForge.EVENT_BUS.register(ThirstHandler.class);
        MinecraftForge.EVENT_BUS.register(RestHandler.class);
        MinecraftForge.EVENT_BUS.register(BlockBreakHandler.class);
        MinecraftForge.EVENT_BUS.register(ColdHandler.class);
        MinecraftForge.EVENT_BUS.register(HypothermiaHandler.class);
        MinecraftForge.EVENT_BUS.register(VirusHandler.class);
        // Register handler responsible for pebble spawning
        MinecraftForge.EVENT_BUS.register(PebbleSpawnHandler.class);
        // Register handler that upgrades pebbles when mining stone
        MinecraftForge.EVENT_BUS.register(PebbleSharpenHandler.class);
        // Register handler that strips logs and drops bark
        MinecraftForge.EVENT_BUS.register(BarkStripHandler.class);

    }

    /** Клиентская инициализация (регистрируем обработку кликов по воде) */
    private void clientSetup(final FMLClientSetupEvent event) {
        LOGGER.info("ExampleMod client setup");
        MinecraftForge.EVENT_BUS.register(ClientInteractionHandler.class);
        RenderTypeLookup.setRenderLayer(ModBlocks.RASPBERRY_BUSH.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.ELDERBERRY_BUSH.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.CRANBERRY_BUSH.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.ANGELICA.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.HORSERADISH_PLANT.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.GINGER_PLANT.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.MUHOMOR.get(), RenderType.cutout());
    }
}
