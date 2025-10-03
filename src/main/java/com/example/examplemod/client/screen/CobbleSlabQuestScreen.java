package com.example.examplemod.client.screen;

import com.example.examplemod.client.GuiUtil;
import com.example.examplemod.quest.QuestManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
        y = drawParagraph(ms, x, y, "Каменная поверхность", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, "для дальнейших", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, "изобретений", 0xFFFFFF00);
        return y;
    }

    @Override
    protected int renderGoals(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                              int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, "Создать 4 булыжные плиты", 0xFFFFFF00);
        y += 6;
        ItemStack stack = new ItemStack(Items.COBBLESTONE_SLAB, 4);
        if (GuiUtil.renderItemWithTooltip(this, ms, stack, x, y, mouseX, mouseY)) {
            hoveredStack = stack;
        }
        y += 22;
        return y;
    }

    @Override
    protected int renderInstructions(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                     int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, "Скрафтить булыжные", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, "плиты", 0xFFFFFF00);
        return y;
    }

    @Override
    protected boolean hasRequiredItems() {
        return this.minecraft.player != null
                && this.minecraft.player.inventory.countItem(Items.COBBLESTONE_SLAB) >= 4;
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
