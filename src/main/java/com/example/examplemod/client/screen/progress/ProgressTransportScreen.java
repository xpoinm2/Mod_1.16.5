package com.example.examplemod.client.screen.progress;

import com.example.examplemod.client.FramedButton;
import com.example.examplemod.client.GuiUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ProgressTransportScreen extends Screen {
    private static final int PANEL_MARGIN = 10;
    private static final int MAP_PADDING = 15;

    private final Screen parent;

    private final List<QuestNode> nodes = new ArrayList<>();
    private final List<QuestConnection> connections = new ArrayList<>();

    private int offsetX;
    private int offsetY;
    private boolean panning;
    private int mapLeft;
    private int mapTop;
    private int mapRight;
    private int mapBottom;

    private enum QuestState {
        LOCKED,
        AVAILABLE,
        COMPLETED
    }

    private static class QuestNode {
        final FramedButton button;
        final int baseX;
        final int baseY;

        QuestNode(FramedButton button, int baseX, int baseY) {
            this.button = button;
            this.baseX = baseX;
            this.baseY = baseY;
        }
    }

    private static class QuestConnection {
        final QuestNode from;
        final QuestNode to;
        final java.util.function.Supplier<QuestState> toStateSupplier;

        QuestConnection(QuestNode from, QuestNode to, java.util.function.Supplier<QuestState> toStateSupplier) {
            this.from = from;
            this.to = to;
            this.toStateSupplier = toStateSupplier;
        }
    }

    public ProgressTransportScreen(Screen parent) {
        super(new StringTextComponent("Транспорт"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.addButton(new FramedButton(5, 5, 20, 20, "<", 0xFFFFFF00, 0xFFFFFFFF,
                b -> this.minecraft.setScreen(parent)));

        nodes.clear();
        connections.clear();

        // TODO: Добавить квесты транспорта

        updateNodePositions();
        updateMapBounds();
        super.init();
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(ms);
        updateMapBounds();
        GuiUtil.drawPanel(ms, PANEL_MARGIN, PANEL_MARGIN, this.width - PANEL_MARGIN * 2, this.height - PANEL_MARGIN * 2);
        drawCenteredString(ms, this.font, this.title, this.width / 2, 30, 0xFF00FFFF);

        updateNodePositions();

        // Пока нет кнопок, поэтому ничего не обновляем

        renderConnections(ms);

        super.render(ms, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 1 && isInMap(mouseX, mouseY)) {
            panning = true;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (panning && button == 1) {
            offsetX += (int) Math.round(dragX);
            offsetY += (int) Math.round(dragY);
            updateNodePositions();
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 1 && panning) {
            panning = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private QuestNode registerNode(FramedButton button, int baseX, int baseY) {
        QuestNode node = new QuestNode(button, baseX, baseY);
        nodes.add(node);
        this.addButton(button);
        return node;
    }

    private void addConnection(QuestNode from, QuestNode to, java.util.function.Supplier<QuestState> toStateSupplier) {
        connections.add(new QuestConnection(from, to, toStateSupplier));
    }

    private void updateNodePositions() {
        for (QuestNode node : nodes) {
            node.button.x = node.baseX + offsetX;
            node.button.y = node.baseY + offsetY;
        }
    }

    private void updateMapBounds() {
        mapLeft = PANEL_MARGIN + MAP_PADDING;
        mapRight = this.width - PANEL_MARGIN - MAP_PADDING;
        mapTop = PANEL_MARGIN + 45;
        mapBottom = this.height - PANEL_MARGIN - MAP_PADDING;
    }

    private boolean isInMap(double mouseX, double mouseY) {
        return mouseX >= mapLeft && mouseX <= mapRight && mouseY >= mapTop && mouseY <= mapBottom;
    }

    private void renderConnections(MatrixStack ms) {
        for (QuestConnection connection : connections) {
            QuestState state = connection.toStateSupplier.get();
            int color = state == QuestState.LOCKED ? 0xFF7F7F7F : 0xFFFFFFFF;
            FramedButton fromButton = connection.from.button;
            FramedButton toButton = connection.to.button;
            int x1 = fromButton.x + fromButton.getWidth() / 2;
            int y1 = fromButton.y + fromButton.getHeight() / 2;
            int x2 = toButton.x + toButton.getWidth() / 2;
            int y2 = toButton.y + toButton.getHeight() / 2;
            if (x1 != x2) {
                net.minecraft.client.gui.AbstractGui.fill(ms, Math.min(x1, x2), y1, Math.max(x1, x2), y1 + 1, color);
            }
            if (y1 != y2) {
                net.minecraft.client.gui.AbstractGui.fill(ms, x2, Math.min(y1, y2), x2 + 1, Math.max(y1, y2), color);
            }
        }
    }
}
