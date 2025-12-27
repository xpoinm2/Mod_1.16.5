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
    private boolean showLayout = false;
    private Rect layoutButtonRect = null;
    private String hoveredLayoutName = null;

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
        hoveredLayoutName = null;
        y = drawParagraph(ms, x, y, innerWidth, "Нажмите кнопку ниже,", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "чтобы увидеть схему", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "и активируйте", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "пиритовым огнивом", 0xFFFFFF00);
        ItemStack pyrite = new ItemStack(ModItems.PYRITE_FLINT.get());
        if (GuiUtil.renderItemWithTooltip(this, ms, pyrite, x, y, mouseX, mouseY)) {
            hoveredStack = pyrite;
        }
        y += 24;

        int buttonWidth = 120;
        int buttonHeight = 20;
        int buttonX = x;
        int buttonY = y;
        layoutButtonRect = new Rect(buttonX, buttonY, buttonWidth, buttonHeight);
        drawToggleButton(ms, buttonX, buttonY, buttonWidth, buttonHeight, mouseX, mouseY);
        y += buttonHeight + 8;

        if (showLayout) {
            y = drawLayout(ms, x, y, mouseX, mouseY);
        }
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
        if (hoveredLayoutName != null && descriptionArea.isPointInsideViewport(mouseX, mouseY)) {
            this.renderTooltip(ms, new StringTextComponent(hoveredLayoutName), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && layoutButtonRect != null
                && layoutButtonRect.contains(mouseX, mouseY)
                && descriptionArea.isVisible(layoutButtonRect.x, layoutButtonRect.y,
                layoutButtonRect.width, layoutButtonRect.height)) {
            showLayout = !showLayout;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void drawToggleButton(MatrixStack ms, int x, int y, int width, int height, int mouseX, int mouseY) {
        int background = showLayout ? 0xAA337733 : 0xAA444444;
        if (layoutButtonRect != null && layoutButtonRect.contains(mouseX, mouseY)) {
            background = showLayout ? 0xCC449944 : 0xCC666666;
        }
        fill(ms, x, y, x + width, y + height, background);
        fill(ms, x, y, x + width, y + 1, 0xFF000000);
        fill(ms, x, y + height - 1, x + width, y + height, 0xFF000000);
        fill(ms, x, y, x + 1, y + height, 0xFF000000);
        fill(ms, x + width - 1, y, x + width, y + height, 0xFF000000);
        StringTextComponent text = new StringTextComponent(showLayout ? "Скрыть схему" : "Показать схему");
        drawCenteredString(ms, this.font, text, x + width / 2,
                y + (height - this.font.lineHeight) / 2, 0xFFFFFFFF);
    }

    private int drawLayout(MatrixStack ms, int x, int y, int mouseX, int mouseY) {
        int cells = 4;
        int cellSize = 18;
        int padding = 2;
        int layoutWidth = cells * cellSize;
        int layoutHeight = cells * cellSize;
        fill(ms, x - 2, y - 2, x + layoutWidth + 2, y + layoutHeight + 2, 0xFF000000);
        for (int row = 0; row < cells; row++) {
            for (int col = 0; col < cells; col++) {
                int cellX0 = x + col * cellSize;
                int cellY0 = y + row * cellSize;
                int cellX1 = cellX0 + cellSize - padding;
                int cellY1 = cellY0 + cellSize - padding;
                int color = getCellColor(col, row);
                fill(ms, cellX0, cellY0, cellX1, cellY1, color);
                String label = getCellLabel(col, row);
                int textX = cellX0 + (cellX1 - cellX0) / 2;
                int textY = cellY0 + ((cellY1 - cellY0) - this.font.lineHeight) / 2;
                drawCenteredString(ms, this.font, label, textX, textY, 0xFF000000);
                if (descriptionArea.isPointInsideViewport(mouseX, mouseY)
                        && mouseX >= cellX0 && mouseX <= cellX1 && mouseY >= cellY0 && mouseY <= cellY1) {
                    hoveredLayoutName = getCellName(col, row);
                }
            }
        }
        int legendY = y + layoutHeight + 6;
        int legendWidth = descriptionArea != null ? descriptionArea.getViewportWidth() : 0;
        legendY = drawParagraph(ms, x, legendY, legendWidth, "Х - хворост (углы)", 0xFFFFFF00);
        legendY = drawParagraph(ms, x, legendY, legendWidth, "Д - деревянные плиты", 0xFFFFFF00);
        legendY = drawParagraph(ms, x, legendY, legendWidth, "К - каменные плиты", 0xFFFFFF00);
        return legendY;
    }

    private int getCellColor(int col, int row) {
        boolean corner = (col == 0 || col == 3) && (row == 0 || row == 3);
        if (corner) {
            return 0xFFCC8844;
        }
        if (col == 0 || col == 3 || row == 0 || row == 3) {
            return 0xFF8B7355;
        }
        return 0xFFAAAEB6;
    }

    private String getCellLabel(int col, int row) {
        boolean corner = (col == 0 || col == 3) && (row == 0 || row == 3);
        if (corner) {
            return "Х";
        }
        if (col == 0 || col == 3 || row == 0 || row == 3) {
            return "Д";
        }
        return "К";
    }

    private String getCellName(int col, int row) {
        boolean corner = (col == 0 || col == 3) && (row == 0 || row == 3);
        if (corner) {
            return "Хворост";
        }
        if (col == 0 || col == 3 || row == 0 || row == 3) {
            return "Деревянная плита";
        }
        return "Каменная плита";
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
