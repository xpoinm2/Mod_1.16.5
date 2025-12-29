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
public class StoneToolsQuestScreen extends AbstractQuestScreen {

    public StoneToolsQuestScreen(Screen parent) {
        super(parent, "Каменные инструменты");
    }

    @Override
    protected int renderDescription(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                    int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Каменные инструменты", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "служат началом прогресса", 0xFFFFFF00);
        return y;
    }

    @Override
    protected int renderGoals(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                              int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Создать набор каменных инструментов", 0xFFFFFF00);
        ItemStack[] tools = {
                new ItemStack(ModItems.STONE_PICKAXE.get()),
                new ItemStack(ModItems.STONE_AXE.get()),
                new ItemStack(ModItems.STONE_SHOVEL.get()),
                new ItemStack(ModItems.STONE_HOE.get()),
                new ItemStack(ModItems.STONE_SWORD.get())
        };
        int itemsPerRow = Math.max(1, innerWidth / 20);
        for (int i = 0; i < tools.length; i++) {
            int itemX = x + (i % itemsPerRow) * 20;
            int itemY = y + (i / itemsPerRow) * 22;
            if (GuiUtil.renderItemWithTooltip(this, ms, tools[i], itemX, itemY, mouseX, mouseY)) {
                hoveredStack = tools[i];
            }
        }
        int rows = (tools.length + itemsPerRow - 1) / itemsPerRow;
        y += rows * 22;
        return y + 4;
    }

    @Override
    protected int renderInstructions(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                     int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Крафтите инструменты из", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "камня и палок", 0xFFFFFF00);
        return y;
    }

    @Override
    protected boolean hasRequiredItems() {
        if (this.minecraft.player == null) {
            return false;
        }
        boolean hasPickaxe = false;
        boolean hasAxe = false;
        boolean hasShovel = false;
        boolean hasHoe = false;
        boolean hasSword = false;

        for (ItemStack stack : this.minecraft.player.inventory.items) {
            if (stack.getItem() == ModItems.STONE_PICKAXE.get()) {
                hasPickaxe = true;
            } else if (stack.getItem() == ModItems.STONE_AXE.get()) {
                hasAxe = true;
            } else if (stack.getItem() == ModItems.STONE_SHOVEL.get()) {
                hasShovel = true;
            } else if (stack.getItem() == ModItems.STONE_HOE.get()) {
                hasHoe = true;
            } else if (stack.getItem() == ModItems.STONE_SWORD.get()) {
                hasSword = true;
            }
        }

        return hasPickaxe && hasAxe && hasShovel && hasHoe && hasSword;
    }

    @Override
    protected boolean isQuestCompleted() {
        return QuestManager.isStoneToolsCompleted();
    }

    @Override
    protected void markCompleted() {
        QuestManager.setStoneToolsCompleted(true);
    }
}
