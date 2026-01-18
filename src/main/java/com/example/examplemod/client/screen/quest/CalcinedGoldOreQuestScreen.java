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
public class CalcinedGoldOreQuestScreen extends AbstractQuestScreen {

    public CalcinedGoldOreQuestScreen(Screen parent) {
        super(parent, "Обожжённая золотая руда");
    }

    @Override
    protected int renderDescription(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                    int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth,
                "Золото требует длительного прогрева, чтобы избавиться от влаги и остатков глины.", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth,
                "Горячая руда легко превращается в чистый металл, если вовремя дать ей остыть на костре.", 0xFFFFFF00);
        return y;
    }

    @Override
    protected int renderGoals(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                              int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth,
                "Получить 16 обожжённой золотой руды", 0xFFFFFF00);
        y += 6;
        ItemStack calcinedStack = new ItemStack(ModItems.CALCINED_GOLD_ORE.get());
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
                "Кладите очищённую гравийную золотую руду на кострище, чтобы сначала получить горячую обожжённую руду.", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth,
                "Горячая руда наносит урон при контакте, поэтому перекладывайте её щипцами и держите подальше от кожи.", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth,
                "Когда горячая руда остынет, она превратится в обожжённую золотую руду, которую можно плавить дальше.", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth,
                "Можно жарить и грязную гравийную золотую руду, но из неё выйдет меньше металла, чем из очищенной.", 0xFFFFFF00);
        return y;
    }

    @Override
    protected boolean hasRequiredItems() {
        return this.minecraft.player != null
                && this.minecraft.player.inventory.countItem(ModItems.CALCINED_GOLD_ORE.get()) >= 16;
    }

    @Override
    protected boolean isQuestUnlocked() {
        return QuestManager.isCleanedGravelGoldOreCompleted();
    }

    @Override
    protected boolean isQuestCompleted() {
        return QuestManager.isCalcinedGoldOreCompleted();
    }

    @Override
    protected void markCompleted() {
        QuestManager.setCalcinedGoldOreCompleted(true);
    }
}

