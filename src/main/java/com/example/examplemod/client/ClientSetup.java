package com.example.examplemod.client;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.ModEntities;
import com.example.examplemod.ModTileEntities;
import com.example.examplemod.client.render.ClayPotWaterRenderer;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import com.example.examplemod.client.render.ClayPotWaterRenderer;
import com.example.examplemod.client.render.BeaverRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientSetup {
    private ClientSetup() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.BEAVER.get(), BeaverRenderer::new);
        ClientRegistry.bindTileEntityRenderer(ModTileEntities.CLAY_POT.get(), ClayPotWaterRenderer::new);
    }
}