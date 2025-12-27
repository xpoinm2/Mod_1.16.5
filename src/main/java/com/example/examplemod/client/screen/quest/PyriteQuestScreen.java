package com.example.examplemod.client.screen.quest;

import com.example.examplemod.ModItems;
import com.example.examplemod.client.GuiUtil;
import com.example.examplemod.client.screen.main.ScrollArea;
import com.example.examplemod.quest.QuestManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PyriteQuestScreen extends AbstractQuestScreen {

    public PyriteQuestScreen(Screen parent) {
        super(parent, "Пирит");
    }

    @Override
    protected int renderDescription(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                    int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth,
                "Пирит встречается в гравийных жилах.", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth,
                "Его можно найти в шахтах и на поверхности в осыпях.", 0xFFFFFF00);
        return y;
    }

    @Override
    protected int renderGoals(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                              int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Добыть 1 кусочек пирита", 0xFFFFFF00);
        y += 6;
        ItemStack stack = new ItemStack(ModItems.PYRITE_PIECE.get());
        if (GuiUtil.renderItemWithTooltip(this, ms, stack, x, y, mouseX, mouseY)) {
            hoveredStack = stack;
        }
        y += 22;
        return y;
    }

    @Override
    protected int renderInstructions(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                     int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth,
                "Используйте кирку, чтобы добыть пирит.", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth,
                "Он почти всегда залегает рядом с гравием.", 0xFFFFFF00);
        return y;
    }

    @Override
    protected boolean hasRequiredItems() {
        return this.minecraft.player != null
                && this.minecraft.player.inventory.countItem(ModItems.PYRITE_PIECE.get()) > 0;
    }

    @Override
    protected boolean isQuestUnlocked() {
        return QuestManager.isStoneToolsCompleted() || QuestManager.isBoneToolsCompleted();
    }

    @Override
    protected boolean isQuestCompleted() {
        return QuestManager.isPyriteCompleted();
    }

    @Override
    protected void markCompleted() {
        QuestManager.setPyriteCompleted(true);
    }
}