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
public class PureIronOreQuestScreen extends Screen {
    private static final int LAYOUT_SHIFT = 10;
    private static final int CONFIRM_BUTTON_BOTTOM_MARGIN = 5;
    private static final float SECTION_LABEL_SCALE = 1.15f;
    private final Screen parent;
    private int scrollOffset = 0;
    private int maxScroll = 0;
    private ItemStack hoveredStack = ItemStack.EMPTY;
    private FramedButton confirmButton;

    public PureIronOreQuestScreen(Screen parent) {
        super(new StringTextComponent("Чистая железная руда"));
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
        int btnY = this.height - btnHeight - CONFIRM_BUTTON_BOTTOM_MARGIN;
        this.confirmButton = new FramedButton(btnX, btnY, btnWidth, btnHeight, "Подтвердить", 0xFF00FF00, 0xFFFFFFFF,
                b -> {
                    if (hasRequiredItems()) {
                        QuestManager.setPureIronOreCompleted(true);
                    }
                });
        this.addButton(this.confirmButton);
        super.init();
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float pt) {
        this.renderBackground(ms);
        hoveredStack = ItemStack.EMPTY;
        int x0 = 10;
        int y0 = 10;
        int width = this.width - 20;
        int height = this.height - 20 + LAYOUT_SHIFT;
        GuiUtil.drawPanel(ms, x0, y0, width, height);
        boolean unlocked = QuestManager.isIronClusterCompleted();
        this.confirmButton.visible = unlocked && !QuestManager.isPureIronOreCompleted();
        drawTitle(ms, x0 + width / 2, y0 + 15);

        int leftX = x0 + 20;
        int leftY = y0 + 40 - scrollOffset;
        drawSectionLabel(ms, "Описание", leftX, leftY);
        leftY += 30;
        drawString(ms, this.font, "Сортировка и обогащение:", leftX, leftY, 0xFFFFFF00);
        leftY += 10;
        drawString(ms, this.font, "после выемки руду", leftX, leftY, 0xFFFFFF00);
        leftY += 10;
        drawString(ms, this.font, "сортировали на площадках,", leftX, leftY, 0xFFFFFF00);
        leftY += 10;
        drawString(ms, this.font, "мыли в водотоках", leftX, leftY, 0xFFFFFF00);
        leftY += 10;
        drawString(ms, this.font, "для отделения примесей.", leftX, leftY, 0xFFFFFF00);

        int rightX = x0 + width / 2 + 20;
        int rightY = y0 + 40 - scrollOffset;
        drawSectionLabel(ms, "Цель", rightX, rightY);
        rightY += 30;
        drawString(ms, this.font, "Создать 8", rightX, rightY, 0xFFFFFF00);
        rightY += 10;
        drawString(ms, this.font, "чистой железной руды", rightX, rightY, 0xFFFFFF00);
        ItemStack stack = new ItemStack(ModItems.PURE_IRON_ORE.get(), 8);
        if (GuiUtil.renderItemWithTooltip(this, ms, stack, rightX, rightY + 10, mouseX, mouseY)) {
            hoveredStack = stack;
        }
        rightY += 40 + LAYOUT_SHIFT;
        drawSectionLabel(ms, "Инструкция", rightX, rightY);
        rightY += 30;
        drawString(ms, this.font, "Нужно железный кластер", rightX, rightY, 0xFFFFFF00);
        rightY += 10;
        drawString(ms, this.font, "помыть в воде", rightX, rightY, 0xFFFFFF00);

        int contentBottom = Math.max(leftY, rightY);
        maxScroll = Math.max(0, contentBottom - (y0 + height - 10));
        super.render(ms, mouseX, mouseY, pt);
    }

    private boolean hasRequiredItems() {
        return this.minecraft.player != null &&
                this.minecraft.player.inventory.countItem(ModItems.PURE_IRON_ORE.get()) >= 8;
    }

    private void drawTitle(MatrixStack ms, int centerX, int y) {
        String title = this.title.getString();
        ms.pushPose();
        ms.scale(2.0F, 2.0F, 2.0F);
        drawCenteredString(ms, this.font, title, (int) (centerX / 2f), (int) (y / 2f), 0xFF00BFFF);
        ms.popPose();
        if (QuestManager.isPureIronOreCompleted()) {
            int titleWidth = this.font.width(title) * 2;
            drawString(ms, this.font, " (Выполнено)", centerX + titleWidth / 2 + 5, y, 0xFF00FF00);
        }
    }

    private void drawSectionLabel(MatrixStack ms, String text, int x, int y) {
        drawScaledUnderlined(ms, text, x, y, 0xFFFFFFFF, SECTION_LABEL_SCALE);
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