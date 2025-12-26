package com.example.examplemod;

import com.example.examplemod.util.RegistryHelper;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;

public final class ModEntities {
    private ModEntities() {
    }

    /**
     * Форсирует загрузку класса на стадии инициализации мода.
     * (Сущности сейчас могут быть пустыми, но метод нужен для единого паттерна init()).
     */
    public static void init() {
        // no-op
    }
}