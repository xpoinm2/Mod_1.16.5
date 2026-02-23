package com.example.examplemod.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

public class UraganBlock extends Block {
    public UraganBlock() {
        super(AbstractBlock.Properties.copy(Blocks.DIAMOND_BLOCK));
    }
}
