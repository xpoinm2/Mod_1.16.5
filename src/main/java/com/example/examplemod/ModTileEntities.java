package com.example.examplemod;

import com.example.examplemod.tileentity.ClayPotTileEntity;
import com.example.examplemod.tileentity.FirepitTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTileEntities {
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES =
            DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, ExampleMod.MODID);

    public static final RegistryObject<TileEntityType<FirepitTileEntity>> FIREPIT = TILE_ENTITIES.register(
            "firepit",
            () -> TileEntityType.Builder.of(FirepitTileEntity::new, ModBlocks.FIREPIT_BLOCK.get()).build(null));

    public static final RegistryObject<TileEntityType<ClayPotTileEntity>> CLAY_POT = TILE_ENTITIES.register(
            "clay_pot",
            () -> TileEntityType.Builder.of(ClayPotTileEntity::new, ModBlocks.CLAY_POT.get()).build(null));

    public static void register(IEventBus bus) {
        TILE_ENTITIES.register(bus);
    }
}