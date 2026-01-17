package com.example.examplemod.client.screen.quest;

import com.example.examplemod.ModItems;
import com.example.examplemod.client.GuiUtil;
import com.example.examplemod.client.screen.main.ScrollArea;
import com.example.examplemod.quest.QuestManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FirepitQuestScreen extends AbstractQuestScreen {
    private Rect modelButtonRect = null;

    public FirepitQuestScreen(Screen parent) {
        super(parent, "Кострище");
    }

    @Override
    protected int renderDescription(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                    int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Устройство кострища", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "для прогрева плит:", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "Основание: выровняйте", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "площадку 1×1.5 м", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "и застелите щебнем", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "для дренажа", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "Укладка плит: плоские", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "сланец/песчаник", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "в ряд с зазором 2 см", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "Топливо: тонкие ветки", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "для розжига, затем", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "полешки дуба/бука", 0xFFFFFF00);
        return y;
    }

    @Override
    protected int renderGoals(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                              int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Создать мультиструктуру", 0xFFFFFF00);
        y += 6;
        ItemStack firepitStack = new ItemStack(ModItems.FIREPIT_BLOCK.get());
        if (GuiUtil.renderItemWithTooltip(this, ms, firepitStack, x, y, mouseX, mouseY)) {
            hoveredStack = firepitStack;
        }
        y += 22;
        return y;
    }

    @Override
    protected int renderInstructions(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                     int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Нажмите кнопку ниже,", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "чтобы увидеть 3D модель", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "и активируйте", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "пиритовым огнивом", 0xFFFFFF00);
        ItemStack pyrite = new ItemStack(ModItems.PYRITE_FLINT.get());
        if (GuiUtil.renderItemWithTooltip(this, ms, pyrite, x, y, mouseX, mouseY)) {
            hoveredStack = pyrite;
        }
        y += 24;

        int buttonWidth = 140;
        int buttonHeight = 20;
        int buttonX = x;
        int buttonY = y;
        modelButtonRect = new Rect(buttonX, buttonY, buttonWidth, buttonHeight);
        drawModelButton(ms, buttonX, buttonY, buttonWidth, buttonHeight, mouseX, mouseY);
        y += buttonHeight + 8;

        return y;
    }

    @Override
    protected boolean hasRequiredItems() {
        return this.minecraft.player != null
                && this.minecraft.player.inventory.countItem(ModItems.PYRITE_FLINT.get()) > 0;
    }

    @Override
    protected boolean isQuestUnlocked() {
        return QuestManager.isPureIronOreCompleted()
                && QuestManager.isCobbleSlabsCompleted()
                && QuestManager.isBrushwoodCompleted()
                && QuestManager.isPyriteFlintCompleted();
    }

    @Override
    protected boolean isQuestCompleted() {
        return QuestManager.isFirepitCompleted();
    }

    @Override
    protected void markCompleted() {
        QuestManager.setFirepitCompleted(true);
    }

    @Override
    protected void renderAdditional(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        super.renderAdditional(ms, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && modelButtonRect != null
                && modelButtonRect.contains(mouseX, mouseY)
                && descriptionArea.isVisible(modelButtonRect.x, modelButtonRect.y,
                modelButtonRect.width, modelButtonRect.height)) {
            this.minecraft.setScreen(new FirepitStructureScreen(this));
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void drawModelButton(MatrixStack ms, int x, int y, int width, int height, int mouseX, int mouseY) {
        int background = 0xAA444444;
        if (modelButtonRect != null && modelButtonRect.contains(mouseX, mouseY)) {
            background = 0xCC666666;
        }
        fill(ms, x, y, x + width, y + height, background);
        fill(ms, x, y, x + width, y + 1, 0xFF000000);
        fill(ms, x, y + height - 1, x + width, y + height, 0xFF000000);
        fill(ms, x, y, x + 1, y + height, 0xFF000000);
        fill(ms, x + width - 1, y, x + width, y + height, 0xFF000000);
        StringTextComponent text = new StringTextComponent("Показать 3D модель");
        drawCenteredString(ms, this.font, text, x + width / 2,
                y + (height - this.font.lineHeight) / 2, 0xFFFFFFFF);
    }


    private static class Rect {
        final int x;
        final int y;
        final int width;
        final int height;

        Rect(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        boolean contains(double px, double py) {
            return px >= x && px <= x + width && py >= y && py <= y + height;
        }
    }
}
