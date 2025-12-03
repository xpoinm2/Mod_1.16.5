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

    private FramedButton gatheringButton;
    private FramedButton metallurgyButton;
    private FramedButton productionButton;
    private FramedButton agricultureButton;
    private FramedButton clothingButton;
    private FramedButton transportButton;
    private FramedButton medicineButton;
    private FramedButton jewelryButton;
    private FramedButton warButton;
    private FramedButton constructionButton;

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

        // Создаем кнопки направлений
        int tabW = (int)(120 * 1.4);
        int tabH = (int)(20 * 1.4);
        int centerX = panelX + (panelWidth - tabW) / 2;

        this.gatheringButton = new FramedButton(centerX, 0, tabW, tabH, "Собирательство", 0xFF000000, 0xFFFF0000,
                b -> this.minecraft.setScreen(new ProgressGatheringScreen(this)));
        this.metallurgyButton = new FramedButton(centerX, 0, tabW, tabH, "Металлургия", 0xFF000000, 0xFFFF0000,
                b -> this.minecraft.setScreen(new ProgressMetallurgyScreen(this)));
        this.productionButton = new FramedButton(centerX, 0, tabW, tabH, "Производство", 0xFF000000, 0xFFFF0000,
                b -> this.minecraft.setScreen(new ProgressProductionScreen(this)));
        this.agricultureButton = new FramedButton(centerX, 0, tabW, tabH, "Сельское хозяйство/Еда", 0xFF000000, 0xFFFF0000,
                b -> this.minecraft.setScreen(new ProgressAgricultureScreen(this)));
        this.clothingButton = new FramedButton(centerX, 0, tabW, tabH, "Одежда/Текстиль", 0xFF000000, 0xFFFF0000,
                b -> this.minecraft.setScreen(new ProgressClothingScreen(this)));
        this.transportButton = new FramedButton(centerX, 0, tabW, tabH, "Транспорт", 0xFF000000, 0xFFFF0000,
                b -> this.minecraft.setScreen(new ProgressTransportScreen(this)));
        this.medicineButton = new FramedButton(centerX, 0, tabW, tabH, "Медицина/Таблетки", 0xFF000000, 0xFFFF0000,
                b -> this.minecraft.setScreen(new ProgressMedicineScreen(this)));
        this.jewelryButton = new FramedButton(centerX, 0, tabW, tabH, "Украшения/Ювелирка", 0xFF000000, 0xFFFF0000,
                b -> this.minecraft.setScreen(new ProgressJewelryScreen(this)));
        this.warButton = new FramedButton(centerX, 0, tabW, tabH, "Война/Оружие", 0xFF000000, 0xFFFF0000,
                b -> this.minecraft.setScreen(new ProgressWarScreen(this)));
        this.constructionButton = new FramedButton(centerX, 0, tabW, tabH, "Строительство/Жилище", 0xFF000000, 0xFFFF0000,
                b -> this.minecraft.setScreen(new ProgressConstructionScreen(this)));

        // Добавляем кнопки в список (для обработки кликов)
        this.addButton(gatheringButton);
        this.addButton(metallurgyButton);
        this.addButton(productionButton);
        this.addButton(agricultureButton);
        this.addButton(clothingButton);
        this.addButton(transportButton);
        this.addButton(medicineButton);
        this.addButton(jewelryButton);
        this.addButton(warButton);
        this.addButton(constructionButton);

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
        int tabW = (int)(120 * 1.4); // Увеличено в 1.4 раза
        int spacing = (int)(25 * 1.4); // Увеличено в 1.4 раза
        int currentY = y;

        // Центрирование кнопок по горизонтали
        int centerX = x + (innerWidth - tabW) / 2;

        // Позиционируем и отображаем кнопки
        gatheringButton.x = centerX;
        gatheringButton.y = currentY;
        currentY += spacing;

        metallurgyButton.x = centerX;
        metallurgyButton.y = currentY;
        currentY += spacing;

        productionButton.x = centerX;
        productionButton.y = currentY;
        currentY += spacing;

        agricultureButton.x = centerX;
        agricultureButton.y = currentY;
        currentY += spacing;

        clothingButton.x = centerX;
        clothingButton.y = currentY;
        currentY += spacing;

        transportButton.x = centerX;
        transportButton.y = currentY;
        currentY += spacing;

        medicineButton.x = centerX;
        medicineButton.y = currentY;
        currentY += spacing;

        jewelryButton.x = centerX;
        jewelryButton.y = currentY;
        currentY += spacing;

        warButton.x = centerX;
        warButton.y = currentY;
        currentY += spacing;

        constructionButton.x = centerX;
        constructionButton.y = currentY;
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
