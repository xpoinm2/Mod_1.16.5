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
public class BoneTongsQuestScreen extends AbstractQuestScreen {

    public BoneTongsQuestScreen(Screen parent) {
        super(parent, "Костяные щипцы");
    }

    @Override
    protected int renderDescription(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                    int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Костяные щипцы — это удлинённые рычаги,", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "собранные из костей и шнуров.", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "Щипцы создают дыхание в руках", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "для работы с раскалённым металлом.", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "Внутри есть отсек, где можно временно", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "хранить разогретые материалы.", 0xFFFFFF00);
        return y;
    }

    @Override
    protected int renderGoals(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                              int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Скрафтить костяные щипцы", 0xFFFFFF00);
        y += 4;
        ItemStack tongsStack = new ItemStack(ModItems.BONE_TONGS.get());
        if (GuiUtil.renderItemWithTooltip(this, ms, tongsStack, x, y, mouseX, mouseY)) {
            hoveredStack = tongsStack;
        }
        y += 24;
        return y;
    }

    @Override
    protected int renderInstructions(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                     int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Функция 1: берёт горячие слитки из кострища", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "и переносит их в печь без ожогов.", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "Функция 2: вытаскивает продукты печи", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "и аккуратно укладывает обратно в кострище.", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "Функция 3: хранит один предмет внутри", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "для контроля температуры и дозагрузки.", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "Функция 4: щелчком по устройствам можно", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "переносить содержимое между печью и кострищем.", 0xFFFFFF00);
        return y;
    }

    @Override
    protected boolean hasRequiredItems() {
        return this.minecraft.player != null
                && this.minecraft.player.inventory.countItem(ModItems.BONE_TONGS.get()) >= 1;
    }

    @Override
    protected boolean isQuestUnlocked() {
        return QuestManager.isFirepitCompleted();
    }

    @Override
    protected boolean isQuestCompleted() {
        return QuestManager.isBoneTongsCompleted();
    }

    @Override
    protected void markCompleted() {
        QuestManager.setBoneTongsCompleted(true);
    }
}
