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
            RegistryHelper.registerContainer("firepit",
                    () -> IForgeContainerType.create((windowId, playerInventory, data) -> new FirepitContainer(windowId, playerInventory, data)),
                    ModRegistries.CONTAINERS);

    public static final RegistryObject<ContainerType<ClayPotContainer>> CLAY_POT =
            RegistryHelper.registerContainer("clay_pot",
                    () -> IForgeContainerType.create((windowId, playerInventory, data) -> new ClayPotContainer(windowId, playerInventory, data)),
                    ModRegistries.CONTAINERS);

    public static final RegistryObject<ContainerType<BoneTongsContainer>> BONE_TONGS =
            RegistryHelper.registerContainer("bone_tongs",
                    () -> IForgeContainerType.create((windowId, playerInventory, data) -> new BoneTongsContainer(windowId, playerInventory, data)),
                    ModRegistries.CONTAINERS);

    public static final RegistryObject<ContainerType<SlabContainer>> SLAB =
            RegistryHelper.registerContainer("slab",
                    () -> IForgeContainerType.create((windowId, playerInventory, data) -> new SlabContainer(windowId, playerInventory, data)),
                    ModRegistries.CONTAINERS);
}