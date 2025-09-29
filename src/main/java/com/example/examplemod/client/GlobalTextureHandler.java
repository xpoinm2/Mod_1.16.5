package com.example.examplemod.client;

import com.example.examplemod.ExampleMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class GlobalTextureHandler {
    @SubscribeEvent
    public static void onModelBake(ModelBakeEvent event) {
        // Previously this handler replaced every baked block model with the burnt brushwood variant.
        // That behaviour caused non-cube blocks (such as berry bushes) to lose their textures and
        // appear as the missing texture placeholder. The handler is intentionally left empty so the
        // vanilla and modded models remain untouched during baking.
    }
}