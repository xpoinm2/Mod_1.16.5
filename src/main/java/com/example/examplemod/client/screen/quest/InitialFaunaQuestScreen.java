package com.example.examplemod.client.screen.quest;

import com.example.examplemod.client.screen.main.ScrollArea;
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
        y = drawParagraph(ms, x, y, innerWidth, "Малина: леса, даёт скорость", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "Бузина: леса, снижает простуду", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "Клюква: болота, ускоряет копание", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "Дягель: болота, снижает яд", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "Хрен: равнины, снижает вирусы", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "Имбирь: джунгли, снижает переохлаждение", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "Лён: равнины, леса и болота, нужен для волокон", 0xFFFFFF00);
        return y;
    }

    @Override
    protected int renderGoals(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                              int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Изучить список растений", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "и их эффекты", 0xFFFFFF00);
        return y;
    }

    @Override
    protected int renderInstructions(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                     int mouseX, int mouseY, float partialTicks) {
        return drawParagraph(ms, x, y, innerWidth, "Просто подтвердите квест", 0xFFFFFF00);
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
