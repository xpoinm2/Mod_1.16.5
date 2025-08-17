package com.example.examplemod.client.screen;

import com.example.examplemod.ModItems;
import com.example.examplemod.client.FramedButton;
import com.example.examplemod.client.ItemIconButton;
import com.example.examplemod.client.GuiUtil;
import com.example.examplemod.client.screen.BranchQuestScreen;
import com.example.examplemod.client.screen.FlaxFibersQuestScreen;
import com.example.examplemod.quest.QuestManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import java.util.Arrays;

@OnlyIn(Dist.CLIENT)
public class ProgressGatheringScreen extends Screen {
    private final Screen parent;
    private ItemIconButton hewnStoneButton;
    private ItemIconButton branchButton;
    private ItemIconButton bigBoneButton;
    private ItemIconButton sharpBoneButton;
    private ItemIconButton flaxFibersButton;

    public ProgressGatheringScreen(Screen parent) {
        super(new StringTextComponent("Собирательство"));
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
        this.hewnStoneButton = new ItemIconButton(x, y, new ItemStack(ModItems.HEWN_STONE.get()),
                b -> this.minecraft.setScreen(new HewnStonesQuestScreen(this)),
                () -> Arrays.asList(
                        new StringTextComponent("Оттёсанный камень")
                                .withStyle(TextFormatting.BLUE, TextFormatting.UNDERLINE),
                        new StringTextComponent("Нет требований")));
        this.addButton(this.hewnStoneButton);

        this.branchButton = new ItemIconButton(x + spacingX, y, new ItemStack(ModItems.BRANCH.get()),
                b -> this.minecraft.setScreen(new BranchQuestScreen(this)),
                () -> Arrays.asList(
                        new StringTextComponent("Ветка")
                                .withStyle(TextFormatting.BLUE, TextFormatting.UNDERLINE),
                        new StringTextComponent("Нет требований")));
        this.addButton(this.branchButton);

        this.bigBoneButton = new ItemIconButton(x, y + spacingY, new ItemStack(ModItems.BIG_BONE.get()),
                b -> this.minecraft.setScreen(new BigBoneQuestScreen(this)),
                () -> Arrays.asList(
                        new StringTextComponent("Большая кость")
                                .withStyle(TextFormatting.BLUE, TextFormatting.UNDERLINE),
                        new StringTextComponent("Нет требований")));
        this.addButton(this.bigBoneButton);
        this.sharpBoneButton = new ItemIconButton(x + spacingX, y + spacingY,
                new ItemStack(ModItems.SHARPENED_BONE.get()),
                b -> this.minecraft.setScreen(new SharpenedBoneQuestScreen(this)),
                () -> Arrays.asList(
                        new StringTextComponent("Заостренная кость")
                                .withStyle(TextFormatting.BLUE, TextFormatting.UNDERLINE),
                        new StringTextComponent("Требуется: ")
                                .append(new StringTextComponent("Большая кость")
                                        .withStyle(TextFormatting.BLUE))));
        this.addButton(this.sharpBoneButton);
        this.flaxFibersButton = new ItemIconButton(x, y + spacingY * 2,
                new ItemStack(ModItems.FLAX_FIBERS.get()),
                b -> this.minecraft.setScreen(new FlaxFibersQuestScreen(this)),
                () -> Arrays.asList(
                        new StringTextComponent("Волокна льна")
                                .withStyle(TextFormatting.BLUE, TextFormatting.UNDERLINE),
                        new StringTextComponent("Нет требований")));
        this.addButton(this.flaxFibersButton);
        super.init();
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float pt) {
        this.renderBackground(ms);
        int x0 = 10;
        int y0 = 10;
        GuiUtil.drawPanel(ms, x0, y0, this.width - 20, this.height - 20);
        drawCenteredString(ms, this.font, this.title, this.width / 2, 30, 0xFF00FFFF);

        // Update button color based on quest state
        this.hewnStoneButton.setBorderColor(
                QuestManager.isHewnStonesCompleted() ? 0xFF00FF00 : 0xFF00BFFF);
        this.branchButton.setBorderColor(
                QuestManager.isBranchCompleted() ? 0xFF00FF00 : 0xFF00BFFF);
        this.bigBoneButton.setBorderColor(
                QuestManager.isBigBonesCompleted() ? 0xFF00FF00 : 0xFF00BFFF);
        int sharpColor;
        if (!QuestManager.isBigBonesCompleted()) {
            sharpColor = 0xFFFF0000;
        } else if (QuestManager.isSharpenedBoneCompleted()) {
            sharpColor = 0xFF00FF00;
        } else {
            sharpColor = 0xFF00BFFF;
        }
        this.sharpBoneButton.setBorderColor(sharpColor);
        this.flaxFibersButton.setBorderColor(
                QuestManager.isFlaxFibersCompleted() ? 0xFF00FF00 : 0xFF00BFFF);

        drawConnection(ms, this.bigBoneButton, this.sharpBoneButton);

        super.render(ms, mouseX, mouseY, pt);
    }

    private void drawConnection(MatrixStack ms, ItemIconButton from, ItemIconButton to) {
        int x1 = from.x + from.getWidth();
        int y1 = from.y + from.getHeight() / 2;
        int x2 = to.x;
        int y2 = to.y + to.getHeight() / 2;
        AbstractGui.fill(ms, x1, y1, x2, y1 + 1, 0xFFFFFFFF);
        AbstractGui.fill(ms, x2 - 1, Math.min(y1, y2), x2, Math.max(y1, y2), 0xFFFFFFFF);
    }
}
