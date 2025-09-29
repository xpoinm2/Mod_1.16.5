package com.example.examplemod;

import com.example.examplemod.client.ClientInteractionHandler;
import com.example.examplemod.client.render.BeaverRenderer;
import com.example.examplemod.client.render.HeavenDimensionRenderInfo;
import com.example.examplemod.client.screen.FirepitScreen;
import com.example.examplemod.entity.BeaverEntity;
import com.example.examplemod.world.WorldGenRegistry;
import com.example.examplemod.world.ModBiomes;
import com.example.examplemod.world.ModFeatures;
import com.example.examplemod.network.ModNetworkHandler;
import com.example.examplemod.ModItems;
import com.example.examplemod.ModBlocks;
import com.example.examplemod.ModCreativeTabs;
import com.example.examplemod.ModContainers;
import com.example.examplemod.ModEntityTypes;
import com.example.examplemod.server.ThirstHandler;
import com.example.examplemod.server.RestHandler;
import com.example.examplemod.server.BlockBreakHandler;
import com.example.examplemod.server.ColdHandler;
import com.example.examplemod.server.HypothermiaHandler;
import com.example.examplemod.server.VirusHandler;
import com.example.examplemod.server.CraftingBlocker;
import com.example.examplemod.server.HewnStoneSpawnHandler;
import com.example.examplemod.server.FlaxDryingHandler;
import com.example.examplemod.server.FirepitStructureHandler;
import com.example.examplemod.server.RedMushroomHandler;

import net.minecraft.client.world.DimensionRenderInfo;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import software.bernie.geckolib3.GeckoLib;

import java.util.Map;


@Mod(ExampleMod.MODID)
public class ExampleMod {
    public static final String MODID = "examplemod";
    private static final Logger LOGGER = LogManager.getLogger();

    public ExampleMod() {

        GeckoLib.initialize();

        ModNetworkHandler.register();


        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.register(modBus);
        ModBlocks.register(modBus);
        ModCreativeTabs.register(modBus);
        ModContainers.register(modBus);
        ModEntityTypes.register(modBus);
        ModBiomes.register(modBus);
        ModFeatures.register(modBus);
        modBus.addListener(this::commonSetup);
        modBus.addListener(this::clientSetup);
    }


    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("ExampleMod common setup");
        MinecraftForge.EVENT_BUS.register(ThirstHandler.class);
        MinecraftForge.EVENT_BUS.register(RestHandler.class);
        MinecraftForge.EVENT_BUS.register(BlockBreakHandler.class);
        MinecraftForge.EVENT_BUS.register(ColdHandler.class);
        MinecraftForge.EVENT_BUS.register(HypothermiaHandler.class);
        MinecraftForge.EVENT_BUS.register(VirusHandler.class);
        MinecraftForge.EVENT_BUS.register(RedMushroomHandler.class);
        MinecraftForge.EVENT_BUS.register(FlaxDryingHandler.class);
        MinecraftForge.EVENT_BUS.register(CraftingBlocker.class);
        MinecraftForge.EVENT_BUS.register(FirepitStructureHandler.class);


        MinecraftForge.EVENT_BUS.register(HewnStoneSpawnHandler.class);

        event.enqueueWork(() -> {
            WorldGenRegistry.register();
            ModBiomes.setupBiomes();
        });
    }


    private void clientSetup(final FMLClientSetupEvent event) {
        LOGGER.info("ExampleMod client setup");
        MinecraftForge.EVENT_BUS.register(ClientInteractionHandler.class);
        event.enqueueWork(() -> {
            net.minecraft.client.gui.ScreenManager.register(ModContainers.FIREPIT.get(), FirepitScreen::new);
            RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.BEAVER.get(), BeaverRenderer::new);
            registerDimensionRenderInfo(new ResourceLocation(ExampleMod.MODID, "heaven_sky"),
                    new HeavenDimensionRenderInfo());
            RenderTypeLookup.setRenderLayer(ModBlocks.RASPBERRY_BUSH.get(), RenderType.cutout());
            RenderTypeLookup.setRenderLayer(ModBlocks.ELDERBERRY_BUSH.get(), RenderType.cutout());
            RenderTypeLookup.setRenderLayer(ModBlocks.CRANBERRY_BUSH.get(), RenderType.cutout());
            RenderTypeLookup.setRenderLayer(ModBlocks.ANGELICA.get(), RenderType.cutout());
            RenderTypeLookup.setRenderLayer(ModBlocks.HORSERADISH_PLANT.get(), RenderType.cutout());
            RenderTypeLookup.setRenderLayer(ModBlocks.GINGER_PLANT.get(), RenderType.cutout());
            RenderTypeLookup.setRenderLayer(ModBlocks.FLAX_PLANT.get(), RenderType.cutout());
            RenderTypeLookup.setRenderLayer(ModBlocks.HANGING_FLAX.get(), RenderType.cutout());
            RenderTypeLookup.setRenderLayer(ModBlocks.PARADISE_DOOR.get(), RenderType.cutout());
        });
    }

    private static void registerDimensionRenderInfo(ResourceLocation key, DimensionRenderInfo info) {
        try {
            @SuppressWarnings("unchecked")
            Map<ResourceLocation, DimensionRenderInfo> effects =
                    ObfuscationReflectionHelper.getPrivateValue(DimensionRenderInfo.class, null, "field_239208_a_");

            if (effects == null) {
                LOGGER.error("Failed to access DimensionRenderInfo effects map when registering {}", key);
                return;
            }

            effects.put(key, info);
        } catch (RuntimeException exception) {
            LOGGER.error("Failed to register dimension render info for {}", key, exception);
        }
    }


    @EventBusSubscriber(modid = ExampleMod.MODID, bus = Bus.MOD)
    public static class ModLifecycleEvents {
        @SubscribeEvent
        public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
            event.put(ModEntityTypes.BEAVER.get(), BeaverEntity.createAttributes().build());
        }
    }
}
