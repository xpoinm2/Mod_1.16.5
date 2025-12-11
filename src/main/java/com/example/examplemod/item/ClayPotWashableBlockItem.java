package com.example.examplemod.item;

import com.example.examplemod.block.ClayPotBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;

public class ClayPotWashableBlockItem extends BlockItem {
    public ClayPotWashableBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        if (player != null && ClayPotBlock.tryWashOre(
                context.getLevel(),
                context.getClickedPos(),
                player,
                context.getItemInHand()
        )) {
            return ActionResultType.sidedSuccess(context.getLevel().isClientSide());
        }
        return super.useOn(context);
    }
}
