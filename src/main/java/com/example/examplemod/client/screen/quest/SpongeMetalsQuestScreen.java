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
public class SpongeMetalsQuestScreen extends AbstractQuestScreen {

    public SpongeMetalsQuestScreen(Screen parent) {
        super(parent, "Губчатые металлы");
    }

    @Override
    protected int renderDescription(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                    int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth,
                "Губчатые металлы получаются из обожжённой руды", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth,
                "в кирпичной печи. Они готовы к ковке в слитки.", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth,
                "Губчатые металлы можно хранить и переносить.", 0xFFFFFF00);
        return y;
    }

    @Override
    protected int renderGoals(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                              int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth,
                "Получить 8 губчатого железа, 8 губчатого олова и 8 губчатого золота", 0xFFFFFF00);
        y += 6;
        int iconX = x;
        ItemStack ironStack = new ItemStack(ModItems.SPONGE_IRON.get());
        if (GuiUtil.renderItemWithTooltip(this, ms, ironStack, iconX, y, mouseX, mouseY)) {
            hoveredStack = ironStack;
        }
        iconX += 22;
        ItemStack tinStack = new ItemStack(ModItems.SPONGE_TIN.get());
        if (GuiUtil.renderItemWithTooltip(this, ms, tinStack, iconX, y, mouseX, mouseY)) {
            hoveredStack = tinStack;
        }
        iconX += 22;
        ItemStack goldStack = new ItemStack(ModItems.SPONGE_GOLD.get());
        if (GuiUtil.renderItemWithTooltip(this, ms, goldStack, iconX, y, mouseX, mouseY)) {
            hoveredStack = goldStack;
        }
        y += 22;
        return y;
    }

    @Override
    protected int renderInstructions(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                     int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth,
                "Разместите обожжённую руду в кирпичной печи.", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth,
                "Подождите пока руда превратится в губчатый металл.", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth,
                "Губчатые металлы можно ковать в слитки на наковальне.", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth,
                "Используйте молот для ковки и щипцы для переноса.", 0xFFFFFF00);
        return y;
    }

    @Override
    protected boolean hasRequiredItems() {
        return this.minecraft.player != null
                && this.minecraft.player.inventory.countItem(ModItems.SPONGE_IRON.get()) >= 8
                && this.minecraft.player.inventory.countItem(ModItems.SPONGE_TIN.get()) >= 8
                && this.minecraft.player.inventory.countItem(ModItems.SPONGE_GOLD.get()) >= 8;
    }

    @Override
    protected boolean isQuestUnlocked() {
        return QuestManager.isBrickKilnCompleted();
    }

    @Override
    protected boolean isQuestCompleted() {
        return QuestManager.isSpongeMetalsCompleted();
    }

    @Override
    protected void markCompleted() {
        QuestManager.setSpongeMetalsCompleted(true);
    }
}