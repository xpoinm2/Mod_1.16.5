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
public class RoughKnivesQuestScreen extends AbstractQuestScreen {

    public RoughKnivesQuestScreen(Screen parent) {
        super(parent, "Первые грубые ножи");
    }

    @Override
    protected int renderDescription(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                    int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth,
                "Первые ножи позволяют обрабатывать заготовки и кожу.", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth,
                "Каменный вариант проще, костяной служит дольше.", 0xFFFFFF00);
        return y;
    }

    @Override
    protected int renderGoals(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                              int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth,
                "Создать один каменный или костяной нож", 0xFFFFFF00);
        y += 6;
        ItemStack stoneKnife = new ItemStack(ModItems.ROUGH_STONE_KNIFE.get());
        if (GuiUtil.renderItemWithTooltip(this, ms, stoneKnife, x, y, mouseX, mouseY)) {
            hoveredStack = stoneKnife;
        }
        y += 22;
        ItemStack boneKnife = new ItemStack(ModItems.ROUGH_BONE_KNIFE.get());
        if (GuiUtil.renderItemWithTooltip(this, ms, boneKnife, x + 24, y - 22, mouseX, mouseY)) {
            hoveredStack = boneKnife;
        }
        return y;
    }

    @Override
    protected int renderInstructions(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                     int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth,
                "Соберите материалы после изучения каменных или костяных инструментов.", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth,
                "Скрафтите выбранный нож на рабочем месте или в инвентаре.", 0xFFFFFF00);
        return y;
    }

    @Override
    protected boolean hasRequiredItems() {
        if (this.minecraft.player == null) {
            return false;
        }
        return this.minecraft.player.inventory.countItem(ModItems.ROUGH_STONE_KNIFE.get()) > 0
                || this.minecraft.player.inventory.countItem(ModItems.ROUGH_BONE_KNIFE.get()) > 0;
    }

    @Override
    protected boolean isQuestUnlocked() {
        return QuestManager.isStoneToolsCompleted() || QuestManager.isBoneToolsCompleted();
    }

    @Override
    protected boolean isQuestCompleted() {
        return QuestManager.isRoughKnivesCompleted();
    }

    @Override
    protected void markCompleted() {
        QuestManager.setRoughKnivesCompleted(true);
    }
}