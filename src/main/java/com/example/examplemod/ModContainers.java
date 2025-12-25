package com.example.examplemod;

import com.example.examplemod.container.BoneTongsContainer;
import com.example.examplemod.container.ClayPotContainer;
import com.example.examplemod.container.FirepitContainer;
import com.example.examplemod.container.SlabContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModContainers {
    public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, ExampleMod.MODID);

    public static final RegistryObject<ContainerType<FirepitContainer>> FIREPIT = CONTAINERS.register("firepit",
            () -> IForgeContainerType.create((windowId, playerInventory, data) -> new FirepitContainer(windowId, playerInventory, data)));

    public static final RegistryObject<ContainerType<ClayPotContainer>> CLAY_POT = CONTAINERS.register("clay_pot",
            () -> IForgeContainerType.create((windowId, playerInventory, data) -> new ClayPotContainer(windowId, playerInventory, data)));

    public static final RegistryObject<ContainerType<BoneTongsContainer>> BONE_TONGS = CONTAINERS.register("bone_tongs",
            () -> IForgeContainerType.create((windowId, playerInventory, data) -> new BoneTongsContainer(windowId, playerInventory, data)));

    public static final RegistryObject<ContainerType<SlabContainer>> SLAB = CONTAINERS.register("slab",
            () -> IForgeContainerType.create((windowId, playerInventory, data) -> new SlabContainer(windowId, playerInventory, data)));

    public static void register(IEventBus bus) {
        CONTAINERS.register(bus);
    }
}