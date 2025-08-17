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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import java.util.Arrays;

@OnlyIn(Dist.CLIENT)
public class ProgressProductionScreen extends Screen {
    private final Screen parent;
    private ItemIconButton planksButton;
    private ItemIconButton slabsButton;
    private ItemIconButton stoneToolsButton;

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
        int spacing = 50;
        this.planksButton = new ItemIconButton(x, y, new ItemStack(Items.OAK_PLANKS),
                b -> this.minecraft.setScreen(new PlanksQuestScreen(this)),
                () -> Arrays.asList(new StringTextComponent("Доски"), new StringTextComponent("Нет требований")));
        this.addButton(this.planksButton);

        this.slabsButton = new ItemIconButton(x + spacing, y, new ItemStack(Items.OAK_SLAB),
                b -> this.minecraft.setScreen(new SlabsQuestScreen(this)),
                () -> Arrays.asList(new StringTextComponent("Плиты"), new StringTextComponent("Требуется: Доски")));
        this.addButton(this.slabsButton);

        this.stoneToolsButton = new ItemIconButton(x + spacing * 2, y,
                new ItemStack(ModItems.STONE_PICKAXE.get()),
                b -> this.minecraft.setScreen(new StoneToolsQuestScreen(this)),
                () -> Arrays.asList(new StringTextComponent("Каменные инструменты"),
                        new StringTextComponent("Требуется: Доски, Плиты, Оттесанный камень")));
        this.addButton(this.stoneToolsButton);
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

        boolean stoneUnlocked = QuestManager.isPlanksCompleted() &&
                QuestManager.isSlabsCompleted() &&
                QuestManager.isHewnStonesCompleted();
        int toolsColor;
        if (!stoneUnlocked) {
            toolsColor = 0xFFFF0000; // locked
        } else if (QuestManager.isStoneToolsCompleted()) {
            toolsColor = 0xFF00FF00; // completed
        } else {
            toolsColor = 0xFF00BFFF; // available
        }
        this.stoneToolsButton.setBorderColor(toolsColor);

        super.render(ms, mouseX, mouseY, pt);
    }
}