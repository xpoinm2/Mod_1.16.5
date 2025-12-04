package com.example.examplemod.client.screen;

import com.example.examplemod.ModItems;
import com.example.examplemod.client.FramedButton;
import com.example.examplemod.client.GuiUtil;
import com.example.examplemod.client.ItemIconButton;
import com.example.examplemod.quest.QuestManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
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
public class ProgressGatheringScreen extends Screen {
    private static final int PANEL_MARGIN = 10;
    private static final int MAP_PADDING = 15;

    private final Screen parent;

    private final List<QuestNode> nodes = new ArrayList<>();
    private final List<QuestConnection> connections = new ArrayList<>();
    private ItemIconButton branchButton;
    private ItemIconButton initialFaunaButton;
    private ItemIconButton brushwoodButton;
    private ItemIconButton hewnStoneButton;
    private ItemIconButton bigBoneButton;
    private ItemIconButton sharpBoneButton;
    private ItemIconButton flaxFibersButton;
    private ItemIconButton unrefinedTinButton;
    private ItemIconButton unrefinedGoldButton;
    private ItemIconButton cleanedTinButton;
    private ItemIconButton cleanedGoldButton;


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

    public ProgressGatheringScreen(Screen parent) {
        super(new StringTextComponent("Собирательство"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.addButton(new FramedButton(5, 5, 20, 20, "<", 0xFFFFFF00, 0xFFFFFFFF,
                b -> this.minecraft.setScreen(parent)));

        nodes.clear();
        connections.clear();

        if (parent instanceof ProgressEraScreen) {
            initQuests();
        } else if (parent instanceof AncientMetallurgyEraScreen) {
            initMetallurgyQuests();
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

        this.branchButton = new ItemIconButton(baseX, baseY, new ItemStack(ModItems.BRANCH.get()),
                b -> this.minecraft.setScreen(new BranchQuestScreen(this)),
                () -> Arrays.asList(
                        new StringTextComponent("Ветка")
                                .withStyle(TextFormatting.BLUE, TextFormatting.UNDERLINE),
                        new StringTextComponent("Нет требований")));
        QuestNode branchNode = registerNode(this.branchButton, baseX, baseY);

        this.initialFaunaButton = new ItemIconButton(baseX + spacingX, baseY,
                new ItemStack(ModItems.RASPBERRY.get()),
                b -> this.minecraft.setScreen(new InitialFaunaQuestScreen(this)),
                () -> Arrays.asList(
                        new StringTextComponent("Начальная фауна")
                                .withStyle(TextFormatting.BLUE, TextFormatting.UNDERLINE),
                        new StringTextComponent("Нет требований")));
        registerNode(this.initialFaunaButton, baseX + spacingX, baseY);

        this.hewnStoneButton = new ItemIconButton(baseX, baseY + spacingY,
                new ItemStack(ModItems.HEWN_STONE.get()),
                b -> this.minecraft.setScreen(new HewnStonesQuestScreen(this)),
                () -> Arrays.asList(
                        new StringTextComponent("Оттёсанный камень")
                                .withStyle(TextFormatting.BLUE, TextFormatting.UNDERLINE),
                        new StringTextComponent("Нет требований")));
        registerNode(this.hewnStoneButton, baseX, baseY + spacingY);

        this.brushwoodButton = new ItemIconButton(baseX + spacingX, baseY + spacingY,
                new ItemStack(ModItems.BRUSHWOOD_SLAB.get()),
                b -> this.minecraft.setScreen(new BrushwoodQuestScreen(this)),
                () -> Arrays.asList(
                        new StringTextComponent("Хворост")
                                .withStyle(TextFormatting.BLUE, TextFormatting.UNDERLINE),
                        new StringTextComponent("Требуется: ")
                                .append(new StringTextComponent("Ветка")
                                        .withStyle(TextFormatting.BLUE))));
        QuestNode brushwoodNode = registerNode(this.brushwoodButton, baseX + spacingX, baseY + spacingY);
        this.bigBoneButton = new ItemIconButton(baseX, baseY + spacingY * 2,
                new ItemStack(ModItems.BIG_BONE.get()),

                b -> this.minecraft.setScreen(new BigBoneQuestScreen(this)),
                () -> Arrays.asList(
                        new StringTextComponent("Большая кость")
                                .withStyle(TextFormatting.BLUE, TextFormatting.UNDERLINE),
                        new StringTextComponent("Нет требований")));
        QuestNode bigBoneNode = registerNode(this.bigBoneButton, baseX, baseY + spacingY * 2);

        this.sharpBoneButton = new ItemIconButton(baseX + spacingX, baseY + spacingY * 2,
                new ItemStack(ModItems.SHARPENED_BONE.get()),
                b -> this.minecraft.setScreen(new SharpenedBoneQuestScreen(this)),
                () -> Arrays.asList(
                        new StringTextComponent("Заостренная кость")
                                .withStyle(TextFormatting.BLUE, TextFormatting.UNDERLINE),
                        new StringTextComponent("Требуется: ")
                                .append(new StringTextComponent("Большая кость")
                                        .withStyle(TextFormatting.BLUE))));
        QuestNode sharpBoneNode = registerNode(this.sharpBoneButton, baseX + spacingX, baseY + spacingY * 2);

        this.flaxFibersButton = new ItemIconButton(baseX, baseY + spacingY * 3,
                new ItemStack(ModItems.FLAX_FIBERS.get()),
                b -> this.minecraft.setScreen(new FlaxFibersQuestScreen(this)),
                () -> Arrays.asList(
                        new StringTextComponent("Волокна льна")
                                .withStyle(TextFormatting.BLUE, TextFormatting.UNDERLINE),
                        new StringTextComponent("Нет требований")));
        registerNode(this.flaxFibersButton, baseX, baseY + spacingY * 3);

        addConnection(branchNode, brushwoodNode, this::getBrushwoodState);
        addConnection(bigBoneNode, sharpBoneNode, this::getSharpBoneState);
    }

    private void initMetallurgyQuests() {
        int baseX = 80;
        int baseY = 90;
        int spacingX = 90;
        int spacingY = 80;

        this.unrefinedTinButton = new ItemIconButton(baseX, baseY,
                new ItemStack(ModItems.UNREFINED_TIN_ORE.get()),
                b -> this.minecraft.setScreen(new UnrefinedTinOreQuestScreen(this)),
                () -> Arrays.asList(
                        new StringTextComponent("Неочищенная оловянная руда")
                                .withStyle(TextFormatting.BLUE, TextFormatting.UNDERLINE),
                        new StringTextComponent("Нужно для открытия: ")
                                .append(new StringTextComponent("Пройти древний мир")
                                        .withStyle(TextFormatting.GOLD))));
        QuestNode unrefinedTinNode = registerNode(this.unrefinedTinButton, baseX, baseY);

        this.unrefinedGoldButton = new ItemIconButton(baseX + spacingX, baseY,
                new ItemStack(ModItems.UNREFINED_GOLD_ORE.get()),
                b -> this.minecraft.setScreen(new UnrefinedGoldOreQuestScreen(this)),
                () -> Arrays.asList(
                        new StringTextComponent("Неочищенная золотая руда")
                                .withStyle(TextFormatting.BLUE, TextFormatting.UNDERLINE),
                        new StringTextComponent("Нужно для открытия: ")
                                .append(new StringTextComponent("Пройти древний мир")
                                        .withStyle(TextFormatting.GOLD))));
        QuestNode unrefinedGoldNode = registerNode(this.unrefinedGoldButton, baseX + spacingX, baseY);

        this.cleanedTinButton = new ItemIconButton(baseX, baseY + spacingY,
                new ItemStack(ModItems.CLEANED_GRAVEL_TIN_ORE.get()),
                b -> this.minecraft.setScreen(new CleanedGravelTinOreQuestScreen(this)),
                () -> Arrays.asList(
                        new StringTextComponent("Очищённая гравийная оловянная руда")
                                .withStyle(TextFormatting.BLUE, TextFormatting.UNDERLINE),
                        new StringTextComponent("Нужно для открытия: ")
                                .append(new StringTextComponent("Неочищенная оловянная руда")
                                        .withStyle(TextFormatting.GOLD))));
        QuestNode cleanedTinNode = registerNode(this.cleanedTinButton, baseX, baseY + spacingY);

        this.cleanedGoldButton = new ItemIconButton(baseX + spacingX, baseY + spacingY,
                new ItemStack(ModItems.CLEANED_GRAVEL_GOLD_ORE.get()),
                b -> this.minecraft.setScreen(new CleanedGravelGoldOreQuestScreen(this)),
                () -> Arrays.asList(
                        new StringTextComponent("Очищённая гравийная золотая руда")
                                .withStyle(TextFormatting.BLUE, TextFormatting.UNDERLINE),
                        new StringTextComponent("Нужно для открытия: ")
                                .append(new StringTextComponent("Неочищенная золотая руда")
                                        .withStyle(TextFormatting.GOLD))));
        QuestNode cleanedGoldNode = registerNode(this.cleanedGoldButton, baseX + spacingX, baseY + spacingY);

        addConnection(unrefinedTinNode, cleanedTinNode, this::getCleanedTinState);
        addConnection(unrefinedGoldNode, cleanedGoldNode, this::getCleanedGoldState);
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
            this.branchButton.setBorderColor(colorForState(getBranchState()));
            this.initialFaunaButton.setBorderColor(colorForState(getInitialFaunaState()));
            this.brushwoodButton.setBorderColor(colorForState(getBrushwoodState()));
            this.hewnStoneButton.setBorderColor(colorForState(getHewnStoneState()));
            this.bigBoneButton.setBorderColor(colorForState(getBigBoneState()));
            this.sharpBoneButton.setBorderColor(colorForState(getSharpBoneState()));
            this.flaxFibersButton.setBorderColor(colorForState(getFlaxFibersState()));
        } else if (parent instanceof AncientMetallurgyEraScreen) {
            if (this.unrefinedTinButton != null) {
                this.unrefinedTinButton.setBorderColor(colorForState(getUnrefinedTinState()));
            }
            if (this.unrefinedGoldButton != null) {
                this.unrefinedGoldButton.setBorderColor(colorForState(getUnrefinedGoldState()));
            }
            if (this.cleanedTinButton != null) {
                this.cleanedTinButton.setBorderColor(colorForState(getCleanedTinState()));
            }
            if (this.cleanedGoldButton != null) {
                this.cleanedGoldButton.setBorderColor(colorForState(getCleanedGoldState()));
            }
        }

        // Clip content within panel bounds
        enableScissor(PANEL_MARGIN, PANEL_MARGIN, this.width - PANEL_MARGIN * 2, this.height - PANEL_MARGIN * 2);

        renderConnections(ms);

        super.render(ms, mouseX, mouseY, partialTicks);

        disableScissor();
    }

    private QuestState getBranchState() {
        return QuestManager.isBranchCompleted() ? QuestState.COMPLETED : QuestState.AVAILABLE;
    }

    private QuestState getInitialFaunaState() {
        return QuestManager.isInitialFaunaCompleted() ? QuestState.COMPLETED : QuestState.AVAILABLE;
    }

    private QuestState getBrushwoodState() {
        if (!QuestManager.isBranchCompleted()) {
            return QuestState.LOCKED;
        }
        return QuestManager.isBrushwoodCompleted() ? QuestState.COMPLETED : QuestState.AVAILABLE;
    }

    private QuestState getHewnStoneState() {
        return QuestManager.isHewnStonesCompleted() ? QuestState.COMPLETED : QuestState.AVAILABLE;
    }

    private QuestState getBigBoneState() {
        return QuestManager.isBigBonesCompleted() ? QuestState.COMPLETED : QuestState.AVAILABLE;
    }

    private QuestState getSharpBoneState() {
        if (!QuestManager.isBigBonesCompleted()) {
            return QuestState.LOCKED;
        }
        return QuestManager.isSharpenedBoneCompleted() ? QuestState.COMPLETED : QuestState.AVAILABLE;
    }

    private QuestState getFlaxFibersState() {
        return QuestManager.isFlaxFibersCompleted() ? QuestState.COMPLETED : QuestState.AVAILABLE;
    }

    private QuestState getUnrefinedTinState() {
        if (!QuestManager.isAncientWorldCompleted()) {
            return QuestState.LOCKED;
        }
        return QuestManager.isUnrefinedTinOreCompleted() ? QuestState.COMPLETED : QuestState.AVAILABLE;
    }

    private QuestState getUnrefinedGoldState() {
        if (!QuestManager.isAncientWorldCompleted()) {
            return QuestState.LOCKED;
        }
        return QuestManager.isUnrefinedGoldOreCompleted() ? QuestState.COMPLETED : QuestState.AVAILABLE;
    }

    private QuestState getCleanedTinState() {
        if (!QuestManager.isUnrefinedTinOreCompleted()) {
            return QuestState.LOCKED;
        }
        return QuestManager.isCleanedGravelTinOreCompleted() ? QuestState.COMPLETED : QuestState.AVAILABLE;
    }

    private QuestState getCleanedGoldState() {
        if (!QuestManager.isUnrefinedGoldOreCompleted()) {
            return QuestState.LOCKED;
        }
        return QuestManager.isCleanedGravelGoldOreCompleted() ? QuestState.COMPLETED : QuestState.AVAILABLE;
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

}
