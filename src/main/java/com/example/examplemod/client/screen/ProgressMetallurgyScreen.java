package com.example.examplemod.client.screen;

import com.example.examplemod.ModItems;
import com.example.examplemod.client.FramedButton;
import com.example.examplemod.client.GuiUtil;
import com.example.examplemod.client.ItemIconButton;
import com.example.examplemod.quest.QuestManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class ProgressMetallurgyScreen extends Screen {
    private static final int PANEL_MARGIN = 10;
    private static final int MAP_PADDING = 15;

    private final Screen parent;

    private final List<QuestNode> nodes = new ArrayList<>();
    private final List<QuestConnection> connections = new ArrayList<>();

    private ItemIconButton startSmithingButton;
    private ItemIconButton ironClusterButton;
    private ItemIconButton pureIronOreButton;
    private ItemIconButton firepitButton;
    private ItemIconButton pyriteButton;
    private ItemIconButton pyriteFlintButton;
    private ItemIconButton calcinedIronOreButton;


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
        final ItemIconButton button;
        final int baseX;
        final int baseY;

        QuestNode(ItemIconButton button, int baseX, int baseY) {
            this.button = button;
            this.baseX = baseX;
            this.baseY = baseY;
        }
    }

    private static class QuestConnection {
        final QuestNode from;
        final QuestNode to;
        final Supplier<QuestState> toStateSupplier;

        QuestConnection(QuestNode from, QuestNode to, Supplier<QuestState> toStateSupplier) {
            this.from = from;
            this.to = to;
            this.toStateSupplier = toStateSupplier;
        }
    }

    public ProgressMetallurgyScreen(Screen parent) {
        super(new StringTextComponent("Металлургия"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.addButton(new FramedButton(5, 5, 20, 20, "<", 0xFFFFFF00, 0xFFFFFFFF,
                b -> this.minecraft.setScreen(parent)));
                nodes.clear();
        connections.clear();

        // Only show quests if coming from Ancient World era, not Ancient Metallurgy era
        if (parent instanceof ProgressEraScreen) {
            initQuests();
        }

        updateNodePositions();
        updateMapBounds();
        super.init();
    }

    private void initQuests() {
        int baseX = 80;
        int baseY = 90;
        int spacingX = 70;
        int spacingY = 60;

        this.startSmithingButton = new ItemIconButton(baseX, baseY,
                new ItemStack(ModItems.IMPURE_IRON_ORE.get()),
                b -> this.minecraft.setScreen(new StartSmithingQuestScreen(this)),
                () -> Arrays.asList(
                        new StringTextComponent("Начало кузнечного дела")
                                .withStyle(TextFormatting.BLUE, TextFormatting.UNDERLINE),
                        new StringTextComponent("Требуется: ")
                                .append(new StringTextComponent("Каменные или костяные инструменты")
                                        .withStyle(TextFormatting.BLUE))));
        QuestNode startNode = registerNode(this.startSmithingButton, baseX, baseY);

                this.ironClusterButton = new ItemIconButton(baseX + spacingX, baseY,
                        new ItemStack(ModItems.IRON_ORE_GRAVEL.get()),
                        b -> this.minecraft.setScreen(new IronClusterQuestScreen(this)),
                        () -> Arrays.asList(
                                new StringTextComponent("Железный рудный гравий")
                                        .withStyle(TextFormatting.BLUE, TextFormatting.UNDERLINE),
                                new StringTextComponent("Требуется: ")
                                        .append(new StringTextComponent("Стартовые молоты")
                                                .withStyle(TextFormatting.BLUE))));
                QuestNode ironNode = registerNode(this.ironClusterButton, baseX + spacingX, baseY);

        this.pureIronOreButton = new ItemIconButton(baseX + spacingX * 2, baseY,
                new ItemStack(ModItems.PURE_IRON_ORE.get()),
                b -> this.minecraft.setScreen(new PureIronOreQuestScreen(this)),
                () -> Arrays.asList(
                        new StringTextComponent("Чистая железная руда")
                                .withStyle(TextFormatting.BLUE, TextFormatting.UNDERLINE),
                        new StringTextComponent("Требуется: ")
                                .append(new StringTextComponent("Железный рудный гравий")
                                        .withStyle(TextFormatting.BLUE))));
        QuestNode pureNode = registerNode(this.pureIronOreButton, baseX + spacingX * 2, baseY);

        this.pyriteButton = new ItemIconButton(baseX, baseY + spacingY,
                new ItemStack(ModItems.PYRITE_PIECE.get()),
                b -> this.minecraft.setScreen(new PyriteQuestScreen(this)),
                () -> Arrays.asList(
                        new StringTextComponent("Пирит")
                                .withStyle(TextFormatting.BLUE, TextFormatting.UNDERLINE),
                        new StringTextComponent("Требуется: ")
                                .append(new StringTextComponent("Каменные или костяные инструменты")
                                        .withStyle(TextFormatting.BLUE))));
        QuestNode pyriteNode = registerNode(this.pyriteButton, baseX, baseY + spacingY);

        this.pyriteFlintButton = new ItemIconButton(baseX + spacingX, baseY + spacingY,
                new ItemStack(ModItems.PYRITE_FLINT.get()),
                b -> this.minecraft.setScreen(new PyriteFlintQuestScreen(this)),
                () -> Arrays.asList(
                        new StringTextComponent("Пиритовое огниво")
                                .withStyle(TextFormatting.BLUE, TextFormatting.UNDERLINE),
                        new StringTextComponent("Требуется: ")
                                .append(new StringTextComponent("Пирит")
                                        .withStyle(TextFormatting.BLUE))));
        QuestNode pyriteFlintNode = registerNode(this.pyriteFlintButton, baseX + spacingX, baseY + spacingY);

                this.firepitButton = new ItemIconButton(baseX + spacingX * 2, baseY + spacingY,
                        new ItemStack(ModItems.FIREPIT_BLOCK.get()),
                        b -> this.minecraft.setScreen(new FirepitQuestScreen(this)),
                        () -> Arrays.asList(
                                new StringTextComponent("Кострище")
                                        .withStyle(TextFormatting.BLUE, TextFormatting.UNDERLINE),
                                new StringTextComponent("Требуется: ")
                                        .append(new StringTextComponent("Чистая железная руда")
                                                .withStyle(TextFormatting.BLUE)),
                                new StringTextComponent("Также нужно: ")
                                        .append(new StringTextComponent("Булыжная плита")
                                                .withStyle(TextFormatting.BLUE))
                                        .append(new StringTextComponent(", "))
                                        .append(new StringTextComponent("Хворост")
                                                .withStyle(TextFormatting.BLUE))
                                        .append(new StringTextComponent(" и "))
                                        .append(new StringTextComponent("Пиритовое огниво")
                                                .withStyle(TextFormatting.BLUE))));
        QuestNode firepitNode = registerNode(this.firepitButton, baseX + spacingX * 2, baseY + spacingY);

        this.calcinedIronOreButton = new ItemIconButton(baseX + spacingX * 3, baseY + spacingY,
                new ItemStack(ModItems.CALCINED_IRON_ORE.get()),
                b -> this.minecraft.setScreen(new CalcinedIronOreQuestScreen(this)),
                () -> Arrays.asList(
                        new StringTextComponent("Обожжённая железная руда")
                                .withStyle(TextFormatting.BLUE, TextFormatting.UNDERLINE),
                        new StringTextComponent("Требуется: ")
                                .append(new StringTextComponent("Кострище")
                                        .withStyle(TextFormatting.BLUE))));
        QuestNode calcinedNode = registerNode(this.calcinedIronOreButton, baseX + spacingX * 3, baseY + spacingY);

        addConnection(startNode, ironNode, this::getIronClusterState);
        addConnection(ironNode, pureNode, this::getPureIronOreState);
        addConnection(pureNode, firepitNode, this::getFirepitState);
        addConnection(pyriteNode, pyriteFlintNode, this::getPyriteFlintState);
        addConnection(pyriteFlintNode, firepitNode, this::getFirepitState);
        addConnection(firepitNode, calcinedNode, this::getCalcinedIronOreState);
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

                // Only render quests if coming from Ancient World era
                if (parent instanceof ProgressEraScreen) {
                    this.startSmithingButton.setBorderColor(colorForState(getStartSmithingState()));
                    this.ironClusterButton.setBorderColor(colorForState(getIronClusterState()));
                    this.pureIronOreButton.setBorderColor(colorForState(getPureIronOreState()));
                    this.firepitButton.setBorderColor(colorForState(getFirepitState()));
                    this.pyriteButton.setBorderColor(colorForState(getPyriteState()));
                    this.pyriteFlintButton.setBorderColor(colorForState(getPyriteFlintState()));
                    this.calcinedIronOreButton.setBorderColor(colorForState(getCalcinedIronOreState()));
                }

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
                private QuestNode registerNode(ItemIconButton button, int baseX, int baseY) {
                    QuestNode node = new QuestNode(button, baseX, baseY);
                    nodes.add(node);
                    this.addButton(button);
                    return node;
                }

                private void addConnection(QuestNode from, QuestNode to, Supplier<QuestState> toStateSupplier) {
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
                        ItemIconButton fromButton = connection.from.button;
                        ItemIconButton toButton = connection.to.button;
                        int x1 = fromButton.x + fromButton.getWidth() / 2;
                        int y1 = fromButton.y + fromButton.getHeight() / 2;
                        int x2 = toButton.x + toButton.getWidth() / 2;
                        int y2 = toButton.y + toButton.getHeight() / 2;
                        if (x1 != x2) {
                            AbstractGui.fill(ms, Math.min(x1, x2), y1, Math.max(x1, x2), y1 + 1, color);
                        }
                        if (y1 != y2) {
                            AbstractGui.fill(ms, x2, Math.min(y1, y2), x2 + 1, Math.max(y1, y2), color);
                        }
                    }
                }

                private int colorForState(QuestState state) {
                    switch (state) {
                        case COMPLETED:
                            return 0xFF00FF00;
                        case AVAILABLE:
                            return 0xFF00BFFF;
                        default:
                            return 0xFFFF0000;
                    }
                }

                private QuestState getStartSmithingState() {
                    if (!(QuestManager.isStoneToolsCompleted() || QuestManager.isBoneToolsCompleted())) {
                        return QuestState.LOCKED;
                    }
                    return QuestManager.isStartSmithingCompleted() ? QuestState.COMPLETED : QuestState.AVAILABLE;
                }

                private QuestState getIronClusterState() {
                    if (!QuestManager.isStartHammersCompleted()) {
                        return QuestState.LOCKED;
                    }
                    return QuestManager.isIronClusterCompleted() ? QuestState.COMPLETED : QuestState.AVAILABLE;
                }
                private QuestState getPureIronOreState() {
                    if (!QuestManager.isIronClusterCompleted()) {
                        return QuestState.LOCKED;
                    }
                    return QuestManager.isPureIronOreCompleted() ? QuestState.COMPLETED : QuestState.AVAILABLE;
                }

                private QuestState getPyriteState() {
                    if (!(QuestManager.isStoneToolsCompleted() || QuestManager.isBoneToolsCompleted())) {
                        return QuestState.LOCKED;
                    }
                    return QuestManager.isPyriteCompleted() ? QuestState.COMPLETED : QuestState.AVAILABLE;
                }

                private QuestState getPyriteFlintState() {
                    if (!QuestManager.isPyriteCompleted()) {
                        return QuestState.LOCKED;
                    }
                    return QuestManager.isPyriteFlintCompleted() ? QuestState.COMPLETED : QuestState.AVAILABLE;
                }
                private QuestState getFirepitState() {
                    boolean unlocked = QuestManager.isPureIronOreCompleted()
                            && QuestManager.isCobbleSlabsCompleted()
                            && QuestManager.isBrushwoodCompleted()
                            && QuestManager.isPyriteFlintCompleted();
                    if (!unlocked) {
                        return QuestState.LOCKED;
                    }
                    return QuestManager.isFirepitCompleted() ? QuestState.COMPLETED : QuestState.AVAILABLE;
                }

                private QuestState getCalcinedIronOreState() {
                    if (!QuestManager.isFirepitCompleted()) {
                        return QuestState.LOCKED;
                    }
                    return QuestManager.isCalcinedIronOreCompleted() ? QuestState.COMPLETED : QuestState.AVAILABLE;
                }

            }