package com.example.examplemod;

import com.example.examplemod.entity.BeaverEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.RegistryObject;

public final class ModEntities {
    private ModEntities() {
    }

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITIES, ExampleMod.MODID);

    public static final RegistryObject<EntityType<BeaverEntity>> BEAVER =
            ENTITIES.register("beaver", () -> EntityType.Builder
                    .of(BeaverEntity::new, EntityClassification.CREATURE)
                    .sized(0.9F, 0.7F)
                    .build(ExampleMod.MODID + ":beaver"));
}