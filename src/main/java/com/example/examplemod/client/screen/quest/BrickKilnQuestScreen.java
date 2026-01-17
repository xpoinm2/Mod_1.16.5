package com.example.examplemod.client.screen.quest;

import com.example.examplemod.ModItems;
import com.example.examplemod.client.FramedButton;
import com.example.examplemod.client.GuiUtil;
import com.example.examplemod.client.screen.main.ScrollArea;
import com.example.examplemod.quest.QuestManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BrickKilnQuestScreen extends AbstractQuestScreen {

    private FramedButton viewStructureButton;

    public BrickKilnQuestScreen(Screen parent) {
        super(parent, "Кирпичная печь");
    }

    @Override
    protected void init() {
        super.init();

        // Добавляем кнопку просмотра 3D структуры
        this.viewStructureButton = new FramedButton(0, 0, 120, 20, "Просмотреть структуру",
                0xFF000000, 0xFFFFFF00,
                b -> this.minecraft.setScreen(new BrickKilnStructureScreen(this)));
        this.addButton(viewStructureButton);
    }

    @Override
    protected int renderDescription(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                    int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Кирпичная печь — это высокотемпературная", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "конструкция для обжига глины в кирпич.", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "Она состоит из кирпичных блоков с футеровкой", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "и позволяет производить качественный", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "строительный материал.", 0xFFFFFF00);
        return y;
    }

    @Override
    protected int renderGoals(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                              int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Сделать 58 кирпичных блоков с футеровкой", 0xFFFFFF00);
        y += 6;
        ItemStack brickStack = new ItemStack(ModItems.BRICK_BLOCK_WITH_LINING.get());
        if (GuiUtil.renderItemWithTooltip(this, ms, brickStack, x, y, mouseX, mouseY)) {
            hoveredStack = brickStack;
        }
        y += 24;
        return y;
    }

    @Override
    protected int renderInstructions(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                     int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Нужно собрать мультиблок кирпичной печи", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "и ударить молотом для активации.", 0xFFFFFF00);
        y += 10;

        // Позиционируем кнопку просмотра структуры
        if (viewStructureButton != null) {
            viewStructureButton.x = x;
            viewStructureButton.y = y;
            viewStructureButton.setWidth(Math.min(innerWidth, 150));
        }
        y += 30;

        y = drawParagraph(ms, x, y, innerWidth, "Мультиблок состоит из 3x3x3 кирпичных блоков", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "с футеровкой. Центральный блок среднего слоя", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "должен оставаться пустым.", 0xFFFFFF00);
        y += 10;

        y = drawParagraph(ms, x, y, innerWidth, "После сборки мультиблока ударьте молотом", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "по любому блоку структуры для формирования печи.", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "Печь готова к использованию!", 0xFFFFFF00);

        return y;
    }

    @Override
    protected boolean hasRequiredItems() {
        return this.minecraft.player != null
                && this.minecraft.player.inventory.countItem(ModItems.BRICK_BLOCK_WITH_LINING.get()) >= 58;
    }

    @Override
    protected boolean isQuestUnlocked() {
        return QuestManager.isAncientWorldCompleted();
    }

    @Override
    protected boolean isQuestCompleted() {
        return QuestManager.isBrickKilnCompleted();
    }

    @Override
    protected void markCompleted() {
        QuestManager.setBrickKilnCompleted(true);
    }
}