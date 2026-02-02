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
public class RawBlanksQuestScreen extends AbstractQuestScreen {

    public RawBlanksQuestScreen(Screen parent) {
        super(parent, "Сырые заготовки");
    }

    @Override
    protected int renderDescription(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                    int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth,
                "Горячие куски металла можно вытянуть в сырые заготовки.", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth,
                "Это зачаток будущего слитка и основа для ковки.", 0xFFFFFF00);
        return y;
    }

    @Override
    protected int renderGoals(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                              int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth,
                "Скрафтить 8 сырых заготовок олова, золота или железа", 0xFFFFFF00);
        y += 6;
        int iconX = x;
        ItemStack ironStack = new ItemStack(ModItems.RAW_IRON_BLANK.get());
        if (GuiUtil.renderItemWithTooltip(this, ms, ironStack, iconX, y, mouseX, mouseY)) {
            hoveredStack = ironStack;
        }
        iconX += 22;
        ItemStack tinStack = new ItemStack(ModItems.RAW_TIN_BLANK.get());
        if (GuiUtil.renderItemWithTooltip(this, ms, tinStack, iconX, y, mouseX, mouseY)) {
            hoveredStack = tinStack;
        }
        iconX += 22;
        ItemStack goldStack = new ItemStack(ModItems.RAW_GOLD_BLANK.get());
        if (GuiUtil.renderItemWithTooltip(this, ms, goldStack, iconX, y, mouseX, mouseY)) {
            hoveredStack = goldStack;
        }
        y += 22;
        return y;
    }

    @Override
    protected int renderInstructions(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                     int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth,
                "Нагрейте куски металла в кострище или кирпичной печи,", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth,
                "пока они станут горячими. Берите щипцами — горячо!", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth,
                "Положите горячий кусок в левый слот булыжниковой", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth,
                "наковальни, молот — в инструментальный слот.", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth,
                "Нажимайте кнопку молота, пока не получите заготовку.", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth,
                "Сделайте 8 заготовок одного типа.", 0xFFFFFF00);
        return y;
    }

    @Override
    protected boolean hasRequiredItems() {
        if (this.minecraft == null || this.minecraft.player == null) {
            return false;
        }
        return this.minecraft.player.inventory.countItem(ModItems.RAW_IRON_BLANK.get()) >= 8
                || this.minecraft.player.inventory.countItem(ModItems.RAW_TIN_BLANK.get()) >= 8
                || this.minecraft.player.inventory.countItem(ModItems.RAW_GOLD_BLANK.get()) >= 8;
    }

    @Override
    protected boolean isQuestUnlocked() {
        return QuestManager.isMetalChunksCompleted();
    }

    @Override
    protected boolean isQuestCompleted() {
        return QuestManager.isRawBlanksCompleted();
    }

    @Override
    protected void markCompleted() {
        QuestManager.setRawBlanksCompleted(true);
    }
}
