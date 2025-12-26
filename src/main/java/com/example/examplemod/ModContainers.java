package com.example.examplemod;

import com.example.examplemod.container.BoneTongsContainer;
import com.example.examplemod.container.ClayPotContainer;
import com.example.examplemod.container.FirepitContainer;
import com.example.examplemod.container.SlabContainer;
import com.example.examplemod.util.RegistryHelper;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;

public class ModContainers {

    public static final RegistryObject<ContainerType<FirepitContainer>> FIREPIT =
            ModRegistries.CONTAINERS.register("firepit",
                    () -> IForgeContainerType.create((windowId, playerInventory, data) -> new FirepitContainer(windowId, playerInventory, data)));

    public static final RegistryObject<ContainerType<ClayPotContainer>> CLAY_POT =
            ModRegistries.CONTAINERS.register("clay_pot",
                    () -> IForgeContainerType.create((windowId, playerInventory, data) -> new ClayPotContainer(windowId, playerInventory, data)));

    public static final RegistryObject<ContainerType<BoneTongsContainer>> BONE_TONGS =
            ModRegistries.CONTAINERS.register("bone_tongs",
                    () -> IForgeContainerType.create((windowId, playerInventory, data) -> new BoneTongsContainer(windowId, playerInventory, data)));

    public static final RegistryObject<ContainerType<SlabContainer>> SLAB =
            ModRegistries.CONTAINERS.register("slab",
                    () -> IForgeContainerType.create((windowId, playerInventory, data) -> new SlabContainer(windowId, playerInventory, data)));
}