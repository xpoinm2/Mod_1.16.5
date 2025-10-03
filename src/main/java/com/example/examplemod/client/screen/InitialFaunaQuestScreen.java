package com.example.examplemod.client.screen;

import com.example.examplemod.quest.QuestManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class InitialFaunaQuestScreen extends AbstractQuestScreen {

    public InitialFaunaQuestScreen(Screen parent) {
        super(parent, "Начальная фауна");
    }

    @Override
    protected int renderDescription(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                    int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, "Малина: леса, даёт скорость", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, "Бузина: леса, снижает простуду", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, "Клюква: болота, ускоряет копание", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, "Дягель: болота, снижает яд", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, "Хрен: равнины, снижает вирусы", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, "Имбирь: джунгли, снижает переохлаждение", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, "Лён: равнины, леса и болота, нужен для волокон", 0xFFFFFF00);
        return y;
    }

    @Override
    protected int renderGoals(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                              int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, "Изучить список растений", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, "и их эффекты", 0xFFFFFF00);
        return y;
    }

    @Override
    protected int renderInstructions(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                     int mouseX, int mouseY, float partialTicks) {
        return drawParagraph(ms, x, y, "Просто подтвердите квест", 0xFFFFFF00);
    }

    @Override
    protected boolean hasRequiredItems() {
        return true;
    }

    @Override
    protected boolean isQuestCompleted() {
        return QuestManager.isInitialFaunaCompleted();
    }

    @Override
    protected void markCompleted() {
        QuestManager.setInitialFaunaCompleted(true);
    }
}
