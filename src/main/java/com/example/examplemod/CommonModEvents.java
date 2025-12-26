package com.example.examplemod;

import com.example.examplemod.entity.BeaverEntity;
import com.example.examplemod.world.WorldGenRegistry;
import com.example.examplemod.world.ModBiomes;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class CommonModEvents {
    private CommonModEvents() {
    }

    @SubscribeEvent
    public static void onAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.BEAVER.get(), BeaverEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        // Временно убрали регистрацию генерации мира для диагностики
        event.enqueueWork(() -> {
            // WorldGenRegistry.register();
            // ModBiomes.setupBiomes();
        });
    }
}