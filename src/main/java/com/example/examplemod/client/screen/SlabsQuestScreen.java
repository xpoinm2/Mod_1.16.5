package com.example.examplemod.client.screen;

import com.example.examplemod.client.FramedButton;
import com.example.examplemod.quest.QuestManager;
import com.example.examplemod.client.GuiUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.glfw.GLFW;
import com.example.examplemod.client.GuiUtil;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SlabsQuestScreen extends Screen {
    private final Screen parent;
    private int scrollOffset = 0;
    private int maxScroll = 0;
    private ItemStack hoveredStack = ItemStack.EMPTY;
    private FramedButton confirmButton;

    public SlabsQuestScreen(Screen parent) {
        super(new StringTextComponent("Полублоки"));
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
                    if (hasRequiredItems()) {
                        QuestManager.setSlabsCompleted(true);
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
        int height = this.height - 20;
        GuiUtil.drawPanel(ms, x0, y0, width, height);
        boolean unlocked = QuestManager.isPlanksCompleted();
        this.confirmButton.visible = unlocked && !QuestManager.isSlabsCompleted();
        drawTitle(ms, x0 + width / 2, y0 + 15);

        int leftX = x0 + 20;
        int leftY = y0 + 40 - scrollOffset;
        drawScaledUnderlined(ms, "Описание", leftX, leftY, 0xFFFFFFFF, 4f/3f);
        leftY += 30;
        drawString(ms, this.font, "Их досок делали доски поменьше,", leftX, leftY, 0xFFFFFF00);
        leftY += 10;
        drawString(ms, this.font, "просто разрубая их.", leftX, leftY, 0xFFFFFF00);

        int rightX = x0 + width / 2 + 20;
        int rightY = y0 + 40 - scrollOffset;
        drawScaledUnderlined(ms, "Цель", rightX, rightY, 0xFFFFFFFF, 4f/3f);
        rightY += 30;
        drawString(ms, this.font, "Нужно получить 6 полублоков", rightX, rightY, 0xFFFFFF00);
        ItemStack stack = new ItemStack(Items.OAK_SLAB, 6);
        if (GuiUtil.renderItemWithTooltip(this, ms, stack, rightX, rightY + 10, mouseX, mouseY)) {
            hoveredStack = stack;
        }
        rightY += 40;
        drawScaledUnderlined(ms, "Инструкция", rightX, rightY, 0xFFFFFFFF, 4f/3f);
        rightY += 30;
        drawString(ms, this.font, "В любой слот топор и доски", rightX, rightY, 0xFFFFFF00);
        int contentBottom = Math.max(leftY, rightY);
        maxScroll = Math.max(0, contentBottom - (y0 + height - 10));
        super.render(ms, mouseX, mouseY, pt);
    }

    private boolean hasRequiredItems() {
        return this.minecraft.player != null &&
                this.minecraft.player.inventory.countItem(Items.OAK_SLAB) >= 6;
    }

    private void drawTitle(MatrixStack ms, int centerX, int y) {
        String title = this.title.getString();
        ms.pushPose();
        ms.scale(2.0F, 2.0F, 2.0F);
        drawCenteredString(ms, this.font, title, (int) (centerX / 2f), (int) (y / 2f), 0xFF00BFFF);
        ms.popPose();
        if (QuestManager.isSlabsCompleted()) {
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
