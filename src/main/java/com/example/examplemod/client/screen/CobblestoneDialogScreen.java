package com.example.examplemod.client.screen;

import com.example.examplemod.ModBlocks;
import com.example.examplemod.client.FramedButton;
import com.example.examplemod.network.ModNetworkHandler;
import com.example.examplemod.network.PlaceAnvilPacket;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CobblestoneDialogScreen extends Screen {

    public static BlockPos targetPos; // Позиция блока, по которому ударили
    private FramedButton anvilButton;
    private FramedButton cancelButton;

    public CobblestoneDialogScreen() {
        super(new StringTextComponent("Что делаем"));
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Кнопка "Наковальня"
        this.anvilButton = new FramedButton(centerX - 100, centerY - 20, 200, 20,
                "Наковальня", 0xFF00FF00, 0xFFFFFFFF, button -> {
            // Отправляем пакет на сервер для размещения наковальни
            if (targetPos != null) {
                ModNetworkHandler.CHANNEL.sendToServer(new PlaceAnvilPacket(targetPos));
            }
            this.onClose();
        });

        this.addButton(this.anvilButton);
        // Пока не добавляем кнопку отмены, чтобы было только "Наковальня"
        // this.addButton(this.cancelButton);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        // Затемненный фон
        this.renderBackground(matrixStack);

        // Заголовок
        String title = "Что делаем";
        int titleWidth = this.font.width(title);
        this.font.draw(matrixStack, title, (this.width - titleWidth) / 2, this.height / 2 - 50, 0xFFFFFF);

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}