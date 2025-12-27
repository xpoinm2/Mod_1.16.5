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
public class StartSmithingQuestScreen extends AbstractQuestScreen {

    public StartSmithingQuestScreen(Screen parent) {
        super(parent, "Начало кузнечного дела");
    }

    @Override
    protected int renderDescription(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                    int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Постройте наковальню", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "и подготовьте кузню", 0xFFFFFF00);
        return y;
    }

    @Override
    protected int renderGoals(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                              int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Добыть железную руду с примесями", 0xFFFFFF00);
        ItemStack stack = new ItemStack(ModItems.IMPURE_IRON_ORE.get());
        if (GuiUtil.renderItemWithTooltip(this, ms, stack, x, y, mouseX, mouseY)) {
            hoveredStack = stack;
        }
        return y + 24;
    }

    @Override
    protected int renderInstructions(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                     int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Ищите жилы железной руды с примесями", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "и добывайте их каменными или костяными кирками", 0xFFFFFF00);
        return y;
    }

    @Override
    protected boolean hasRequiredItems() {
        return this.minecraft.player != null
                && this.minecraft.player.inventory.contains(new ItemStack(ModItems.IMPURE_IRON_ORE.get()));
    }

    @Override
    protected boolean isQuestUnlocked() {
        return QuestManager.isStoneToolsCompleted() || QuestManager.isBoneToolsCompleted();
    }

    @Override
    protected boolean isQuestCompleted() {
        return QuestManager.isStartSmithingCompleted();
    }

    @Override
    protected void markCompleted() {
        QuestManager.setStartSmithingCompleted(true);
    }
}
