package com.example.examplemod;

import com.example.examplemod.util.RegistryHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Унифицированная система регистрации для всех компонентов мода
 */
public class ModRegistries {

    // Регистраторы
    public static final DeferredRegister<net.minecraft.item.Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ExampleMod.MODID);

    public static final DeferredRegister<net.minecraft.block.Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, ExampleMod.MODID);

    public static final DeferredRegister<net.minecraft.entity.EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITIES, ExampleMod.MODID);

    public static final DeferredRegister<net.minecraft.inventory.container.ContainerType<?>> CONTAINERS =
            DeferredRegister.create(ForgeRegistries.CONTAINERS, ExampleMod.MODID);

    public static final DeferredRegister<net.minecraft.tileentity.TileEntityType<?>> TILE_ENTITIES =
            DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, ExampleMod.MODID);

    public static final DeferredRegister<net.minecraft.util.SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ExampleMod.MODID);

    /**
     * Регистрирует все регистраторы в mod event bus
     */
    public static void register(IEventBus modBus) {
        RegistryHelper.registerAll(modBus,
                ITEMS, BLOCKS, ENTITIES, CONTAINERS, TILE_ENTITIES, SOUND_EVENTS);
    }
}
