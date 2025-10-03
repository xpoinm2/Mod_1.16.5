package com.example.examplemod.client.screen;

import com.example.examplemod.client.GuiUtil;
import com.example.examplemod.quest.QuestManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PlanksQuestScreen extends AbstractQuestScreen {

    public PlanksQuestScreen(Screen parent) {
        super(parent, "Доски");
    }

    @Override
    protected int renderDescription(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                    int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Люди работали топорами, чтобы", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "разделывать бревна.", 0xFFFFFF00);
        return y;
    }

    @Override
    protected int renderGoals(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                              int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Нужно получить 4 доски", 0xFFFFFF00);
        int itemsPerRow = Math.max(1, innerWidth / 20);
        int index = 0;
        for (Item plank : ItemTags.PLANKS.getValues()) {
            ItemStack stack = new ItemStack(plank, 4);
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
        return drawParagraph(ms, x, y, innerWidth, "Крафт досок через топор", 0xFFFFFF00);
    }

    @Override
    protected boolean hasRequiredItems() {
        if (this.minecraft.player == null) {
            return false;
        }
        int count = 0;
        for (ItemStack stack : this.minecraft.player.inventory.items) {
            if (stack.getItem().is(ItemTags.PLANKS)) {
                count += stack.getCount();
                if (count >= 4) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected boolean isQuestCompleted() {
        return QuestManager.isPlanksCompleted();
    }

    @Override
    protected void markCompleted() {
        QuestManager.setPlanksCompleted(true);
    }
}
