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
public class CombsQuestScreen extends AbstractQuestScreen {

    public CombsQuestScreen(Screen parent) {
        super(parent, "Гребни");
    }

    @Override
    protected int renderDescription(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                    int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Необходимая вещь в", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "обращении с волокнами", 0xFFFFFF00);
        return y;
    }

    @Override
    protected int renderGoals(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                              int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Сделать один гребень", 0xFFFFFF00);
        y += 6;
        ItemStack[] stacks = new ItemStack[]{
                new ItemStack(ModItems.WOODEN_COMB.get()),
                new ItemStack(ModItems.BONE_COMB.get())
        };
        int itemX = x;
        for (ItemStack stack : stacks) {
            if (GuiUtil.renderItemWithTooltip(this, ms, stack, itemX, y, mouseX, mouseY)) {
                hoveredStack = stack;
            }
            itemX += 20;
        }
        y += 22;
        return y;
    }

    @Override
    protected int renderInstructions(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                     int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Делаем любой гребень", 0xFFFFFF00);
        return y;
    }

    @Override
    protected boolean hasRequiredItems() {
        return this.minecraft.player != null && (
                this.minecraft.player.inventory.countItem(ModItems.WOODEN_COMB.get()) >= 1
                        || this.minecraft.player.inventory.countItem(ModItems.BONE_COMB.get()) >= 1);
    }

    @Override
    protected boolean isQuestUnlocked() {
        return QuestManager.isBigBonesCompleted()
                && QuestManager.isHewnStonesCompleted()
                && QuestManager.isBranchCompleted();
    }

    @Override
    protected boolean isQuestCompleted() {
        return QuestManager.isCombsCompleted();
    }

    @Override
    protected void markCompleted() {
        QuestManager.setCombsCompleted(true);
    }
}
