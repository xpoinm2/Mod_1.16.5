package com.example.examplemod.client.screen;

import com.example.examplemod.client.GuiUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;

/**
 * Helper for rendering scrollable panels inside quest screens.
 */
public class ScrollArea {
    private static final int PADDING = 6;
    private static final int SCROLLBAR_WIDTH = 6;
    private static final int SCROLLBAR_GAP = 4;

    private final int x;
    private final int y;
    private final int width;
    private final int height;

    private int scrollOffset;
    private int contentHeight;
    private int maxScroll;

    private int knobTop;
    private int knobHeight;
    private boolean dragging;
    private double dragStartMouseY;
    private int dragStartScroll;

    public ScrollArea(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getViewportX() {
        return x + PADDING;
    }

    public int getViewportY() {
        return y + PADDING;
    }

    public int getViewportWidth() {
        return width - PADDING * 2 - SCROLLBAR_WIDTH - SCROLLBAR_GAP;
    }

    public int getViewportHeight() {
        return height - PADDING * 2;
    }

    public boolean isPointInsideViewport(double mouseX, double mouseY) {
        int vx = getViewportX();
        int vy = getViewportY();
        return mouseX >= vx && mouseX <= vx + getViewportWidth()
                && mouseY >= vy && mouseY <= vy + getViewportHeight();
    }

    public boolean isVisible(int rectX, int rectY, int rectWidth, int rectHeight) {
        int vx = getViewportX();
        int vy = getViewportY();
        int vw = getViewportWidth();
        int vh = getViewportHeight();
        return rectX < vx + vw && rectX + rectWidth > vx && rectY < vy + vh && rectY + rectHeight > vy;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (!isInside(mouseX, mouseY)) {
            return false;
        }
        if (maxScroll <= 0 || delta == 0) {
            return maxScroll > 0;
        }
        setScrollOffset(scrollOffset - (int) (delta * 10));
        return true;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0 || maxScroll <= 0) {
            return false;
        }
        int scrollbarX0 = getScrollbarX0();
        int scrollbarX1 = scrollbarX0 + SCROLLBAR_WIDTH;
        if (mouseX < scrollbarX0 || mouseX > scrollbarX1) {
            return false;
        }
        if (mouseY >= knobTop && mouseY <= knobTop + knobHeight) {
            dragging = true;
            dragStartMouseY = mouseY;
            dragStartScroll = scrollOffset;
            return true;
        }
        // Click on track jumps towards position
        if (mouseY < knobTop) {
            setScrollOffset(scrollOffset - getViewportHeight());
        } else if (mouseY > knobTop + knobHeight) {
            setScrollOffset(scrollOffset + getViewportHeight());
        }
        return true;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (!dragging) {
            return false;
        }
        int scrollable = getViewportHeight() - knobHeight;
        if (scrollable <= 0) {
            return true;
        }
        double delta = mouseY - dragStartMouseY;
        double ratio = delta / scrollable;
        setScrollOffset(dragStartScroll + (int) Math.round(ratio * maxScroll));
        return true;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (dragging && button == 0) {
            dragging = false;
            return true;
        }
        return false;
    }

    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks, Renderer renderer) {
        GuiUtil.drawPanel(ms, x, y, width, height);

        int viewportX = getViewportX();
        int viewportY = getViewportY();
        int viewportWidth = Math.max(0, getViewportWidth());
        int viewportHeight = Math.max(0, getViewportHeight());
        if (viewportWidth <= 0 || viewportHeight <= 0) {
            contentHeight = 0;
            maxScroll = 0;
            knobTop = viewportY;
            knobHeight = viewportHeight;
            return;
        }

        int contentStartY = viewportY - scrollOffset;
        enableScissor(viewportX, viewportY, viewportWidth, viewportHeight);
        int contentBottom = renderer.render(this, ms, viewportX, contentStartY, viewportWidth, mouseX, mouseY, partialTicks);
        disableScissor();

        contentHeight = Math.max(0, contentBottom - contentStartY);
        maxScroll = Math.max(0, contentHeight - viewportHeight);
        if (scrollOffset > maxScroll) {
            scrollOffset = maxScroll;
        }
        drawScrollbar(ms, viewportX, viewportY, viewportHeight);
    }

    private void drawScrollbar(MatrixStack ms, int viewportX, int viewportY, int viewportHeight) {
        int scrollbarX0 = getScrollbarX0();
        int scrollbarX1 = scrollbarX0 + SCROLLBAR_WIDTH;
        AbstractGui.fill(ms, scrollbarX0, viewportY, scrollbarX1, viewportY + viewportHeight, 0x66000000);
        if (maxScroll <= 0) {
            knobTop = viewportY;
            knobHeight = viewportHeight;
            AbstractGui.fill(ms, scrollbarX0, knobTop, scrollbarX1, knobTop + knobHeight, 0x99FFFFFF);
            return;
        }
        int minKnob = Math.max(10, viewportHeight / 6);
        knobHeight = Math.max(minKnob, (int) ((long) viewportHeight * viewportHeight / contentHeight));
        if (knobHeight > viewportHeight) {
            knobHeight = viewportHeight;
        }
        int scrollable = viewportHeight - knobHeight;
        knobTop = viewportY + (scrollable <= 0 ? 0 : (int) ((long) scrollOffset * scrollable / maxScroll));
        AbstractGui.fill(ms, scrollbarX0, knobTop, scrollbarX1, knobTop + knobHeight, dragging ? 0xCCFFFFFF : 0xAAFFFFFF);
    }

    private int getScrollbarX0() {
        return x + width - PADDING - SCROLLBAR_WIDTH;
    }

    private void setScrollOffset(int value) {
        scrollOffset = Math.max(0, Math.min(maxScroll, value));
    }

    private boolean isInside(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    private static void enableScissor(int x, int y, int width, int height) {
        Minecraft mc = Minecraft.getInstance();
        double scale = mc.getWindow().getGuiScale();
        int scissorX = (int) Math.round(x * scale);
        int scissorY = (int) Math.round(mc.getWindow().getScreenHeight() - (y + height) * scale);
        int scissorW = (int) Math.round(width * scale);
        int scissorH = (int) Math.round(height * scale);
        if (scissorW <= 0 || scissorH <= 0) {
            return;
        }
        RenderSystem.enableScissor(scissorX, scissorY, scissorW, scissorH);
    }

    private static void disableScissor() {
        RenderSystem.disableScissor();
    }

    @FunctionalInterface
    public interface Renderer {
        int render(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                   int mouseX, int mouseY, float partialTicks);
    }
}
