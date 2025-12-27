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
public class ScrapedLeatherQuestScreen extends AbstractQuestScreen {

    public ScrapedLeatherQuestScreen(Screen parent) {
        super(parent, "Выскобленная кожа");
    }

    @Override
    protected int renderDescription(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                    int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth,
                "Обработанная кожа нужна для дальнейшего ремесла.", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth,
                "Её получают соскабливанием сыромятной шкуры.", 0xFFFFFF00);
        return y;
    }

    @Override
    protected int renderGoals(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                              int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Создать 1 выскобленную кожу", 0xFFFFFF00);
        y += 6;
        ItemStack stack = new ItemStack(ModItems.SCRAPED_HIDE.get());
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
                "Используйте грубый нож, чтобы снять остатки мяса и жира.", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth,
                "После обработки получите выскобленную кожу.", 0xFFFFFF00);
        return y;
    }

    @Override
    protected boolean hasRequiredItems() {
        return this.minecraft.player != null
                && this.minecraft.player.inventory.countItem(ModItems.SCRAPED_HIDE.get()) > 0;
    }

    @Override
    protected boolean isQuestUnlocked() {
        return QuestManager.isRoughKnivesCompleted();
    }

    @Override
    protected boolean isQuestCompleted() {
        return QuestManager.isScrapedLeatherCompleted();
    }

    @Override
    protected void markCompleted() {
        QuestManager.setScrapedLeatherCompleted(true);
    }
}