package com.example.examplemod.client.screen;

import com.example.examplemod.ModItems;
import com.example.examplemod.client.FramedButton;
import com.example.examplemod.client.GuiUtil;
import com.example.examplemod.client.ItemIconButton;
import com.example.examplemod.quest.QuestManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import java.util.Arrays;
import com.example.examplemod.client.screen.IronClusterQuestScreen;
import com.example.examplemod.client.screen.PureIronOreQuestScreen;

@OnlyIn(Dist.CLIENT)
public class ProgressMetallurgyScreen extends Screen {
    private final Screen parent;
    private ItemIconButton startSmithingButton;
    private ItemIconButton firepitButton;
    private ItemIconButton ironClusterButton;
    private ItemIconButton pureIronOreButton;

    public ProgressMetallurgyScreen(Screen parent) {
        super(new StringTextComponent("Металлургия"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.addButton(new FramedButton(5, 5, 20, 20, "<", 0xFFFFFF00, 0xFFFFFFFF,
                b -> this.minecraft.setScreen(parent)));
        int x = 40;
        int y = 60;
        int spacingX = 50;
        this.startSmithingButton = new ItemIconButton(x, y,
                new ItemStack(ModItems.IMPURE_IRON_ORE.get()),
                b -> this.minecraft.setScreen(new StartSmithingQuestScreen(this)),
                () -> Arrays.asList(
                        new StringTextComponent("Начало кузнечного дела")
                                .withStyle(TextFormatting.BLUE, TextFormatting.UNDERLINE),
                        new StringTextComponent("Требуется: ")
                                .append(new StringTextComponent("Каменные или костяные инструменты")
                                        .withStyle(TextFormatting.BLUE))));
        this.addButton(this.startSmithingButton);

        this.ironClusterButton = new ItemIconButton(x + spacingX, y,
                new ItemStack(ModItems.IRON_CLUSTER.get()),
                b -> this.minecraft.setScreen(new IronClusterQuestScreen(this)),
                () -> Arrays.asList(
                        new StringTextComponent("Железный кластер")
                                .withStyle(TextFormatting.BLUE, TextFormatting.UNDERLINE),
                        new StringTextComponent("Требуется: ")
                                .append(new StringTextComponent("Стартовые молоты")
                                        .withStyle(TextFormatting.BLUE))));
        this.addButton(this.ironClusterButton);
        this.pureIronOreButton = new ItemIconButton(x + spacingX * 2, y,
                new ItemStack(ModItems.PURE_IRON_ORE.get()),
                b -> this.minecraft.setScreen(new PureIronOreQuestScreen(this)),
                () -> Arrays.asList(
                        new StringTextComponent("Чистая железная руда")
                                .withStyle(TextFormatting.BLUE, TextFormatting.UNDERLINE),
                        new StringTextComponent("Требуется: ")
                                .append(new StringTextComponent("Железный кластер")
                                        .withStyle(TextFormatting.BLUE))));
        this.addButton(this.pureIronOreButton);

        this.firepitButton = new ItemIconButton(x + spacingX * 3, y,
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
                                .append(new StringTextComponent(" и "))
                                .append(new StringTextComponent("Хворост")
                                        .withStyle(TextFormatting.BLUE))));
        this.addButton(this.firepitButton);
        super.init();
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float pt) {
        this.renderBackground(ms);
        GuiUtil.drawPanel(ms, 10, 10, this.width - 20, this.height - 20);
        drawCenteredString(ms, this.font, this.title, this.width / 2, 30, 0xFF00FFFF);

        int color;
        if (!(QuestManager.isStoneToolsCompleted() || QuestManager.isBoneToolsCompleted())) {
            color = 0xFFFF0000;
        } else if (QuestManager.isStartSmithingCompleted()) {
            color = 0xFF00FF00;
        } else {
            color = 0xFF00BFFF;
        }
        this.startSmithingButton.setBorderColor(color);

        boolean firepitUnlocked = QuestManager.isPureIronOreCompleted()
                && QuestManager.isCobbleSlabsCompleted()
                && QuestManager.isBrushwoodCompleted();

        int firepitColor;
        if (!firepitUnlocked) {
            firepitColor = 0xFFFF0000;
        } else if (QuestManager.isFirepitCompleted()) {
            firepitColor = 0xFF00FF00;
        } else {
            firepitColor = 0xFF00BFFF;
        }
        this.firepitButton.setBorderColor(firepitColor);

        int ironColor;
        if (!QuestManager.isStartHammersCompleted()) {
            ironColor = 0xFFFF0000;
        } else if (QuestManager.isIronClusterCompleted()) {
            ironColor = 0xFF00FF00;
        } else {
            ironColor = 0xFF00BFFF;
        }
        this.ironClusterButton.setBorderColor(ironColor);

        int pureColor;
        if (!QuestManager.isIronClusterCompleted()) {
            pureColor = 0xFFFF0000;
        } else if (QuestManager.isPureIronOreCompleted()) {
            pureColor = 0xFF00FF00;
        } else {
            pureColor = 0xFF00BFFF;
        }
        this.pureIronOreButton.setBorderColor(pureColor);


        super.render(ms, mouseX, mouseY, pt);
    }
}