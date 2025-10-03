package com.example.examplemod.client.screen;

import com.example.examplemod.ModItems;
import com.example.examplemod.client.FramedButton;
import com.example.examplemod.client.GuiUtil;
import com.example.examplemod.quest.QuestManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class FirepitQuestScreen extends Screen {
    private final Screen parent;
    private int scrollOffset = 0;
    private int maxScroll = 0;
    private ItemStack hoveredStack = ItemStack.EMPTY;
    private FramedButton confirmButton;
    private FramedButton layoutButton;
    private boolean showLayout = false;

    public FirepitQuestScreen(Screen parent) {
        super(new StringTextComponent("Кострище"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int x0 = 10;
        int y0 = 10;
        this.addButton(new FramedButton(x0 + 5, y0 + 5, 20, 20, "<", 0xFFFFFF00, 0xFFFFFFFF,
                b -> this.minecraft.setScreen(parent)));

        int btnWidth = 100;
        int btnHeight = 20;
        int btnX = (this.width - btnWidth) / 2;
        int btnY = this.height - btnHeight - 15;
        this.confirmButton = new FramedButton(btnX, btnY, btnWidth, btnHeight, "Подтвердить", 0xFF00FF00, 0xFFFFFFFF,
                b -> {
                    if (hasRequiredItems() && QuestManager.isStartSmithingCompleted()) {
                        QuestManager.setFirepitCompleted(true);
                    }
                });
        this.addButton(this.confirmButton);

        this.layoutButton = new FramedButton(0, 0, 120, 20, "Показать схему", 0xFFFFFF00, 0xFFFFFFFF,
                b -> {
                    showLayout = !showLayout;
                    layoutButton.setMessage(new StringTextComponent(showLayout ? "Скрыть схему" : "Показать схему"));
                });
        this.addButton(this.layoutButton);
        super.init();
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float pt) {
        this.renderBackground(ms);
        hoveredStack = ItemStack.EMPTY;
        int x0 = 10;
        int y0 = 10;
        int width = this.width - 20;
        int height = this.height - 20;
        GuiUtil.drawPanel(ms, x0, y0, width, height);
        boolean unlocked = QuestManager.isStartSmithingCompleted();
        this.confirmButton.visible = unlocked && !QuestManager.isFirepitCompleted();
        drawTitle(ms, x0 + width / 2, y0 + 15);

        int leftX = x0 + 20;
        int leftY = y0 + 40 - scrollOffset;
        drawScaledUnderlined(ms, "Описание", leftX, leftY, 0xFFFFFFFF, 4f / 3f);
        leftY += 30;
        drawString(ms, this.font, "Устройство кострища", leftX, leftY, 0xFFFFFF00);
        leftY += 10;
        drawString(ms, this.font, "для прогрева плит:", leftX, leftY, 0xFFFFFF00);
        leftY += 20;
        drawString(ms, this.font, "Основание: выровняйте", leftX, leftY, 0xFFFFFF00);
        leftY += 10;
        drawString(ms, this.font, "площадку 1×1.5 м", leftX, leftY, 0xFFFFFF00);
        leftY += 10;
        drawString(ms, this.font, "и застелите щебнем", leftX, leftY, 0xFFFFFF00);
        leftY += 10;
        drawString(ms, this.font, "для дренажа", leftX, leftY, 0xFFFFFF00);
        leftY += 20;
        drawString(ms, this.font, "Укладка плит: плоские", leftX, leftY, 0xFFFFFF00);
        leftY += 10;
        drawString(ms, this.font, "сланец/песчаник", leftX, leftY, 0xFFFFFF00);
        leftY += 10;
        drawString(ms, this.font, "в ряд с зазором 2 см", leftX, leftY, 0xFFFFFF00);
        leftY += 20;
        drawString(ms, this.font, "Топливо: тонкие ветки", leftX, leftY, 0xFFFFFF00);
        leftY += 10;
        drawString(ms, this.font, "для розжига, затем", leftX, leftY, 0xFFFFFF00);
        leftY += 10;
        drawString(ms, this.font, "полешки дуба/бука", leftX, leftY, 0xFFFFFF00);

        int leftBottom = leftY;

        int rightX = x0 + width / 2 + 20;
        int rightY = y0 + 40 - scrollOffset;
        drawScaledUnderlined(ms, "Цель", rightX, rightY, 0xFFFFFFFF, 4f / 3f);
        rightY += 30;
        drawString(ms, this.font, "Создать мультиструктуру", rightX, rightY, 0xFFFFFF00);
        rightY += 10;
        ItemStack firepitStack = new ItemStack(ModItems.FIREPIT_BLOCK.get());
        if (GuiUtil.renderItemWithTooltip(this, ms, firepitStack, rightX, rightY + 10, mouseX, mouseY)) {
            hoveredStack = firepitStack;
        }
        rightY += 40;
        drawScaledUnderlined(ms, "Инструкция", rightX, rightY, 0xFFFFFFFF, 4f / 3f);
        rightY += 30;
        drawString(ms, this.font, "Нажмите кнопку ниже,", rightX, rightY, 0xFFFFFF00);
        rightY += 10;
        drawString(ms, this.font, "чтобы увидеть схему", rightX, rightY, 0xFFFFFF00);
        rightY += 10;
        drawString(ms, this.font, "и активируйте", rightX, rightY, 0xFFFFFF00);
        rightY += 10;
        drawString(ms, this.font, "пиритовым огнивом", rightX, rightY, 0xFFFFFF00);
        rightY += 10;
        ItemStack pyriteStack = new ItemStack(ModItems.PYRITE_FLINT.get());
        if (GuiUtil.renderItemWithTooltip(this, ms, pyriteStack, rightX, rightY + 5, mouseX, mouseY)) {
            hoveredStack = pyriteStack;
        }
        rightY += 35;

        this.layoutButton.x = rightX;
        this.layoutButton.y = rightY;
        this.layoutButton.visible = true;
        rightY += this.layoutButton.getHeight() + 5;

        int layoutBottom = rightY;
        if (showLayout) {
            layoutBottom = drawLayout(ms, rightX, rightY);
            rightY = layoutBottom;
        }

        int contentBottom = Math.max(leftBottom, rightY);
        if (showLayout) {
            contentBottom = Math.max(contentBottom, layoutBottom);
        }
        maxScroll = Math.max(0, contentBottom - (y0 + height - 10));
        super.render(ms, mouseX, mouseY, pt);
    }

    private int drawLayout(MatrixStack ms, int startX, int startY) {
        int cellSize = 18;
        int padding = 2;
        int layoutWidth = cellSize * 4;
        int layoutHeight = cellSize * 4;
        fill(ms, startX - 2, startY - 2, startX + layoutWidth + 2, startY + layoutHeight + 2, 0xFF000000);
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                int cellX0 = startX + col * cellSize;
                int cellY0 = startY + row * cellSize;
                int cellX1 = cellX0 + cellSize - padding;
                int cellY1 = cellY0 + cellSize - padding;
                int color = getCellColor(col, row);
                fill(ms, cellX0, cellY0, cellX1, cellY1, color);
                String label = getCellLabel(col, row);
                int textCenterX = cellX0 + (cellX1 - cellX0) / 2;
                int textTop = cellY0 + ((cellY1 - cellY0) - this.font.lineHeight) / 2;
                drawCenteredString(ms, this.font, label, textCenterX, textTop, 0xFF000000);
            }
        }
        int legendY = startY + layoutHeight + 6;
        drawString(ms, this.font, "Х - хворост (углы)", startX, legendY, 0xFFFFFF00);
        legendY += 10;
        drawString(ms, this.font, "Д - деревянные плиты", startX, legendY, 0xFFFFFF00);
        legendY += 10;
        drawString(ms, this.font, "К - каменные плиты", startX, legendY, 0xFFFFFF00);
        return legendY + this.font.lineHeight;
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

    private boolean hasRequiredItems() {
        return this.minecraft.player != null &&
                this.minecraft.player.inventory.countItem(ModItems.PYRITE_FLINT.get()) > 0;
    }

    private void drawTitle(MatrixStack ms, int centerX, int y) {
        String title = this.title.getString();
        ms.pushPose();
        ms.scale(2.0F, 2.0F, 2.0F);
        drawCenteredString(ms, this.font, title, (int) (centerX / 2f), (int) (y / 2f), 0xFF00BFFF);
        ms.popPose();
        if (QuestManager.isFirepitCompleted()) {
            int titleWidth = this.font.width(title) * 2;
            drawString(ms, this.font, " (Выполнено)", centerX + titleWidth / 2 + 5, y, 0xFF00FF00);
        }
    }

    private void drawScaledUnderlined(MatrixStack ms, String text, int x, int y, int color, float scale) {
        ms.pushPose();
        ms.scale(scale, scale, scale);
        float inv = 1.0F / scale;
        this.font.draw(ms, text, x * inv, y * inv, color);
        ms.popPose();
        int width = (int) (this.font.width(text) * scale);
        int underlineY = (int) (y + this.font.lineHeight * scale);
        fill(ms, x, underlineY, x + width, underlineY + 1, color);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!hoveredStack.isEmpty()) {
            if (button == 0) {
                GuiUtil.openRecipe(hoveredStack);
                return true;
            }
            if (button == 1) {
                GuiUtil.openUsage(hoveredStack);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!hoveredStack.isEmpty()) {
            if (keyCode == GLFW.GLFW_KEY_R) {
                GuiUtil.openRecipe(hoveredStack);
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_U) {
                GuiUtil.openUsage(hoveredStack);
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (delta != 0) {
            scrollOffset = (int) Math.max(0, Math.min(maxScroll, scrollOffset - delta * 10));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }
}