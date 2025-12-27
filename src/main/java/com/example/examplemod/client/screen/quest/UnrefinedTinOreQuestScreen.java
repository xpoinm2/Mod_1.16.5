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
public class UnrefinedTinOreQuestScreen extends AbstractQuestScreen {

    public UnrefinedTinOreQuestScreen(Screen parent) {
        super(parent, "Неочищенная оловянная руда");
    }

    @Override
    protected int renderDescription(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                    int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Глубоко под землёй лежат жилы оловянной руды.", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "Нужно для открытия - Пройти древний мир.", 0xFFFFFF00);
        return y;
    }

    @Override
    protected int renderGoals(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                              int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Добыть 16 неочищенной оловянной руды.", 0xFFFFFF00);
        y += 6;
        ItemStack stack = new ItemStack(ModItems.UNREFINED_TIN_ORE.get(), 16);
        if (GuiUtil.renderItemWithTooltip(this, ms, stack, x, y, mouseX, mouseY)) {
            hoveredStack = stack;
        }
        y += 22;
        return y;
    }

    @Override
    protected int renderInstructions(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                     int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Используйте кирку или молот, чтобы расщепить жилу.", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "Руда выпадает целыми блоками, храните запас сразу десятка штук.", 0xFFFFFF00);
        return y;
    }

    @Override
    protected boolean hasRequiredItems() {
        return this.minecraft.player != null
                && this.minecraft.player.inventory.countItem(ModItems.UNREFINED_TIN_ORE.get()) >= 16;
    }

    @Override
    protected boolean isQuestCompleted() {
        return QuestManager.isUnrefinedTinOreCompleted();
    }

    @Override
    protected boolean isQuestUnlocked() {
        return QuestManager.isAncientWorldCompleted();
    }

    @Override
    protected void markCompleted() {
        QuestManager.setUnrefinedTinOreCompleted(true);
    }
}

