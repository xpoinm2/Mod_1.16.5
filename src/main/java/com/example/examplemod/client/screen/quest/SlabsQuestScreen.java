package com.example.examplemod.client.screen.quest;

import com.example.examplemod.client.GuiUtil;
import com.example.examplemod.client.screen.main.ScrollArea;
import com.example.examplemod.quest.QuestManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SlabsQuestScreen extends AbstractQuestScreen {

    public SlabsQuestScreen(Screen parent) {
        super(parent, "Плиты");
    }

    @Override
    protected int renderDescription(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                    int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Плиты бывают", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "деревянные и каменные", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "Для задания нужны все деревянные", 0xFFFFFF00);
        return y;
    }

    @Override
    protected int renderGoals(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                              int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Получить 6 плит", 0xFFFFFF00);
        int itemsPerRow = Math.max(1, innerWidth / 20);
        int index = 0;
        for (Item slab : ItemTags.WOODEN_SLABS.getValues()) {
            ItemStack stack = new ItemStack(slab, 6);
            int itemX = x + (index % itemsPerRow) * 20;
            int itemY = y + (index / itemsPerRow) * 22;
            if (GuiUtil.renderItemWithTooltip(this, ms, stack, itemX, itemY, mouseX, mouseY)) {
                hoveredStack = stack;
            }
            index++;
        }
        if (index > 0) {
            int rows = (index + itemsPerRow - 1) / itemsPerRow;
            y += rows * 22;
        }
        return y + 4;
    }

    @Override
    protected int renderInstructions(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                     int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Крафт плит на верстаке", 0xFFFFFF00);
        return drawParagraph(ms, x, y, innerWidth, "Выбирайте любые деревянные варианты", 0xFFFFFF00);
    }

    @Override
    protected boolean hasRequiredItems() {
        if (this.minecraft.player == null) {
            return false;
        }
        int count = 0;
        for (ItemStack stack : this.minecraft.player.inventory.items) {
            if (stack.getItem().is(ItemTags.WOODEN_SLABS)) {
                count += stack.getCount();
                if (count >= 6) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected boolean isQuestCompleted() {
        return QuestManager.isSlabsCompleted();
    }

    @Override
    protected void markCompleted() {
        QuestManager.setSlabsCompleted(true);
    }
}
