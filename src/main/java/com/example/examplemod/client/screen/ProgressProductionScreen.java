package com.example.examplemod.client.screen;

import com.example.examplemod.client.FramedButton;
import com.example.examplemod.client.ItemIconButton;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ProgressProductionScreen extends Screen {
    private final Screen parent;
    private ItemIconButton planksButton;

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
        this.addButton(new ItemIconButton(x, y, new ItemStack(Items.OAK_PLANKS),
                b -> this.minecraft.setScreen(new PlanksQuestScreen(this))));
        this.planksButton = new ItemIconButton(x, y, new ItemStack(Items.OAK_PLANKS),
                b -> this.minecraft.setScreen(new PlanksQuestScreen(this)));
        this.addButton(this.planksButton);
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
        super.render(ms, mouseX, mouseY, pt);
        if (PlanksQuestScreen.isCompleted()) {
            drawString(ms, this.font, "✔", this.planksButton.x + this.planksButton.getWidth() + 4,
                    this.planksButton.y + 6, 0xFF00FF00);
        }
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }
}