package com.example.examplemod;

import com.example.examplemod.entity.BeaverEntity;
import com.example.examplemod.util.RegistryHelper;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;

public final class ModEntities {
    private ModEntities() {
    }

    // Используем унифицированный регистратор
    public static final RegistryObject<EntityType<BeaverEntity>> BEAVER =
            RegistryHelper.registerEntity("beaver",
                    EntityType.Builder.of(BeaverEntity::new, EntityClassification.CREATURE)
                            .sized(0.9F, 0.7F),
                    ModRegistries.ENTITIES);
}