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
public class MetalChunksQuestScreen extends AbstractQuestScreen {

    public MetalChunksQuestScreen(Screen parent) {
        super(parent, "Куски металлов");
    }

    @Override
    protected int renderDescription(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                    int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth,
                "Губку дробили не чтобы “сломать”,", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth,
                "а чтобы дать металлу шанс стать металлом...", 0xFFFFFF00);
        return y;
    }

    @Override
    protected int renderGoals(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                              int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth,
                "Скрафтить 8 кусков олова, золота или железа", 0xFFFFFF00);
        y += 6;
        int iconX = x;
        ItemStack ironStack = new ItemStack(ModItems.IRON_CHUNK.get());
        if (GuiUtil.renderItemWithTooltip(this, ms, ironStack, iconX, y, mouseX, mouseY)) {
            hoveredStack = ironStack;
        }
        iconX += 22;
        ItemStack tinStack = new ItemStack(ModItems.TIN_CHUNK.get());
        if (GuiUtil.renderItemWithTooltip(this, ms, tinStack, iconX, y, mouseX, mouseY)) {
            hoveredStack = tinStack;
        }
        iconX += 22;
        ItemStack goldStack = new ItemStack(ModItems.GOLD_CHUNK.get());
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
                "Сделайте булыжниковую наковальню: ПКМ", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth,
                "молотом по блоку булыжника и выберите", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth,
                "в диалоге пункт \"Наковальня\".", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth,
                "Поместите губчатый металл в левый слот,", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth,
                "молот в слот инструмента и нажимайте", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth,
                "кнопку молота, пока не получите кусок.", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth,
                "Накуйте 8 кусков одного типа.", 0xFFFFFF00);
        return y;
    }

    @Override
    protected boolean hasRequiredItems() {
        if (this.minecraft == null || this.minecraft.player == null) {
            return false;
        }
        return this.minecraft.player.inventory.countItem(ModItems.IRON_CHUNK.get()) >= 8
                || this.minecraft.player.inventory.countItem(ModItems.TIN_CHUNK.get()) >= 8
                || this.minecraft.player.inventory.countItem(ModItems.GOLD_CHUNK.get()) >= 8;
    }

    @Override
    protected boolean isQuestUnlocked() {
        return QuestManager.isSpongeMetalsCompleted();
    }

    @Override
    protected boolean isQuestCompleted() {
        return QuestManager.isMetalChunksCompleted();
    }

    @Override
    protected void markCompleted() {
        QuestManager.setMetalChunksCompleted(true);
    }
}
