package com.example.examplemod.client;

import com.example.examplemod.ExampleMod;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

/**
 * Replaces all block models with the burnt brushwood texture,
 * effectively turning every block into a campfire ground look.
 */
@Mod.EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class GlobalTextureHandler {
    @SubscribeEvent
    public static void onModelBake(ModelBakeEvent event) {
        Map<ResourceLocation, IBakedModel> registry = event.getModelRegistry();
        // Baked model of the burnt brushwood block
        ModelResourceLocation burntLoc = new ModelResourceLocation(
                new ResourceLocation(ExampleMod.MODID, "brushwood_block_burnt"), "");
        IBakedModel burnt = registry.get(burntLoc);
        if (burnt == null) {
            return;
        }

        for (Map.Entry<ResourceLocation, IBakedModel> entry : registry.entrySet()) {
            ResourceLocation key = entry.getKey();
            if (key instanceof ModelResourceLocation) {
                ModelResourceLocation mrl = (ModelResourceLocation) key;
                // Skip item representations
                if (!"inventory".equals(mrl.getVariant())) {
                    registry.put(mrl, burnt);
                }
            }
        }
    }
}