package com.example.examplemod.client.screen.quest;

import com.example.examplemod.ModItems;
import com.example.examplemod.client.GuiUtil;
import com.example.examplemod.client.screen.main.ScrollArea;
import com.example.examplemod.item.RoastedOreItem;
import com.example.examplemod.quest.QuestManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CalcinedTinOreQuestScreen extends AbstractQuestScreen {

    public CalcinedTinOreQuestScreen(Screen parent) {
        super(parent, "Обожжённая оловянная руда");
    }

    @Override
    protected int renderDescription(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                    int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth,
                "Олово любит сухое тепло, чтобы избавляться от грязи и влаги.", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth,
                "Горячая руда становится более плотной, пока не остынет и не станет обожжённой.", 0xFFFFFF00);
        return y;
    }

    @Override
    protected int renderGoals(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                              int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth,
                "Получить 16 горячей обожжённой оловянной руды и 16 обожжённой оловянной руды.", 0xFFFFFF00);
        y += 6;
        int iconX = x;
        ItemStack hotStack = new ItemStack(ModItems.CALCINED_TIN_ORE.get());
        RoastedOreItem.setState(hotStack, RoastedOreItem.STATE_HOT);
        if (GuiUtil.renderItemWithTooltip(this, ms, hotStack, iconX, y, mouseX, mouseY)) {
            hoveredStack = hotStack;
        }
        iconX += 22;
        ItemStack calcinedStack = new ItemStack(ModItems.CALCINED_TIN_ORE.get());
        if (GuiUtil.renderItemWithTooltip(this, ms, calcinedStack, iconX, y, mouseX, mouseY)) {
            hoveredStack = calcinedStack;
        }
        y += 22;
        return y;
    }

    @Override
    protected int renderInstructions(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                     int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth,
                "Размещайте очищённую гравийную оловянную руду на кострище, она сначала превратится в горячую обожжённую руду.", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth,
                "Горячая руда сильно обжигает, поэтому перекладывайте её щипцами, чтобы не получить ожог.", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth,
                "После остывания куски превращаются в обожжённую оловянную руду, готовую к переплавке.", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth,
                "Грязную оловянную руду тоже можно жарить на кострище, но металл выходит тоньше.", 0xFFFFFF00);
        return y;
    }

    @Override
    protected boolean hasRequiredItems() {
        return this.minecraft.player != null
                && this.minecraft.player.inventory.countItem(ModItems.CALCINED_TIN_ORE.get()) >= 16
                && this.minecraft.player.inventory.countItem(ModItems.CALCINED_TIN_ORE.get()) >= 16;
    }

    @Override
    protected boolean isQuestUnlocked() {
        return QuestManager.isCleanedGravelTinOreCompleted();
    }

    @Override
    protected boolean isQuestCompleted() {
        return QuestManager.isCalcinedTinOreCompleted();
    }

    @Override
    protected void markCompleted() {
        QuestManager.setCalcinedTinOreCompleted(true);
    }
}

