package com.example.examplemod.client.screen.quest;

import com.example.examplemod.ModItems;
import com.example.examplemod.client.GuiUtil;
import com.example.examplemod.client.screen.main.ScrollArea;
import com.example.examplemod.item.RoastedOreItem;
import com.example.examplemod.quest.QuestManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CalcinedIronOreQuestScreen extends AbstractQuestScreen {

    public CalcinedIronOreQuestScreen(Screen parent) {
        super(parent, "Обожжённая железная руда");
    }

    @Override
    protected int renderDescription(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                    int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth,
                "Обжиг руды удаляет влагу и примеси перед плавкой.", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth,
                "Такой материал легче довести до металла.", 0xFFFFFF00);
        return y;
    }

    @Override
    protected int renderGoals(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                              int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth,
                "Получить 16 обожжённой железной руды", 0xFFFFFF00);
        y += 6;
        ItemStack calcinedStack = new ItemStack(ModItems.CALCINED_IRON_ORE.get());
        if (GuiUtil.renderItemWithTooltip(this, ms, calcinedStack, x, y, mouseX, mouseY)) {
            hoveredStack = calcinedStack;
        }
        y += 22;
        return y;
    }

    @Override
    protected int renderInstructions(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                     int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth,
                "Разложите чистую железную руду на кострище.", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth,
                "Обожгите её до состояния обожжённой железной руды.", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth,
                "Горячую руду перекладывайте щипцами, чтобы не получить ожог.", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth,
                "Грязную железную гравийную руду тоже можно жарить на кострище, но металл выходит тоньше.", 0xFFFFFF00);
        return y;
    }

    @Override
    protected boolean hasRequiredItems() {
        return this.minecraft.player != null
                && this.minecraft.player.inventory.countItem(ModItems.CALCINED_IRON_ORE.get()) >= 16;
    }

    @Override
    protected boolean isQuestUnlocked() {
        return QuestManager.isFirepitCompleted();
    }

    @Override
    protected boolean isQuestCompleted() {
        return QuestManager.isCalcinedIronOreCompleted();
    }

    @Override
    protected void markCompleted() {
        QuestManager.setCalcinedIronOreCompleted(true);
    }
}