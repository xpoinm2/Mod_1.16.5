package com.example.examplemod.client.screen;

import com.example.examplemod.client.FramedButton;
import com.example.examplemod.client.GuiUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AncientMetallurgyEraScreen extends Screen {
    private final Screen parent;
    private ScrollArea scrollArea;

    public AncientMetallurgyEraScreen(Screen parent) {
        super(new StringTextComponent("Древняя металлургия"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.addButton(new FramedButton(5, 5, 20, 20, "<", 0xFFFFFF00, 0xFFFFFFFF,
                b -> this.minecraft.setScreen(parent)));

        int panelX = 10;
        int panelY = 50;
        int panelWidth = this.width - 20;
        int panelHeight = this.height - 60;

        this.scrollArea = new ScrollArea(panelX, panelY, panelWidth, panelHeight);

        super.init();
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(ms);
        GuiUtil.drawPanel(ms, 10, 10, this.width - 20, this.height - 20);
        drawCenteredString(ms, this.font, this.title, this.width / 2, 30, 0xFF00FFFF);

        scrollArea.render(ms, mouseX, mouseY, partialTicks, (area, matrix, x, y, innerWidth, mX, mY, pt) ->
            renderDirections(area, matrix, x, y, innerWidth, mX, mY, pt));

        super.render(ms, mouseX, mouseY, partialTicks);
    }

    private int renderDirections(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth, int mouseX, int mouseY, float partialTicks) {
        int tabW = 120;
        int tabH = 20;
        int spacing = 25;
        int currentY = y;

        // Собирательство
        FramedButton gatheringButton = new FramedButton(x, currentY, tabW, tabH, "Собирательство", 0xFF000000, 0xFFFF0000,
                b -> this.minecraft.setScreen(new ProgressGatheringScreen(this)));
        gatheringButton.render(ms, mouseX, mouseY, partialTicks);
        currentY += spacing;

        // Металлургия
        FramedButton metallurgyButton = new FramedButton(x, currentY, tabW, tabH, "Металлургия", 0xFF000000, 0xFFFF0000,
                b -> this.minecraft.setScreen(new ProgressMetallurgyScreen(this)));
        metallurgyButton.render(ms, mouseX, mouseY, partialTicks);
        currentY += spacing;

        // Производство
        FramedButton productionButton = new FramedButton(x, currentY, tabW, tabH, "Производство", 0xFF000000, 0xFFFF0000,
                b -> this.minecraft.setScreen(new ProgressProductionScreen(this)));
        productionButton.render(ms, mouseX, mouseY, partialTicks);
        currentY += spacing;

        // Сельское хозяйство/Еда
        FramedButton agricultureButton = new FramedButton(x, currentY, tabW, tabH, "Сельское хозяйство/Еда", 0xFF000000, 0xFFFF0000,
                b -> this.minecraft.setScreen(new ProgressAgricultureScreen(this)));
        agricultureButton.render(ms, mouseX, mouseY, partialTicks);
        currentY += spacing;

        // Одежда/Текстиль
        FramedButton clothingButton = new FramedButton(x, currentY, tabW, tabH, "Одежда/Текстиль", 0xFF000000, 0xFFFF0000,
                b -> this.minecraft.setScreen(new ProgressClothingScreen(this)));
        clothingButton.render(ms, mouseX, mouseY, partialTicks);
        currentY += spacing;

        // Транспорт
        FramedButton transportButton = new FramedButton(x, currentY, tabW, tabH, "Транспорт", 0xFF000000, 0xFFFF0000,
                b -> this.minecraft.setScreen(new ProgressTransportScreen(this)));
        transportButton.render(ms, mouseX, mouseY, partialTicks);
        currentY += spacing;

        // Медицина/Таблетки
        FramedButton medicineButton = new FramedButton(x, currentY, tabW, tabH, "Медицина/Таблетки", 0xFF000000, 0xFFFF0000,
                b -> this.minecraft.setScreen(new ProgressMedicineScreen(this)));
        medicineButton.render(ms, mouseX, mouseY, partialTicks);
        currentY += spacing;

        // Украшения/Ювелирка
        FramedButton jewelryButton = new FramedButton(x, currentY, tabW, tabH, "Украшения/Ювелирка", 0xFF000000, 0xFFFF0000,
                b -> this.minecraft.setScreen(new ProgressJewelryScreen(this)));
        jewelryButton.render(ms, mouseX, mouseY, partialTicks);
        currentY += spacing;

        // Война/Оружие
        FramedButton warButton = new FramedButton(x, currentY, tabW, tabH, "Война/Оружие", 0xFF000000, 0xFFFF0000,
                b -> this.minecraft.setScreen(new ProgressWarScreen(this)));
        warButton.render(ms, mouseX, mouseY, partialTicks);
        currentY += spacing;

        // Строительство/Жилище
        FramedButton constructionButton = new FramedButton(x, currentY, tabW, tabH, "Строительство/Жилище", 0xFF000000, 0xFFFF0000,
                b -> this.minecraft.setScreen(new ProgressConstructionScreen(this)));
        constructionButton.render(ms, mouseX, mouseY, partialTicks);
        currentY += spacing;

        return currentY;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (scrollArea.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (scrollArea.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (scrollArea.mouseReleased(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (scrollArea.mouseScrolled(mouseX, mouseY, delta)) {
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }
}
