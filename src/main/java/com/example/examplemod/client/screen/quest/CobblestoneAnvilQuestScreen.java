package com.example.examplemod.client.screen.quest;

import com.example.examplemod.ModBlocks;
import com.example.examplemod.client.GuiUtil;
import com.example.examplemod.client.screen.main.ScrollArea;
import com.example.examplemod.quest.QuestManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CobblestoneAnvilQuestScreen extends AbstractQuestScreen {

    public CobblestoneAnvilQuestScreen(Screen parent) {
        super(parent, "Булыжниковая наковальня");
    }

    @Override
    protected int renderDescription(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                    int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Прочная основа для", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "ковки металлов", 0xFFFFFF00);
        return y;
    }

    @Override
    protected int renderGoals(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                              int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Создать булыжниковую наковальню", 0xFFFFFF00);
        y += 6;
        ItemStack stack = new ItemStack(ModBlocks.COBBLESTONE_ANVIL.get());
        if (GuiUtil.renderItemWithTooltip(this, ms, stack, x, y, mouseX, mouseY)) {
            hoveredStack = stack;
        }
        y += 22;
        return y;
    }

    @Override
    protected int renderInstructions(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                     int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "ПКМ молотом по блоку", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "булыжника, чтобы открыть", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "диалог. Выберите", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "'Наковальня'", 0xFFFFFF00);
        return y;
    }

    @Override
    protected boolean hasRequiredItems() {
        // Для этого квеста проверка не нужна - выполняется при нажатии кнопки
        return true;
    }

    @Override
    protected boolean isQuestUnlocked() {
        return QuestManager.isAncientWorldCompleted();
    }

    @Override
    protected boolean isQuestCompleted() {
        return QuestManager.isCobblestoneAnvilCompleted();
    }

    @Override
    protected void markCompleted() {
        QuestManager.setCobblestoneAnvilCompleted(true);
    }
}