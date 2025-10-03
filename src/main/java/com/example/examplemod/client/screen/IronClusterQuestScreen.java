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
public class IronClusterQuestScreen extends AbstractQuestScreen {

    public IronClusterQuestScreen(Screen parent) {
        super(parent, "Железный кластер");
    }

    @Override
    protected int renderDescription(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                    int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, "Подготовка руды:", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, "дробление молотами", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, "и сортировка на куски", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, "2-5 см", 0xFFFFFF00);
        return y;
    }

    @Override
    protected int renderGoals(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                              int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, "Добыть 8", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, "железных кластеров", 0xFFFFFF00);
        ItemStack stack = new ItemStack(ModItems.IRON_CLUSTER.get(), 8);
        if (GuiUtil.renderItemWithTooltip(this, ms, stack, x, y, mouseX, mouseY)) {
            hoveredStack = stack;
        }
        return y + 24;
    }

    @Override
    protected int renderInstructions(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                     int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, "Разбейте молотом", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, "железную руду", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, "с примесями", 0xFFFFFF00);
        return y;
    }

    @Override
    protected boolean hasRequiredItems() {
        return this.minecraft.player != null
                && this.minecraft.player.inventory.countItem(ModItems.IRON_CLUSTER.get()) >= 8;
    }

    @Override
    protected boolean isQuestUnlocked() {
        return QuestManager.isStartHammersCompleted();
    }

    @Override
    protected boolean isQuestCompleted() {
        return QuestManager.isIronClusterCompleted();
    }

    @Override
    protected void markCompleted() {
        QuestManager.setIronClusterCompleted(true);
    }
}
