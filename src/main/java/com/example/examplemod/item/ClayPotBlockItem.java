package com.example.examplemod.item;

import com.example.examplemod.tileentity.ClayPotTileEntity;
import com.example.examplemod.util.FluidTextUtil;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ClayPotBlockItem extends BlockItem {
    public ClayPotBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        ITextComponent capacity = FluidTextUtil.formatAmount(ClayPotTileEntity.CAPACITY);
        tooltip.add(new TranslationTextComponent("tooltip.examplemod.clay_pot.capacity", capacity).withStyle(TextFormatting.BLUE));
    }
}