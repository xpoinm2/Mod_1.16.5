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
public class BoneToolsQuestScreen extends AbstractQuestScreen {

    public BoneToolsQuestScreen(Screen parent) {
        super(parent, "Костяные инструменты");
    }

    @Override
    protected int renderDescription(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                    int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Заострённые кости", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "крупного рогатого", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "скота, закреплённые", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "в деревянных держателях", 0xFFFFFF00);
        return y;
    }

    @Override
    protected int renderGoals(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                              int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Сделать костяной набор", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "инструментов:", 0xFFFFFF00);
        y += 4;
        ItemStack[] stacks = new ItemStack[]{
                new ItemStack(ModItems.BONE_PICKAXE.get()),
                new ItemStack(ModItems.BONE_AXE.get()),
                new ItemStack(ModItems.BONE_HOE.get()),
                new ItemStack(ModItems.BONE_SHOVEL.get()),
                new ItemStack(ModItems.BONE_SWORD.get())
        };
        int itemY = y;
        int itemX = x;
        for (ItemStack stack : stacks) {
            if (GuiUtil.renderItemWithTooltip(this, ms, stack, itemX, itemY, mouseX, mouseY)) {
                hoveredStack = stack;
            }
            itemX += 20;
        }
        y = itemY + 24;
        return y;
    }

    @Override
    protected int renderInstructions(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                     int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Деревянные инструменты", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "недоступны, поэтому", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "можно начать", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "с костяных.", 0xFFFFFF00);
        return y;
    }

    @Override
    protected boolean hasRequiredItems() {
        if (this.minecraft.player == null) {
            return false;
        }
        return this.minecraft.player.inventory.countItem(ModItems.BONE_PICKAXE.get()) >= 1
                && this.minecraft.player.inventory.countItem(ModItems.BONE_AXE.get()) >= 1
                && this.minecraft.player.inventory.countItem(ModItems.BONE_HOE.get()) >= 1
                && this.minecraft.player.inventory.countItem(ModItems.BONE_SHOVEL.get()) >= 1
                && this.minecraft.player.inventory.countItem(ModItems.BONE_SWORD.get()) >= 1;
    }

    @Override
    protected boolean isQuestUnlocked() {
        return QuestManager.isSharpenedBoneCompleted()
                && QuestManager.isFlaxFibersCompleted()
                && QuestManager.isBranchCompleted();
    }

    @Override
    protected boolean isQuestCompleted() {
        return QuestManager.isBoneToolsCompleted();
    }

    @Override
    protected void markCompleted() {
        QuestManager.setBoneToolsCompleted(true);
    }
}
