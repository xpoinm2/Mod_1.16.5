package com.example.examplemod.util;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.RegistryObject;
import com.example.examplemod.ExampleMod;
import com.example.examplemod.ModCreativeTabs;

import java.util.function.Supplier;

/**
 * Утилиты для автоматической регистрации блоков и предметов
 */
public class RegistryHelper {

    /**
     * Автоматическая регистрация блока с соответствующим предметом
     */
    public static <T extends Block> BlockRegistryEntry<T> registerBlock(String name, Supplier<T> blockSupplier,
                                                                       DeferredRegister<Block> blockRegistry,
                                                                       DeferredRegister<Item> itemRegistry) {
        RegistryObject<T> blockEntry = blockRegistry.register(name, blockSupplier);
        RegistryObject<BlockItem> itemEntry = itemRegistry.register(name, () -> new BlockItem(blockEntry.get(), new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));
        return new BlockRegistryEntry<>(blockEntry, itemEntry);
    }

    /**
     * Автоматическая регистрация предмета
     */
    public static <T extends Item> RegistryObject<T> registerItem(String name, Supplier<T> itemSupplier,
                                                                  DeferredRegister<Item> itemRegistry) {
        return itemRegistry.register(name, itemSupplier);
    }

    /**
     * Автоматическая регистрация сущности
     */
    public static <T extends Entity> RegistryObject<EntityType<T>> registerEntity(String name,
                                                                                   EntityType.Builder<T> builder,
                                                                                   DeferredRegister<EntityType<?>> entityRegistry) {
        return entityRegistry.register(name, () -> builder.build(new ResourceLocation(ExampleMod.MODID, name).toString()));
    }

    /**
     * Автоматическая регистрация контейнера
     */
    public static <T extends Container> RegistryObject<ContainerType<T>> registerContainer(String name,
                                                                                            Supplier<ContainerType<T>> containerSupplier,
                                                                                            DeferredRegister<ContainerType<?>> containerRegistry) {
        return containerRegistry.register(name, containerSupplier);
    }

    /**
     * Автоматическая регистрация тайл энтити
     */
    public static <T extends TileEntity> RegistryObject<TileEntityType<T>> registerTileEntity(String name,
                                                                                               Supplier<TileEntityType<T>> tileEntitySupplier,
                                                                                               DeferredRegister<TileEntityType<?>> tileEntityRegistry) {
        return tileEntityRegistry.register(name, tileEntitySupplier);
    }

    /**
     * Автоматическая регистрация звука
     */
    public static RegistryObject<SoundEvent> registerSound(String name,
                                                           DeferredRegister<SoundEvent> soundRegistry) {
        return soundRegistry.register(name, () -> new SoundEvent(new ResourceLocation(ExampleMod.MODID, name)));
    }

    /**
     * Регистрация всех отложенных регистров
     */
    public static void registerAll(IEventBus modBus, DeferredRegister<?>... registries) {
        for (DeferredRegister<?> registry : registries) {
            registry.register(modBus);
        }
    }

    /**
     * Класс для хранения пары блок-предмет
     */
    public static class BlockRegistryEntry<T extends Block> {
        private final RegistryObject<T> block;
        private final RegistryObject<BlockItem> item;

        public BlockRegistryEntry(RegistryObject<T> block, RegistryObject<BlockItem> item) {
            this.block = block;
            this.item = item;
        }

        public RegistryObject<T> getBlock() {
            return block;
        }

        public RegistryObject<BlockItem> getItem() {
            return item;
        }

        public T getBlockInstance() {
            return block.get();
        }

        public BlockItem getItemInstance() {
            return item.get();
        }
    }
}
