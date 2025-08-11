package com.example.examplemod.client.screen;

import com.example.examplemod.client.FramedButton;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Items;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PlanksQuestScreen extends Screen {
    private final Screen parent;
    private static boolean completed = false;

    public PlanksQuestScreen(Screen parent) {
        super(new StringTextComponent("Доски"));
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
        this.addButton(new FramedButton(btnX, btnY, btnWidth, btnHeight, "Подтвердить", 0xFF00FF00, 0xFFFFFFFF,
                b -> {
                    if (hasRequiredItems()) {
                        completed = true;
                    }
                }));
        super.init();
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float pt) {
        this.renderBackground(ms);
        int x0 = 10;
        int y0 = 10;
        int width = this.width - 20;
        int height = this.height - 20;
        fill(ms, x0 - 1, y0 - 1, x0 + width + 1, y0 + height + 1, 0xFF000000);
        fill(ms, x0, y0, x0 + width, y0 + height, 0xFF000000);
        drawTitle(ms, x0 + width / 2, y0 + 15);

        int leftX = x0 + 20;
        int leftY = y0 + 40;
        drawScaledUnderlined(ms, "Описание", leftX, leftY, 0xFFFFFFFF, 2.0F);
        leftY += 30;
        drawString(ms, this.font, "Люди работали топорами, чтобы", leftX, leftY, 0xFFFFFFFF);
        leftY += 10;
        drawString(ms, this.font, "разделывать бревна.", leftX, leftY, 0xFFFFFFFF);

        int rightX = x0 + width / 2 + 20;
        int rightY = y0 + 40;
        drawScaledUnderlined(ms, "Цель", rightX, rightY, 0xFFFFFFFF, 2.0F);
        rightY += 30;
        drawString(ms, this.font, "Нужно получить 4 доски", rightX, rightY, 0xFFFFFF00);
        rightY += 25;
        drawScaledUnderlined(ms, "Инструкция", rightX, rightY, 0xFFFFFFFF, 2.0F);
        rightY += 30;
        drawString(ms, this.font, "Крафт досок через топор", rightX, rightY, 0xFFFFFF00);
        super.render(ms, mouseX, mouseY, pt);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }
    private boolean hasRequiredItems() {
        return this.minecraft.player != null &&
                this.minecraft.player.inventory.countItem(Items.OAK_PLANKS) >= 4;
    }

    private void drawTitle(MatrixStack ms, int centerX, int y) {
        String title = this.title.getString();
        ms.pushPose();
        ms.scale(3.0F, 3.0F, 3.0F);
        drawCenteredString(ms, this.font, title, (int) (centerX / 3f), (int) (y / 3f), 0xFF00BFFF);
        ms.popPose();
        if (completed) {
            int titleWidth = this.font.width(title) * 3;
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

    public static boolean isCompleted() {
        return completed;
    }
}