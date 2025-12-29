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
public class CobbleSlabQuestScreen extends AbstractQuestScreen {

    public CobbleSlabQuestScreen(Screen parent) {
        super(parent, "Булыжная плита");
    }

    @Override
    protected int renderDescription(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                    int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Каменная поверхность", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "для дальнейших", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "изобретений", 0xFFFFFF00);
        return y;
    }

    @Override
    protected int renderGoals(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                              int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Скрафтить 4 булыжные плиты", 0xFFFFFF00);
        y += 6;
        ItemStack stack = new ItemStack(ModItems.COBBLESTONE_SLAB.get(), 4);
        if (GuiUtil.renderItemWithTooltip(this, ms, stack, x, y, mouseX, mouseY)) {
            hoveredStack = stack;
        }
        y += 22;
        return y;
    }

    @Override
    protected int renderInstructions(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                     int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Скрафтить булыжные", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "плиты", 0xFFFFFF00);
        return y;
    }

    @Override
    protected boolean hasRequiredItems() {
        if (this.minecraft.player == null) {
            return false;
        }
        int count = 0;
        for (ItemStack stack : this.minecraft.player.inventory.items) {
            if (stack.getItem() == ModItems.COBBLESTONE_SLAB.get()) {
                count += stack.getCount();
                if (count >= 4) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected boolean isQuestUnlocked() {
        return QuestManager.isSlabsCompleted();
    }

    @Override
    protected boolean isQuestCompleted() {
        return QuestManager.isCobbleSlabsCompleted();
    }

    @Override
    protected void markCompleted() {
        QuestManager.setCobbleSlabsCompleted(true);
    }
}
