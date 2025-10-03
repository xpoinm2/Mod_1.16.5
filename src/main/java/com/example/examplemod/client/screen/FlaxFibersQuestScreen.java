package com.example.examplemod.client.screen;

import com.example.examplemod.ModItems;
import com.example.examplemod.client.FramedButton;
import com.example.examplemod.client.GuiUtil;
import com.example.examplemod.quest.QuestManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.glfw.GLFW;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FlaxFibersQuestScreen extends Screen {
    private static final int LAYOUT_SHIFT = 10;
    private static final int CONFIRM_BUTTON_BOTTOM_MARGIN = 5;
    private static final float SECTION_LABEL_SCALE = 1.15f;
    private final Screen parent;
    private int scrollOffset = 0;
    private int maxScroll = 0;
    private int contentHeight = 0;
    private int viewHeight = 0;
    private int contentStart = 0;
    private int scrollbarX = 0;
    private int scrollbarY = 0;
    private int scrollbarHeight = 0;
    private boolean draggingScrollbar = false;
    private ItemStack hoveredStack = ItemStack.EMPTY;
    private FramedButton confirmButton;

    public FlaxFibersQuestScreen(Screen parent) {
        super(new StringTextComponent("Волокна льна"));
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
                        QuestManager.setFlaxFibersCompleted(true);
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
        int height = this.height - 60 + LAYOUT_SHIFT;
        GuiUtil.drawPanel(ms, x0, y0, width, height);
        this.confirmButton.visible = !QuestManager.isFlaxFibersCompleted();
        drawTitle(ms, x0 + width / 2, y0 + 15);

        int leftX = x0 + 20;
        int leftY = y0 + 40 - scrollOffset;
        drawSectionLabel(ms, "Описание", leftX, leftY);
        leftY += 30;
        drawString(ms, this.font, "Лён нужен в", leftX, leftY, 0xFFFFFF00);
        leftY += 10;
        drawString(ms, this.font, "изготовлении множества", leftX, leftY, 0xFFFFFF00);
        leftY += 10;
        drawString(ms, this.font, "предметов.", leftX, leftY, 0xFFFFFF00);

        int rightX = x0 + width / 2 + 10;
        int rightY = y0 + 40 - scrollOffset;
        drawSectionLabel(ms, "Цель", rightX, rightY);
        rightY += 30;
        drawString(ms, this.font, "Сделать 10 волокон", rightX, rightY, 0xFFFFFF00);
        rightY += 10;
        drawString(ms, this.font, "льна", rightX, rightY, 0xFFFFFF00);
        ItemStack stack = new ItemStack(ModItems.FLAX_FIBERS.get(), 10);
        if (GuiUtil.renderItemWithTooltip(this, ms, stack, rightX, rightY + 10, mouseX, mouseY)) {
            hoveredStack = stack;
        }
        rightY += 40 + LAYOUT_SHIFT;
        drawSectionLabel(ms, "Инструкция", rightX, rightY);
        rightY += 30;
        String instruction = "Лён растение. Растет в биомах равнин, лесов и болот. Замочите лён в воде. " +
                "Вымоченный лён повесьте на листву. Через 2 минуты он высохнет. Гребнем соберите волокна.";
        int rightWidth = width - (rightX - x0) - 20;
        for (IReorderingProcessor line : this.font.split(new StringTextComponent(instruction), rightWidth)) {
            this.font.draw(ms, line, rightX, rightY, 0xFFFFFF00);
            rightY += 10;
        }
        int contentBottom = Math.max(leftY, rightY);
        contentStart = y0 + 40;
        viewHeight = height - 50;
        contentHeight = contentBottom - contentStart;
        maxScroll = Math.max(0, contentHeight - viewHeight);
        if (maxScroll > 0) {
            int trackHeight = viewHeight;
            scrollbarHeight = Math.max(20, trackHeight * viewHeight / contentHeight);
            scrollbarX = x0 + width - 8;
            scrollbarY = contentStart + (scrollOffset * (trackHeight - scrollbarHeight)) / maxScroll;
        }
        super.render(ms, mouseX, mouseY, pt);
        if (maxScroll > 0) {
            fill(ms, scrollbarX, scrollbarY, scrollbarX + 4, scrollbarY + scrollbarHeight, 0xFFFFFFFF);
        }
    }

    private boolean hasRequiredItems() {
        return this.minecraft.player != null &&
                this.minecraft.player.inventory.countItem(ModItems.FLAX_FIBERS.get()) >= 10;
    }

    private void drawTitle(MatrixStack ms, int centerX, int y) {
        String title = this.title.getString();
        ms.pushPose();
        ms.scale(2.0F, 2.0F, 2.0F);
        drawCenteredString(ms, this.font, title, (int) (centerX / 2f), (int) (y / 2f), 0xFF00BFFF);
        ms.popPose();
        if (QuestManager.isFlaxFibersCompleted()) {
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
        if (maxScroll > 0 && mouseX >= scrollbarX && mouseX <= scrollbarX + 4 &&
                mouseY >= scrollbarY && mouseY <= scrollbarY + scrollbarHeight) {
            draggingScrollbar = true;
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

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (draggingScrollbar) {
            int trackHeight = viewHeight - scrollbarHeight;
            int y = (int) Math.max(0, Math.min(trackHeight, mouseY - contentStart));
            scrollOffset = (int) ((y * (double) maxScroll) / trackHeight);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (draggingScrollbar) {
            draggingScrollbar = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }
}