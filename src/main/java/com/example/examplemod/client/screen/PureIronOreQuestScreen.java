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
public class PureIronOreQuestScreen extends AbstractQuestScreen {

    public PureIronOreQuestScreen(Screen parent) {
        super(parent, "Чистая железная руда");
    }

    @Override
    protected int renderDescription(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                    int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, "Сортировка и обогащение:", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, "после выемки руду", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, "сортировали на площадках,", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, "мыли в водотоках", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, "для отделения примесей.", 0xFFFFFF00);
        return y;
    }

    @Override
    protected int renderGoals(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                              int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, "Создать 8", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, "чистой железной руды", 0xFFFFFF00);
        ItemStack stack = new ItemStack(ModItems.PURE_IRON_ORE.get(), 8);
        if (GuiUtil.renderItemWithTooltip(this, ms, stack, x, y, mouseX, mouseY)) {
            hoveredStack = stack;
        }
        return y + 24;
    }

    @Override
    protected int renderInstructions(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                     int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, "Нужно железный кластер", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, "помыть в воде", 0xFFFFFF00);
        return y;
    }

    @Override
    protected boolean hasRequiredItems() {
        return this.minecraft.player != null
                && this.minecraft.player.inventory.countItem(ModItems.PURE_IRON_ORE.get()) >= 8;
    }

    @Override
    protected boolean isQuestUnlocked() {
        return QuestManager.isIronClusterCompleted();
    }

    @Override
    protected boolean isQuestCompleted() {
        return QuestManager.isPureIronOreCompleted();
    }

    @Override
    protected void markCompleted() {
        QuestManager.setPureIronOreCompleted(true);
    }
}
