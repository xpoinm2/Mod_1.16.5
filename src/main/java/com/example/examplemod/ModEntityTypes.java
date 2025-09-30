package com.example.examplemod;

import net.minecraft.entity.EntityType;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEntityTypes {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITIES, ExampleMod.MODID);

    public static void register(IEventBus bus) {
        ENTITY_TYPES.register(bus);
    }
}