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
public class ClayCupQuestScreen extends AbstractQuestScreen {

    public ClayCupQuestScreen(Screen parent) {
        super(parent, "Глиняная чашка");
    }

    @Override
    protected int renderDescription(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                    int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth,
                "Небольшая обожжённая чашка удобна для питья и приготовления.", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth,
                "Она выдерживает жар кострища и дополняет набор посуды.", 0xFFFFFF00);
        return y;
    }

    @Override
    protected int renderGoals(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                              int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Создать 1 глиняную чашку", 0xFFFFFF00);
        y += 6;
        ItemStack stack = new ItemStack(ModItems.CLAY_CUP.get());
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
                "Слепите сырую глиняную чашку.", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth,
                "Обожгите её на кострище, чтобы получить готовое изделие.", 0xFFFFFF00);
        return y;
    }

    @Override
    protected boolean hasRequiredItems() {
        return this.minecraft.player != null
                && this.minecraft.player.inventory.countItem(ModItems.CLAY_CUP.get()) > 0;
    }

    @Override
    protected boolean isQuestUnlocked() {
        return QuestManager.isFirepitCompleted();
    }

    @Override
    protected boolean isQuestCompleted() {
        return QuestManager.isClayCupCompleted();
    }

    @Override
    protected void markCompleted() {
        QuestManager.setClayCupCompleted(true);
    }
}