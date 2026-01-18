package com.example.examplemod;

import com.example.examplemod.container.BoneTongsContainer;
import com.example.examplemod.container.ClayPotContainer;
import com.example.examplemod.container.EnhancedDualContainer;
import com.example.examplemod.container.FirepitContainer;
import com.example.examplemod.container.PechugaContainer;
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

    public static final RegistryObject<ContainerType<PechugaContainer>> PECHUGA =
            ModRegistries.CONTAINERS.register("pechuga",
                    () -> IForgeContainerType.create((windowId, playerInventory, data) -> new PechugaContainer(windowId, playerInventory, data)));

    public static final RegistryObject<ContainerType<EnhancedDualContainer>> ENHANCED_DUAL_CONTAINER =
            ModRegistries.CONTAINERS.register("enhanced_dual_container",
                    () -> IForgeContainerType.create((windowId, playerInventory, data) -> new EnhancedDualContainer(windowId, playerInventory, data)));

    /**
     * Форсирует загрузку класса (и, как следствие, добавление записей в DeferredRegister)
     * на стадии инициализации мода.
     */
    public static void init() {
        // no-op
    }
}