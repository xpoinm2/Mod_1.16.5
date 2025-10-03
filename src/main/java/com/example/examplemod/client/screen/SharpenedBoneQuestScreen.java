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
public class SharpenedBoneQuestScreen extends AbstractQuestScreen {

    public SharpenedBoneQuestScreen(Screen parent) {
        super(parent, "Заточенная кость");
    }

    @Override
    protected int renderDescription(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                    int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, "Старейший нож —", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, "затаченная кость", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, "или камень.", 0xFFFFFF00);
        return y;
    }

    @Override
    protected int renderGoals(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                              int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, "Создать заточенную кость", 0xFFFFFF00);
        ItemStack stack = new ItemStack(ModItems.SHARPENED_BONE.get());
        if (GuiUtil.renderItemWithTooltip(this, ms, stack, x, y, mouseX, mouseY)) {
            hoveredStack = stack;
        }
        return y + 24;
    }

    @Override
    protected int renderInstructions(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                     int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, "Используйте точильный", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, "камень на кости", 0xFFFFFF00);
        return y;
    }

    @Override
    protected boolean hasRequiredItems() {
        return this.minecraft.player != null
                && this.minecraft.player.inventory.contains(new ItemStack(ModItems.SHARPENED_BONE.get()));
    }

    @Override
    protected boolean isQuestCompleted() {
        return QuestManager.isSharpenedBoneCompleted();
    }

    @Override
    protected void markCompleted() {
        QuestManager.setSharpenedBoneCompleted(true);
    }
}
