package com.example.examplemod;

import com.example.examplemod.fluid.DirtyWaterFluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.RegistryObject;

public class ModFluids {
    public static final DeferredRegister<net.minecraft.fluid.Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, ExampleMod.MODID);

    public static final RegistryObject<net.minecraft.fluid.Fluid> DIRTY_WATER = FLUIDS.register("dirty_water",
            DirtyWaterFluid.Source::new);

    public static final RegistryObject<net.minecraft.fluid.Fluid> DIRTY_WATER_FLOWING = FLUIDS.register("dirty_water_flowing",
            DirtyWaterFluid.Flowing::new);

    public static void register(IEventBus bus) {
        FLUIDS.register(bus);
    }
}
