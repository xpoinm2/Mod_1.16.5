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
public class StartSmithingQuestScreen extends AbstractQuestScreen {

    public StartSmithingQuestScreen(Screen parent) {
        super(parent, "Начало кузнечества");
    }

    @Override
    protected int renderDescription(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                    int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, "Постройте наковальню", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, "и подготовьте кузню", 0xFFFFFF00);
        return y;
    }

    @Override
    protected int renderGoals(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                              int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, "Получите обожжённую руду", 0xFFFFFF00);
        ItemStack stack = new ItemStack(ModItems.CALCINED_IRON_ORE.get());
        if (GuiUtil.renderItemWithTooltip(this, ms, stack, x, y, mouseX, mouseY)) {
            hoveredStack = stack;
        }
        return y + 24;
    }

    @Override
    protected int renderInstructions(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                     int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, "Переплавьте чистую железную руду", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, "в кострище, чтобы получить обожжённую руду", 0xFFFFFF00);
        return y;
    }

    @Override
    protected boolean hasRequiredItems() {
        return this.minecraft.player != null
                && this.minecraft.player.inventory.contains(new ItemStack(ModItems.CALCINED_IRON_ORE.get()));
    }

    @Override
    protected boolean isQuestUnlocked() {
        return QuestManager.isPureIronOreCompleted();
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
