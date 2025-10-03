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
public class BigBoneQuestScreen extends AbstractQuestScreen {

    public BigBoneQuestScreen(Screen parent) {
        super(parent, "Большая кость");
    }

    @Override
    protected int renderDescription(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                    int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, "Добывали с крупного", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, "рогатого скота", 0xFFFFFF00);
        return y;
    }

    @Override
    protected int renderGoals(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                              int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, "Добыть 10 больших костей", 0xFFFFFF00);
        y += 6;
        ItemStack stack = new ItemStack(ModItems.BIG_BONE.get(), 10);
        if (GuiUtil.renderItemWithTooltip(this, ms, stack, x, y, mouseX, mouseY)) {
            hoveredStack = stack;
        }
        y += 22;
        return y;
    }

    @Override
    protected int renderInstructions(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                     int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, "Добываются с животных", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, "(Не всех)", 0xFFFFFF00);
        return y;
    }

    @Override
    protected boolean hasRequiredItems() {
        return this.minecraft.player != null
                && this.minecraft.player.inventory.countItem(ModItems.BIG_BONE.get()) >= 10;
    }

    @Override
    protected boolean isQuestCompleted() {
        return QuestManager.isBigBonesCompleted();
    }

    @Override
    protected void markCompleted() {
        QuestManager.setBigBonesCompleted(true);
    }
}
