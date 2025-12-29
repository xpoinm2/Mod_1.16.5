package com.example.examplemod.client.screen.progress;

import com.example.examplemod.ModItems;
import com.example.examplemod.client.FramedButton;
import com.example.examplemod.client.GuiUtil;
import com.example.examplemod.client.ItemIconButton;
import com.example.examplemod.client.screen.quest.*;
import com.example.examplemod.quest.QuestManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class ProgressProductionScreen extends Screen {
    private static final int PANEL_MARGIN = 10;
    private static final int MAP_PADDING = 15;

    private final Screen parent;

    private final List<QuestNode> nodes = new ArrayList<>();
    private final List<QuestConnection> connections = new ArrayList<>();

    private ItemIconButton planksButton;
    private ItemIconButton slabsButton;
    private ItemIconButton cobbleSlabButton;
    private ItemIconButton stoneToolsButton;
    private ItemIconButton boneToolsButton;
    private ItemIconButton combButton;
    private ItemIconButton startHammersButton;
    private ItemIconButton roughKnivesButton;
    private ItemIconButton scrapedLeatherButton;
    private ItemIconButton clayPotButton;
    private ItemIconButton clayCupButton;
    private ItemIconButton boneTongsButton;


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

    public ProgressProductionScreen(Screen parent) {
        super(new StringTextComponent("Производство"));
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

        this.planksButton = new ItemIconButton(baseX, baseY, new ItemStack(Items.OAK_PLANKS),
                b -> this.minecraft.setScreen(new PlanksQuestScreen(this)),
                () -> Arrays.asList(
                        new StringTextComponent("Доски")
                                .withStyle(TextFormatting.BLUE, TextFormatting.UNDERLINE),
                        new StringTextComponent("Нет требований")));
        QuestNode planksNode = registerNode(this.planksButton, baseX, baseY);

                this.slabsButton = new ItemIconButton(baseX + spacingX, baseY, new ItemStack(ModItems.OAK_SLAB.get()),
                        b -> this.minecraft.setScreen(new SlabsQuestScreen(this)),
                        () -> Arrays.asList(
                                new StringTextComponent("Плиты")
                                        .withStyle(TextFormatting.BLUE, TextFormatting.UNDERLINE),
                                new StringTextComponent("Требуется: ")
                                        .append(new StringTextComponent("Доски")
                                                .withStyle(TextFormatting.BLUE))));
        QuestNode slabsNode = registerNode(this.slabsButton, baseX + spacingX, baseY);

                this.stoneToolsButton = new ItemIconButton(baseX + spacingX * 2, baseY,
                        new ItemStack(ModItems.STONE_PICKAXE.get()),
                        b -> this.minecraft.setScreen(new StoneToolsQuestScreen(this)),
                        () -> Arrays.asList(
                                new StringTextComponent("Каменные инструменты")
                                        .withStyle(TextFormatting.BLUE, TextFormatting.UNDERLINE),
                                new StringTextComponent("Требуется: ")
                                        .append(new StringTextComponent("Оттёсанный камень")
                                                .withStyle(TextFormatting.BLUE))
                                        .append(new StringTextComponent(", "))
                                        .append(new StringTextComponent("Волокна льна")
                                                .withStyle(TextFormatting.BLUE))
                                        .append(new StringTextComponent(", "))
                                        .append(new StringTextComponent("Ветка")
                                                .withStyle(TextFormatting.BLUE))));
        QuestNode stoneToolsNode = registerNode(this.stoneToolsButton, baseX + spacingX * 2, baseY);

                this.combButton = new ItemIconButton(baseX + spacingX * 3, baseY,
                        new ItemStack(ModItems.BONE_COMB.get()),
                        b -> this.minecraft.setScreen(new CombsQuestScreen(this)),
                        () -> Arrays.asList(
        new StringTextComponent("Гребни")
                .withStyle(TextFormatting.BLUE, TextFormatting.UNDERLINE),
                new StringTextComponent("Требуется: ")
                                .append(new StringTextComponent("Большая кость")
                                        .withStyle(TextFormatting.BLUE))
                                .append(new StringTextComponent(", "))
                                        .append(new StringTextComponent("Оттёсанный камень")
                                                .withStyle(TextFormatting.BLUE))
                                        .append(new StringTextComponent(", "))
                                        .append(new StringTextComponent("Ветка")
                                                .withStyle(TextFormatting.BLUE))));
        QuestNode combNode = registerNode(this.combButton, baseX + spacingX * 3, baseY);

        this.clayPotButton = new ItemIconButton(baseX, baseY + spacingY,
                new ItemStack(ModItems.CLAY_POT.get()),
                b -> this.minecraft.setScreen(new ClayPotQuestScreen(this)),
                () -> Arrays.asList(
                        new StringTextComponent("Глиняный горшок")
                                .withStyle(TextFormatting.BLUE, TextFormatting.UNDERLINE),
                        new StringTextComponent("Требуется: ")
                                .append(new StringTextComponent("Кострище")
                                        .withStyle(TextFormatting.BLUE))));
        QuestNode clayPotNode = registerNode(this.clayPotButton, baseX, baseY + spacingY);

        this.clayCupButton = new ItemIconButton(baseX, baseY + spacingY * 2,
                new ItemStack(ModItems.CLAY_CUP.get()),
                b -> this.minecraft.setScreen(new ClayCupQuestScreen(this)),
                () -> Arrays.asList(
                        new StringTextComponent("Глиняная чашка")
                                .withStyle(TextFormatting.BLUE, TextFormatting.UNDERLINE),
                        new StringTextComponent("Требуется: ")
                                .append(new StringTextComponent("Кострище")
                                        .withStyle(TextFormatting.BLUE))));
        QuestNode clayCupNode = registerNode(this.clayCupButton, baseX, baseY + spacingY * 2);

        this.cobbleSlabButton = new ItemIconButton(baseX + spacingX, baseY + spacingY,
                new ItemStack(ModItems.COBBLESTONE_SLAB.get()),
                b -> this.minecraft.setScreen(new CobbleSlabQuestScreen(this)),
                () -> Arrays.asList(
                        new StringTextComponent("Булыжная плита")
                                .withStyle(TextFormatting.BLUE, TextFormatting.UNDERLINE),
                        new StringTextComponent("Нет требований")));
        QuestNode cobbleNode = registerNode(this.cobbleSlabButton, baseX + spacingX, baseY + spacingY);

        this.boneToolsButton = new ItemIconButton(baseX + spacingX * 2, baseY + spacingY,
                new ItemStack(ModItems.BONE_PICKAXE.get()),
                b -> this.minecraft.setScreen(new BoneToolsQuestScreen(this)),
                () -> Arrays.asList(
                        new StringTextComponent("Костяные инструменты")
                                .withStyle(TextFormatting.BLUE, TextFormatting.UNDERLINE),
                        new StringTextComponent("Требуется: ")
                                .append(new StringTextComponent("Заостренная кость")
                                        .withStyle(TextFormatting.BLUE))
                                .append(new StringTextComponent(", "))
                                        .append(new StringTextComponent("Волокна льна")
                                                .withStyle(TextFormatting.BLUE))
                                        .append(new StringTextComponent(", "))
                                        .append(new StringTextComponent("Ветка")
                                                .withStyle(TextFormatting.BLUE))));
        QuestNode boneToolsNode = registerNode(this.boneToolsButton, baseX + spacingX * 2, baseY + spacingY);

                this.startHammersButton = new ItemIconButton(baseX + spacingX * 3, baseY + spacingY,
                        new ItemStack(ModItems.STONE_HAMMER.get()),
                        b -> this.minecraft.setScreen(new StartHammersQuestScreen(this)),
                        () -> Arrays.asList(
                                new StringTextComponent("Стартовые молоты")
                                        .withStyle(TextFormatting.BLUE, TextFormatting.UNDERLINE),
                                new StringTextComponent("Требуется: ")
                                        .append(new StringTextComponent("Начало кузнечного дела")
                                                .withStyle(TextFormatting.BLUE))
                                        .append(new StringTextComponent(", "))
                                        .append(new StringTextComponent("Костяные инструменты")
                                                .withStyle(TextFormatting.BLUE))
                                        .append(new StringTextComponent(", "))
                                        .append(new StringTextComponent("Каменные инструменты")
                                                .withStyle(TextFormatting.BLUE))));
        QuestNode hammersNode = registerNode(this.startHammersButton, baseX + spacingX * 3, baseY + spacingY);

        this.roughKnivesButton = new ItemIconButton(baseX + spacingX * 2, baseY + spacingY * 2,
                new ItemStack(ModItems.ROUGH_STONE_KNIFE.get()),
                b -> this.minecraft.setScreen(new RoughKnivesQuestScreen(this)),
                () -> Arrays.asList(
                        new StringTextComponent("Первые грубые ножи")
                                .withStyle(TextFormatting.BLUE, TextFormatting.UNDERLINE),
                        new StringTextComponent("Требуется: ")
                                .append(new StringTextComponent("Каменные инструменты")
                                        .withStyle(TextFormatting.BLUE))
                                .append(new StringTextComponent(" или "))
                                .append(new StringTextComponent("Костяные инструменты")
                                        .withStyle(TextFormatting.BLUE))));
        QuestNode roughKnivesNode = registerNode(this.roughKnivesButton, baseX + spacingX * 2, baseY + spacingY * 2);

        this.scrapedLeatherButton = new ItemIconButton(baseX + spacingX * 3, baseY + spacingY * 2,
                new ItemStack(ModItems.SCRAPED_HIDE.get()),
                b -> this.minecraft.setScreen(new ScrapedLeatherQuestScreen(this)),
                () -> Arrays.asList(
                        new StringTextComponent("Выскобленная кожа")
                                .withStyle(TextFormatting.BLUE, TextFormatting.UNDERLINE),
                        new StringTextComponent("Требуется: ")
                                .append(new StringTextComponent("Первые грубые ножи")
                                        .withStyle(TextFormatting.BLUE))));
        QuestNode scrapedLeatherNode = registerNode(this.scrapedLeatherButton, baseX + spacingX * 3, baseY + spacingY * 2);

        addConnection(planksNode, slabsNode, this::getSlabsState);
        addConnection(slabsNode, cobbleNode, this::getCobbleSlabState);
        addConnection(stoneToolsNode, hammersNode, this::getStartHammersState);
        addConnection(boneToolsNode, hammersNode, this::getStartHammersState);
        addConnection(stoneToolsNode, roughKnivesNode, this::getRoughKnivesState);
        addConnection(boneToolsNode, roughKnivesNode, this::getRoughKnivesState);
        addConnection(roughKnivesNode, scrapedLeatherNode, this::getScrapedLeatherState);
        addConnection(clayPotNode, clayCupNode, this::getClayCupState);
    }

    private void initMetallurgyQuests() {
        int baseX = 140;
        int baseY = 130;
        this.boneTongsButton = new ItemIconButton(baseX, baseY,
                new ItemStack(ModItems.BONE_TONGS.get()),
                b -> this.minecraft.setScreen(new BoneTongsQuestScreen(this)),
                () -> Arrays.asList(
                        new StringTextComponent("Костяные щипцы")
                                .withStyle(TextFormatting.BLUE, TextFormatting.UNDERLINE),
                        new StringTextComponent("Требуется: ")
                                .append(new StringTextComponent("Пройти древний мир")
                                        .withStyle(TextFormatting.GOLD))));
        registerNode(this.boneTongsButton, baseX, baseY);
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

        if (parent instanceof ProgressEraScreen) {
            this.planksButton.setBorderColor(colorForState(getPlanksState()));
            this.slabsButton.setBorderColor(colorForState(getSlabsState()));
            this.cobbleSlabButton.setBorderColor(colorForState(getCobbleSlabState()));
            this.stoneToolsButton.setBorderColor(colorForState(getStoneToolsState()));
            this.boneToolsButton.setBorderColor(colorForState(getBoneToolsState()));
            this.combButton.setBorderColor(colorForState(getCombState()));
            this.startHammersButton.setBorderColor(colorForState(getStartHammersState()));
            this.roughKnivesButton.setBorderColor(colorForState(getRoughKnivesState()));
            this.scrapedLeatherButton.setBorderColor(colorForState(getScrapedLeatherState()));
            this.clayPotButton.setBorderColor(colorForState(getClayPotState()));
            this.clayCupButton.setBorderColor(colorForState(getClayCupState()));
        } else if (parent instanceof AncientMetallurgyEraScreen) {
            if (this.boneTongsButton != null) {
                this.boneTongsButton.setBorderColor(colorForState(getBoneTongsState()));
            }
        }

            // Clip content within panel bounds
            enableScissor(PANEL_MARGIN, PANEL_MARGIN, this.width - PANEL_MARGIN * 2, this.height - PANEL_MARGIN * 2);

            renderConnections(ms);

            super.render(ms, mouseX, mouseY, partialTicks);

            disableScissor();
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

private QuestState getPlanksState() {
    return QuestManager.isPlanksCompleted() ? QuestState.COMPLETED : QuestState.AVAILABLE;
}

private QuestState getSlabsState() {
    if (!QuestManager.isPlanksCompleted()) {
    return QuestState.LOCKED;
}
    return QuestManager.isSlabsCompleted() ? QuestState.COMPLETED : QuestState.AVAILABLE;
}

private QuestState getCobbleSlabState() {
    return QuestManager.isCobbleSlabsCompleted() ? QuestState.COMPLETED : QuestState.AVAILABLE;
}

private QuestState getStoneToolsState() {
    boolean unlocked = QuestManager.isHewnStonesCompleted()
            && QuestManager.isFlaxFibersCompleted()
            && QuestManager.isBranchCompleted();
    if (!unlocked) {
        return QuestState.LOCKED;
    }
    return QuestManager.isStoneToolsCompleted() ? QuestState.COMPLETED : QuestState.AVAILABLE;
}

private QuestState getBoneToolsState() {
    boolean unlocked = QuestManager.isSharpenedBoneCompleted()
            && QuestManager.isFlaxFibersCompleted()
            && QuestManager.isBranchCompleted();
    if (!unlocked) {
        return QuestState.LOCKED;
    }
    return QuestManager.isBoneToolsCompleted() ? QuestState.COMPLETED : QuestState.AVAILABLE;
}

    private QuestState getBoneTongsState() {
        if (!QuestManager.isAncientWorldCompleted()) {
            return QuestState.LOCKED;
        }
        return QuestManager.isBoneTongsCompleted() ? QuestState.COMPLETED : QuestState.AVAILABLE;
    }

private QuestState getCombState() {
    boolean unlocked = QuestManager.isBigBonesCompleted()
            && QuestManager.isHewnStonesCompleted()
            && QuestManager.isBranchCompleted();
    if (!unlocked) {
        return QuestState.LOCKED;
    }
    return QuestManager.isCombsCompleted() ? QuestState.COMPLETED : QuestState.AVAILABLE;
}

private QuestState getStartHammersState() {
    boolean unlocked = QuestManager.isStartSmithingCompleted()
            && QuestManager.isBoneToolsCompleted()
            && QuestManager.isStoneToolsCompleted();
    if (!unlocked) {
        return QuestState.LOCKED;
    }
    return QuestManager.isStartHammersCompleted() ? QuestState.COMPLETED : QuestState.AVAILABLE;
}

private QuestState getRoughKnivesState() {
    boolean unlocked = QuestManager.isStoneToolsCompleted() || QuestManager.isBoneToolsCompleted();
    if (!unlocked) {
        return QuestState.LOCKED;
    }
    return QuestManager.isRoughKnivesCompleted() ? QuestState.COMPLETED : QuestState.AVAILABLE;
}

private QuestState getScrapedLeatherState() {
    if (!QuestManager.isRoughKnivesCompleted()) {
        return QuestState.LOCKED;
    }
    return QuestManager.isScrapedLeatherCompleted() ? QuestState.COMPLETED : QuestState.AVAILABLE;
}

private QuestState getClayPotState() {
    if (!QuestManager.isFirepitCompleted()) {
        return QuestState.LOCKED;
    }
    return QuestManager.isClayPotCompleted() ? QuestState.COMPLETED : QuestState.AVAILABLE;
}

    private QuestState getClayCupState() {
        if (!QuestManager.isFirepitCompleted()) {
            return QuestState.LOCKED;
        }
        return QuestManager.isClayCupCompleted() ? QuestState.COMPLETED : QuestState.AVAILABLE;
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