package com.example.examplemod.client.screen;

import com.example.examplemod.ModItems;
import com.example.examplemod.client.GuiUtil;
import com.example.examplemod.quest.QuestManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class UnrefinedGoldOreQuestScreen extends AbstractQuestScreen {

    public UnrefinedGoldOreQuestScreen(Screen parent) {
        super(parent, "Неочищенная золотая руда");
    }

    @Override
    protected int renderDescription(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                    int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Старые жилища золота ещё не выработаны.", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "Нужно для открытия - Пройти древний мир.", 0xFFFFFF00);
        return y;
    }

    @Override
    protected int renderGoals(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                              int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Собрать 16 неочищенной золотой руды.", 0xFFFFFF00);
        y += 6;
        ItemStack stack = new ItemStack(ModItems.UNREFINED_GOLD_ORE.get(), 16);
        if (GuiUtil.renderItemWithTooltip(this, ms, stack, x, y, mouseX, mouseY)) {
            hoveredStack = stack;
        }
        y += 22;
        return y;
    }

    @Override
    protected int renderInstructions(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                     int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Золото добывается теми же способами, что и олово.", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "Очищение пока не нужно, собирайте сырым.", 0xFFFFFF00);
        return y;
    }

    @Override
    protected boolean hasRequiredItems() {
        return this.minecraft.player != null
                && this.minecraft.player.inventory.countItem(ModItems.UNREFINED_GOLD_ORE.get()) >= 16;
    }

    @Override
    protected boolean isQuestCompleted() {
        return QuestManager.isUnrefinedGoldOreCompleted();
    }

    @Override
    protected void markCompleted() {
        QuestManager.setUnrefinedGoldOreCompleted(true);
    }
}

