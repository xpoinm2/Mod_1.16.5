package com.example.examplemod;

import com.example.examplemod.entity.BeaverEntity;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEntityTypes {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITIES, ExampleMod.MODID);

    public static final RegistryObject<EntityType<BeaverEntity>> BEAVER = ENTITY_TYPES.register("beaver",
            () -> EntityType.Builder.of(BeaverEntity::new, EntityClassification.CREATURE)
                    .sized(0.9F, 0.7F)
                    .build(new ResourceLocation(ExampleMod.MODID, "beaver").toString()));

    public static void register(IEventBus bus) {
        ENTITY_TYPES.register(bus);
    }
}