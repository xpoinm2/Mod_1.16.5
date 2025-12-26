package com.example.examplemod;

import com.example.examplemod.entity.BeaverEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.common.ForgeSpawnEggItem;

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
        // Регистрируем spawn egg после всех регистраций
        event.enqueueWork(() -> {
            // Регистрируем spawn egg для бобра
            ForgeRegistries.ITEMS.register(new ForgeSpawnEggItem(
                    ModEntities.BEAVER, 0x6B4C2E, 0x3B2A1A,
                    new net.minecraft.item.Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB))
                    .setRegistryName(ExampleMod.MODID, "beaver_spawn_egg"));
        });
    }
}