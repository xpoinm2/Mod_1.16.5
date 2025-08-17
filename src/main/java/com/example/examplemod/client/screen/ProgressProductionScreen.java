package com.example.examplemod.client.screen;

import com.example.examplemod.client.FramedButton;
import com.example.examplemod.client.ItemIconButton;
import com.example.examplemod.ModItems;
import com.example.examplemod.quest.QuestManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import java.util.Arrays;

@OnlyIn(Dist.CLIENT)
public class ProgressProductionScreen extends Screen {
    private final Screen parent;
    private ItemIconButton planksButton;
    private ItemIconButton slabsButton;
    private ItemIconButton stoneToolsButton;
    private ItemIconButton boneToolsButton;
    private ItemIconButton combButton;

    public ProgressProductionScreen(Screen parent) {
        super(new StringTextComponent("Производство"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.addButton(new FramedButton(5, 5, 20, 20, "<", 0xFFFFFF00, 0xFFFFFFFF,
                b -> this.minecraft.setScreen(parent)));
        int x = 40;
        int y = 60;
        int spacingX = 50;
        int spacingY = 23;
        this.planksButton = new ItemIconButton(x, y, new ItemStack(Items.OAK_PLANKS),
                b -> this.minecraft.setScreen(new PlanksQuestScreen(this)),
                () -> Arrays.asList(
                        new StringTextComponent("Доски")
                                .withStyle(TextFormatting.BLUE, TextFormatting.UNDERLINE),
                        new StringTextComponent("Нет требований")));
        this.addButton(this.planksButton);

        this.slabsButton = new ItemIconButton(x + spacingX, y, new ItemStack(Items.OAK_SLAB),
                b -> this.minecraft.setScreen(new SlabsQuestScreen(this)),
                () -> Arrays.asList(
                        new StringTextComponent("Плиты")
                                .withStyle(TextFormatting.BLUE, TextFormatting.UNDERLINE),
                        new StringTextComponent("Требуется: ")
                                .append(new StringTextComponent("Доски")
                                        .withStyle(TextFormatting.BLUE))));
        this.addButton(this.slabsButton);

        this.stoneToolsButton = new ItemIconButton(x + spacingX * 2, y,
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
        this.addButton(this.stoneToolsButton);

        this.boneToolsButton = new ItemIconButton(x + spacingX * 2, y + spacingY,
                new ItemStack(ModItems.BONE_PICKAXE.get()),
                b -> this.minecraft.setScreen(new BoneToolsQuestScreen(this)),
                () -> Arrays.asList(
                        new StringTextComponent("Костяные инструменты")
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
        this.addButton(this.boneToolsButton);

        this.combButton = new ItemIconButton(x + spacingX * 3, y,
                new ItemStack(ModItems.BONE_COMB.get()),
                b -> this.minecraft.setScreen(new CombsQuestScreen(this)),
                () -> Arrays.asList(
                        new StringTextComponent("Гребни")
                                .withStyle(TextFormatting.BLUE, TextFormatting.UNDERLINE),
                        new StringTextComponent("Требуется: ")
                                .append(new StringTextComponent("Оттёсанный камень")
                                        .withStyle(TextFormatting.BLUE))));
        this.addButton(this.combButton);
        super.init();
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float pt) {
        this.renderBackground(ms);
        AbstractGui.fill(ms, 0, 0, this.width, this.height, 0xCC000000);
        drawCenteredString(ms, this.font, this.title, this.width / 2, 30, 0xFF00FFFF);

        // Update button states and colors based on quest progress
        this.planksButton.setBorderColor(
                QuestManager.isPlanksCompleted() ? 0xFF00FF00 : 0xFF00BFFF);

        int slabsColor;
        if (!QuestManager.isPlanksCompleted()) {
            slabsColor = 0xFFFF0000; // locked
        } else if (QuestManager.isSlabsCompleted()) {
            slabsColor = 0xFF00FF00; // completed
        } else {
            slabsColor = 0xFF00BFFF; // available
        }
        this.slabsButton.setBorderColor(slabsColor);

        boolean stoneUnlocked = QuestManager.isHewnStonesCompleted() &&
                QuestManager.isFlaxFibersCompleted() &&
                QuestManager.isBranchCompleted();
        int toolsColor;
        if (!stoneUnlocked) {
            toolsColor = 0xFFFF0000; // locked
        } else if (QuestManager.isStoneToolsCompleted()) {
            toolsColor = 0xFF00FF00; // completed
        } else {
            toolsColor = 0xFF00BFFF; // available
        }
        this.stoneToolsButton.setBorderColor(toolsColor);

        boolean boneUnlocked = QuestManager.isHewnStonesCompleted() &&
                QuestManager.isFlaxFibersCompleted() &&
                QuestManager.isBranchCompleted();
        int boneColor;
        if (!boneUnlocked) {
            boneColor = 0xFFFF0000; // locked
        } else if (QuestManager.isBoneToolsCompleted()) {
            boneColor = 0xFF00FF00; // completed
        } else {
            boneColor = 0xFF00BFFF; // available
        }
        this.boneToolsButton.setBorderColor(boneColor);

        int combColor;
        if (!QuestManager.isHewnStonesCompleted()) {
            combColor = 0xFFFF0000;
        } else if (QuestManager.isCombsCompleted()) {
            combColor = 0xFF00FF00;
        } else {
            combColor = 0xFF00BFFF;
        }
        this.combButton.setBorderColor(combColor);

        drawConnection(ms, this.planksButton, this.slabsButton);

        super.render(ms, mouseX, mouseY, pt);
    }

    private void drawConnection(MatrixStack ms, ItemIconButton from, ItemIconButton to) {
        int x1 = from.x + from.getWidth() / 2;
        int y1 = from.y + from.getHeight() / 2;
        int x2 = to.x + to.getWidth() / 2;
        int y2 = to.y + to.getHeight() / 2;
        AbstractGui.fill(ms, Math.min(x1, x2), y1, Math.max(x1, x2), y1 + 1, 0xFFFFFFFF);
        AbstractGui.fill(ms, x2, Math.min(y1, y2), x2 + 1, Math.max(y1, y2), 0xFFFFFFFF);
    }
}