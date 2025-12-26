package com.example.examplemod;

import com.example.examplemod.tileentity.ClayPotTileEntity;
import com.example.examplemod.tileentity.FirepitTileEntity;
import com.example.examplemod.tileentity.SlabTileEntity;
import com.example.examplemod.util.RegistryHelper;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;

public class ModTileEntities {

    public static final RegistryObject<TileEntityType<FirepitTileEntity>> FIREPIT =
            ModRegistries.TILE_ENTITIES.register("firepit",
                    () -> TileEntityType.Builder.of(FirepitTileEntity::new, ModBlocks.FIREPIT_BLOCK.get()).build(null));

    public static final RegistryObject<TileEntityType<ClayPotTileEntity>> CLAY_POT =
            ModRegistries.TILE_ENTITIES.register("clay_pot",
                    () -> TileEntityType.Builder.of(ClayPotTileEntity::new, ModBlocks.CLAY_POT.get()).build(null));

    public static final RegistryObject<TileEntityType<SlabTileEntity>> SLAB =
            ModRegistries.TILE_ENTITIES.register("slab",
                    () -> TileEntityType.Builder.of(SlabTileEntity::new,
                            ModBlocks.BRUSHWOOD_SLAB.get(), 
                            ModBlocks.BURNED_BRUSHWOOD_SLAB.get(),
                            ModBlocks.OAK_SLAB.get(),
                            ModBlocks.BIRCH_SLAB.get(),
                            ModBlocks.SPRUCE_SLAB.get(),
                            ModBlocks.JUNGLE_SLAB.get(),
                            ModBlocks.ACACIA_SLAB.get(),
                            ModBlocks.DARK_OAK_SLAB.get(),
                            ModBlocks.CRIMSON_SLAB.get(),
                            ModBlocks.WARPED_SLAB.get(),
                            ModBlocks.STONE_SLAB.get()).build(null));

    /**
     * Форсирует загрузку класса (и, как следствие, добавление записей в DeferredRegister)
     * на стадии инициализации мода.
     */
    public static void init() {
        // no-op
    }
}