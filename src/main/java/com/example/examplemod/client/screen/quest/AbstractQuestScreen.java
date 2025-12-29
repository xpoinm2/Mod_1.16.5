package com.example.examplemod.client.screen.quest;

import com.example.examplemod.client.FramedButton;
import com.example.examplemod.client.GuiUtil;
import com.example.examplemod.client.screen.main.ScrollArea;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

/**
 * Base implementation for quest detail screens that provides a common layout
 * with scrollable description, goal and instruction panels.
 */
public abstract class AbstractQuestScreen extends Screen {
    protected final Screen parent;
    protected ItemStack hoveredStack = ItemStack.EMPTY;

    private FramedButton backButton;
    protected FramedButton confirmButton;

    protected ScrollArea descriptionArea;
    protected ScrollArea goalsArea;
    protected ScrollArea instructionsArea;

    private int panelX;
    private int panelY;
    private int panelWidth;
    private int panelHeight;

    private static final int PANEL_MARGIN = 10;
    private static final int CONTENT_MARGIN = 15;
    private static final int COLUMN_GAP = 12;
    private static final int TITLE_OFFSET_Y = 18;
    private static final int TITLE_SCALE = 2;
    private static final int CONFIRM_BUTTON_BOTTOM_MARGIN = 5;
    private static final int CONFIRM_BUTTON_SPACING = 10;
    private static final int INSTRUCTIONS_VERTICAL_SHIFT = 10;
    private static final float SECTION_LABEL_SCALE = 1.15f;
    private static final int LINE_SPACING = 4;

    protected AbstractQuestScreen(Screen parent, String title) {
        super(new StringTextComponent(title));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        this.panelX = PANEL_MARGIN;
        this.panelY = PANEL_MARGIN;
        this.panelWidth = this.width - PANEL_MARGIN * 2;
        this.panelHeight = this.height - PANEL_MARGIN * 2;

        if (this.backButton != null) {
            this.children.remove(this.backButton);
            this.buttons.remove(this.backButton);
        }
        this.backButton = new FramedButton(panelX + 5, panelY + 5, 20, 20, "<", 0xFFFFFF00, 0xFFFFFFFF,
                b -> this.minecraft.setScreen(parent));
        this.addButton(this.backButton);

        if (this.confirmButton != null) {
            this.children.remove(this.confirmButton);
            this.buttons.remove(this.confirmButton);
        }
        this.confirmButton = new FramedButton(0, 0, 120, 20, "Подтвердить", 0xFF00FF00, 0xFFFFFFFF,
                b -> {
                    if (isQuestUnlocked() && hasRequiredItems() && !isQuestCompleted()) {
                        markCompleted();
                    }
                });
        this.addButton(this.confirmButton);

        int contentTop = panelY + 50;
        int contentBottomPadding = CONFIRM_BUTTON_SPACING + this.confirmButton.getHeight() + CONFIRM_BUTTON_BOTTOM_MARGIN;
        int availableHeight = panelHeight - (contentTop - panelY) - contentBottomPadding;
        int availableWidth = panelWidth - CONTENT_MARGIN * 2;
        int leftWidth = (int) Math.round(availableWidth * 0.55);
        int rightWidth = availableWidth - leftWidth - COLUMN_GAP;
        if (rightWidth < 80) {
            rightWidth = 80;
            leftWidth = availableWidth - rightWidth - COLUMN_GAP;
        }

        int descriptionX = panelX + CONTENT_MARGIN;
        int descriptionY = contentTop;
        this.descriptionArea = new ScrollArea(descriptionX, descriptionY, leftWidth, availableHeight, panelX, panelY, panelWidth, panelHeight, false, false);

        int goalsX = descriptionX + leftWidth + COLUMN_GAP;
        int goalsY = contentTop;
        int goalsHeight = (availableHeight - COLUMN_GAP - INSTRUCTIONS_VERTICAL_SHIFT) / 2;
        if (goalsHeight < 40) {
            goalsHeight = 40;
        }
        int instructionsY = goalsY + goalsHeight + COLUMN_GAP + INSTRUCTIONS_VERTICAL_SHIFT;
        int instructionsHeight = availableHeight - goalsHeight - COLUMN_GAP - INSTRUCTIONS_VERTICAL_SHIFT;
        if (instructionsHeight < 40) {
            instructionsHeight = 40;
            instructionsY = goalsY + availableHeight - instructionsHeight;
        }
        this.goalsArea = new ScrollArea(goalsX, goalsY, rightWidth, goalsHeight, panelX, panelY, panelWidth, panelHeight, false, false);
        this.instructionsArea = new ScrollArea(goalsX, instructionsY, rightWidth, instructionsHeight, panelX, panelY, panelWidth, panelHeight, false, false);

        int confirmY = panelY + panelHeight - CONFIRM_BUTTON_BOTTOM_MARGIN - this.confirmButton.getHeight();
        this.confirmButton.x = panelX + (panelWidth - this.confirmButton.getWidth()) / 2;
        this.confirmButton.y = confirmY;
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(ms);
        hoveredStack = ItemStack.EMPTY;
        GuiUtil.drawPanel(ms, panelX, panelY, panelWidth, panelHeight);

        drawTitle(ms, panelX + panelWidth / 2, panelY + TITLE_OFFSET_Y);

        drawSectionLabel(ms, "Инструкция", descriptionArea.getX(), descriptionArea.getY() - 18);
        descriptionArea.render(ms, mouseX, mouseY, partialTicks,
                (area, matrix, x, y, innerWidth, mX, mY, pt) -> renderInstructions(area, matrix, x, y, innerWidth, mX, mY, pt));

        drawSectionLabel(ms, "Цель", goalsArea.getX(), goalsArea.getY() - 18);
        goalsArea.render(ms, mouseX, mouseY, partialTicks,
                (area, matrix, x, y, innerWidth, mX, mY, pt) -> renderGoals(area, matrix, x, y, innerWidth, mX, mY, pt));

        drawSectionLabel(ms, "Описание", instructionsArea.getX(), instructionsArea.getY() - 18);
        instructionsArea.render(ms, mouseX, mouseY, partialTicks,
                (area, matrix, x, y, innerWidth, mX, mY, pt) -> renderDescription(area, matrix, x, y, innerWidth, mX, mY, pt));

        updateConfirmButtonState();

        super.render(ms, mouseX, mouseY, partialTicks);

        renderAdditional(ms, mouseX, mouseY, partialTicks);
    }

    protected void renderAdditional(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
    }

    private void updateConfirmButtonState() {
        boolean unlocked = isQuestUnlocked();
        boolean completed = isQuestCompleted();
        boolean hasItems = hasRequiredItems();
        // Кнопка видна только когда квест разблокирован, не выполнен И есть все необходимые предметы
        this.confirmButton.visible = unlocked && !completed && hasItems;
        this.confirmButton.active = unlocked && hasItems;
    }

    protected void drawTitle(MatrixStack ms, int centerX, int y) {
        String title = this.title.getString();
        int titleHeight = (int) Math.ceil(this.font.lineHeight * TITLE_SCALE);
        int titleY = y - titleHeight;
        ms.pushPose();
        ms.scale(TITLE_SCALE, TITLE_SCALE, TITLE_SCALE);
        drawCenteredString(ms, this.font, title, (int) (centerX / (float) TITLE_SCALE),
                (int) (titleY / (float) TITLE_SCALE), 0xFF00BFFF);
        ms.popPose();
        if (isQuestCompleted()) {
            int titleWidth = this.font.width(title) * TITLE_SCALE;
            drawString(ms, this.font, " (Выполнено)", centerX + titleWidth / 2 + 5, titleY, 0xFF00FF00);
        }
    }

    private void drawSectionLabel(MatrixStack ms, String text, int x, int y) {
        drawScaledUnderlined(ms, text, x, y, 0xFFFFFFFF, SECTION_LABEL_SCALE);
    }

    protected void drawScaledUnderlined(MatrixStack ms, String text, int x, int y, int color, float scale) {
        ms.pushPose();
        ms.scale(scale, scale, scale);
        float inv = 1.0F / scale;
        this.font.draw(ms, text, x * inv, y * inv, color);
        ms.popPose();
        int width = (int) (this.font.width(text) * scale);
        int underlineY = (int) (y + this.font.lineHeight * scale);
        fill(ms, x, underlineY, x + width, underlineY + 1, color);
    }

    protected int drawParagraph(MatrixStack ms, int x, int y, int innerWidth, String text, int color) {
        if (text == null || text.isEmpty()) {
            return y;
        }
        if (innerWidth <= 0) {
            this.font.draw(ms, text, x, y, color);
            return y + this.font.lineHeight + LINE_SPACING;
        }

        List<String> wrapped = wrapText(text, innerWidth);
        int lineHeight = this.font.lineHeight;
        for (int i = 0; i < wrapped.size(); i++) {
            String line = wrapped.get(i);
            boolean lastLine = i == wrapped.size() - 1;
            if (!lastLine && shouldJustify(line)) {
                drawJustifiedLine(ms, x, y, innerWidth, line, color);
            } else {
                this.font.draw(ms, line, x, y, color);
            }
            y += lineHeight + LINE_SPACING;
        }
        return y;
    }

    private boolean shouldJustify(String line) {
        if (line == null) {
            return false;
        }
        int firstSpace = line.indexOf(' ');
        int lastSpace = line.lastIndexOf(' ');
        return firstSpace != -1 && firstSpace != lastSpace;
    }

    private void drawJustifiedLine(MatrixStack ms, int x, int y, int innerWidth, String line, int color) {
        String[] words = line.split(" ");
        int wordCount = 0;
        int wordsWidth = 0;
        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }
            wordsWidth += this.font.width(word);
            wordCount++;
        }
        if (wordCount <= 1) {
            this.font.draw(ms, line.trim(), x, y, color);
            return;
        }

        int spaceWidth = this.font.width(" ");
        int gaps = wordCount - 1;
        int totalBaseSpace = spaceWidth * gaps;
        int extra = innerWidth - wordsWidth - totalBaseSpace;
        if (extra <= 0) {
            this.font.draw(ms, line, x, y, color);
            return;
        }

        float extraPerGap = extra / (float) gaps;
        float carry = 0.0f;
        float currentX = x;
        int rendered = 0;
        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }
            this.font.draw(ms, word, currentX, y, color);
            currentX += this.font.width(word);
            rendered++;
            if (rendered >= wordCount) {
                break;
            }
            float spacing = spaceWidth + extraPerGap;
            int spacingInt = spaceWidth;
            if (spacing > spaceWidth) {
                carry += spacing - spaceWidth;
                spacingInt += (int) carry;
                carry -= (int) carry;
            }
            currentX += spacingInt;
        }
    }

    private List<String> wrapText(String text, int innerWidth) {
        List<String> lines = new ArrayList<>();
        int spaceWidth = this.font.width(" ");
        for (String paragraph : text.split("\\n")) {
            if (paragraph.isEmpty()) {
                lines.add("");
                continue;
            }
            String[] words = paragraph.split(" ");
            StringBuilder current = new StringBuilder();
            int currentWidth = 0;
            for (String word : words) {
                if (word.isEmpty()) {
                    continue;
                }
                int wordWidth = this.font.width(word);
                if (current.length() == 0) {
                    current.append(word);
                    currentWidth = wordWidth;
                    continue;
                }
                int projected = currentWidth + spaceWidth + wordWidth;
                if (projected <= innerWidth) {
                    current.append(' ').append(word);
                    currentWidth = projected;
                } else {
                    lines.add(current.toString());
                    current.setLength(0);
                    if (wordWidth > innerWidth) {
                        lines.add(word);
                        currentWidth = 0;
                    } else {
                        current.append(word);
                        currentWidth = wordWidth;
                    }
                }
            }
            if (current.length() > 0) {
                lines.add(current.toString());
            }
        }
        if (lines.isEmpty()) {
            lines.add("");
        }
        return lines;
    }

    protected abstract int renderDescription(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                             int mouseX, int mouseY, float partialTicks);

    protected abstract int renderGoals(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                       int mouseX, int mouseY, float partialTicks);

    protected abstract int renderInstructions(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                              int mouseX, int mouseY, float partialTicks);

    protected abstract boolean hasRequiredItems();

    protected boolean isQuestUnlocked() {
        return true;
    }

    protected abstract boolean isQuestCompleted();

    protected abstract void markCompleted();

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (descriptionArea.mouseScrolled(mouseX, mouseY, delta)) {
            return true;
        }
        if (goalsArea.mouseScrolled(mouseX, mouseY, delta)) {
            return true;
        }
        if (instructionsArea.mouseScrolled(mouseX, mouseY, delta)) {
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (descriptionArea.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (goalsArea.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (instructionsArea.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
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
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (descriptionArea.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
            return true;
        }
        if (goalsArea.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
            return true;
        }
        if (instructionsArea.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        boolean handled = descriptionArea.mouseReleased(mouseX, mouseY, button)
                | goalsArea.mouseReleased(mouseX, mouseY, button)
                | instructionsArea.mouseReleased(mouseX, mouseY, button);
        return handled || super.mouseReleased(mouseX, mouseY, button);
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
}