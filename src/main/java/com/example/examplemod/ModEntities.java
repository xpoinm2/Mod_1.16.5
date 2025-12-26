package com.example.examplemod;

import com.example.examplemod.entity.BeaverEntity;
import com.example.examplemod.util.RegistryHelper;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;

public final class ModEntities {
    private ModEntities() {
    }

    // Определяем сущность без регистрации
    public static final RegistryObject<EntityType<BeaverEntity>> BEAVER =
            ModRegistries.ENTITIES.register("beaver", () -> EntityType.Builder
                    .of(BeaverEntity::new, EntityClassification.CREATURE)
                    .sized(0.9F, 0.7F)
                    .build(ExampleMod.MODID + ":beaver"));
}