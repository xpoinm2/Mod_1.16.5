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
public class HewnStonesQuestScreen extends AbstractQuestScreen {

    public HewnStonesQuestScreen(Screen parent) {
        super(parent, "Оттёсанные камни");
    }

    @Override
    protected int renderDescription(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                    int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, "Сбор обломков: люди искали уже", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, "отщепленные природой или ледниками", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, "куски кремня и базальта на", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, "поверхности, особенно в речных", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, "галечниках и моренах.", 0xFFFFFF00);
        return y;
    }

    @Override
    protected int renderGoals(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                              int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, "Нужно получить 10", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, "оттёсанных камней", 0xFFFFFF00);
        ItemStack stack = new ItemStack(ModItems.HEWN_STONE.get(), 10);
        if (GuiUtil.renderItemWithTooltip(this, ms, stack, x, y, mouseX, mouseY)) {
            hoveredStack = stack;
        }
        return y + 24;
    }

    @Override
    protected int renderInstructions(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                     int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, "Камни находятся в реках", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, "и на равнинах", 0xFFFFFF00);
        return y;
    }

    @Override
    protected boolean hasRequiredItems() {
        return this.minecraft.player != null
                && this.minecraft.player.inventory.countItem(ModItems.HEWN_STONE.get()) >= 10;
    }

    @Override
    protected boolean isQuestCompleted() {
        return QuestManager.isHewnStonesCompleted();
    }

    @Override
    protected void markCompleted() {
        QuestManager.setHewnStonesCompleted(true);
    }
}
