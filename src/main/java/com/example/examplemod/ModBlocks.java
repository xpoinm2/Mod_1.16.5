package com.example.examplemod;

import com.example.examplemod.block.PebbleBlock;
import net.minecraft.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ExampleMod.MODID);

    public static final RegistryObject<Block> PEBBLE_BLOCK = BLOCKS.register("pebble_block", PebbleBlock::new);

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}