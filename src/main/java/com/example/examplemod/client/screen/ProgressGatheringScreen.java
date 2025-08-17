package com.example.examplemod.client.screen;

import com.example.examplemod.ModItems;
import com.example.examplemod.client.FramedButton;
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
import java.util.Arrays;

@OnlyIn(Dist.CLIENT)
public class ProgressGatheringScreen extends Screen {
    private final Screen parent;
    private ItemIconButton hewnStoneButton;
    private ItemIconButton bigBoneButton;
    private ItemIconButton sharpBoneButton;

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
        int spacing = 50;
        this.hewnStoneButton = new ItemIconButton(x, y, new ItemStack(ModItems.HEWN_STONE.get()),
                b -> this.minecraft.setScreen(new HewnStonesQuestScreen(this)),
                () -> Arrays.asList(
                        new StringTextComponent("Оттёсанный камень")
                                .withStyle(TextFormatting.BLUE, TextFormatting.UNDERLINE),
                        new StringTextComponent("Нет требований")));
        this.addButton(this.hewnStoneButton);
        this.bigBoneButton = new ItemIconButton(x, y + spacing, new ItemStack(ModItems.BIG_BONE.get()),
                b -> this.minecraft.setScreen(new BigBoneQuestScreen(this)),
                () -> Arrays.asList(
                        new StringTextComponent("Большая кость")
                                .withStyle(TextFormatting.BLUE, TextFormatting.UNDERLINE),
                        new StringTextComponent("Нет требований")));
        this.addButton(this.bigBoneButton);
        this.sharpBoneButton = new ItemIconButton(x + spacing, y + spacing,
                new ItemStack(ModItems.SHARPENED_BONE.get()),
                b -> this.minecraft.setScreen(new SharpenedBoneQuestScreen(this)),
                () -> Arrays.asList(
                        new StringTextComponent("Заостренная кость")
                                .withStyle(TextFormatting.BLUE, TextFormatting.UNDERLINE),
                        new StringTextComponent("Требуется: ")
                                .append(new StringTextComponent("Большая кость")
                                        .withStyle(TextFormatting.BLUE))));
        this.addButton(this.sharpBoneButton);
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

        // Update button color based on quest state
        this.hewnStoneButton.setBorderColor(
                QuestManager.isHewnStonesCompleted() ? 0xFF00FF00 : 0xFF00BFFF);
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

        super.render(ms, mouseX, mouseY, pt);
    }
}
