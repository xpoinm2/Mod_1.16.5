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
public class PyriteFlintQuestScreen extends AbstractQuestScreen {

    public PyriteFlintQuestScreen(Screen parent) {
        super(parent, "Пиритовое огниво");
    }

    @Override
    protected int renderDescription(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                    int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth,
                "Пирит позволяет высекать искры без железа.", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth,
                "Достаточно ударить им по базальту или другому твёрдому камню.", 0xFFFFFF00);
        return y;
    }

    @Override
    protected int renderGoals(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                              int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Создать 1 пиритовое огниво", 0xFFFFFF00);
        y += 6;
        ItemStack stack = new ItemStack(ModItems.PYRITE_FLINT.get());
        if (GuiUtil.renderItemWithTooltip(this, ms, stack, x, y, mouseX, mouseY)) {
            hoveredStack = stack;
        }
        y += 22;
        return y;
    }

    @Override
    protected int renderInstructions(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                     int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth,
                "Скрафтите огниво из пирита.", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth,
                "Удар по базальту о кусок пирита создаёт искру.", 0xFFFFFF00);
        return y;
    }

    @Override
    protected boolean hasRequiredItems() {
        return this.minecraft.player != null
                && this.minecraft.player.inventory.countItem(ModItems.PYRITE_FLINT.get()) > 0;
    }

    @Override
    protected boolean isQuestUnlocked() {
        return QuestManager.isPyriteCompleted();
    }

    @Override
    protected boolean isQuestCompleted() {
        return QuestManager.isPyriteFlintCompleted();
    }

    @Override
    protected void markCompleted() {
        QuestManager.setPyriteFlintCompleted(true);
    }
}