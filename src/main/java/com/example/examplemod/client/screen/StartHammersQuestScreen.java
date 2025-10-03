package com.example.examplemod.client.screen;

import com.example.examplemod.ModItems;
import com.example.examplemod.client.GuiUtil;
import com.example.examplemod.quest.QuestManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StartHammersQuestScreen extends AbstractQuestScreen {

    public StartHammersQuestScreen(Screen parent) {
        super(parent, "Начало молотов");
    }

    @Override
    protected int renderDescription(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                    int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Молот нужен для", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "дробления руды", 0xFFFFFF00);
        return y;
    }

    @Override
    protected int renderGoals(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                              int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Создайте каменный молот", 0xFFFFFF00);
        ItemStack stack = new ItemStack(ModItems.STONE_HAMMER.get());
        if (GuiUtil.renderItemWithTooltip(this, ms, stack, x, y, mouseX, mouseY)) {
            hoveredStack = stack;
        }
        return y + 24;
    }

    @Override
    protected int renderInstructions(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                     int mouseX, int mouseY, float partialTicks) {
        String instruction = "Крафт молота: 2 палки и 2 камня. Найдите рецепт в книге крафта.";
        for (IReorderingProcessor line : this.font.split(new StringTextComponent(instruction), innerWidth)) {
            this.font.draw(ms, line, x, y, 0xFFFFFF00);
            y += this.font.lineHeight + 2;
        }
        return y;
    }

    @Override
    protected boolean hasRequiredItems() {
        return this.minecraft.player != null
                && this.minecraft.player.inventory.contains(new ItemStack(ModItems.STONE_HAMMER.get()));
    }

    @Override
    protected boolean isQuestCompleted() {
        return QuestManager.isStartHammersCompleted();
    }

    @Override
    protected void markCompleted() {
        QuestManager.setStartHammersCompleted(true);
    }
}
