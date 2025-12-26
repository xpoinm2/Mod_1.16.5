package com.example.examplemod;

import com.example.examplemod.tileentity.ClayPotTileEntity;
import com.example.examplemod.tileentity.FirepitTileEntity;
import com.example.examplemod.tileentity.SlabTileEntity;
import com.example.examplemod.util.RegistryHelper;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;

public class ModTileEntities {

    public static final RegistryObject<TileEntityType<FirepitTileEntity>> FIREPIT =
            RegistryHelper.registerTileEntity("firepit",
                    () -> TileEntityType.Builder.of(FirepitTileEntity::new, ModBlocks.FIREPIT_BLOCK.get()).build(null),
                    ModRegistries.TILE_ENTITIES);

    public static final RegistryObject<TileEntityType<ClayPotTileEntity>> CLAY_POT =
            RegistryHelper.registerTileEntity("clay_pot",
                    () -> TileEntityType.Builder.of(ClayPotTileEntity::new, ModBlocks.CLAY_POT.get()).build(null),
                    ModRegistries.TILE_ENTITIES);

    public static final RegistryObject<TileEntityType<SlabTileEntity>> SLAB =
            RegistryHelper.registerTileEntity("slab",
                    () -> TileEntityType.Builder.of(SlabTileEntity::new,
                            ModBlocks.BRUSHWOOD_SLAB.get(), ModBlocks.BURNED_BRUSHWOOD_SLAB.get()).build(null),
                    ModRegistries.TILE_ENTITIES);
}