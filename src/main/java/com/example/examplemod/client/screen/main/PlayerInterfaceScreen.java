package com.example.examplemod.client.screen.main;

import com.example.examplemod.client.FramedButton;
import com.example.examplemod.network.ModNetworkHandler;
import com.example.examplemod.network.OpenCraftingPacket;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PlayerInterfaceScreen extends Screen {
    private static final int WIDTH = 150;
    private static final int HEIGHT = 150;
    private final Screen parent;

    public PlayerInterfaceScreen(Screen parent) {
        super(new StringTextComponent("Интерфейс игрока"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int x0 = 5;
        int y0 = 5;
        this.addButton(new FramedButton(x0 + 15, y0 + 40, 120, 20, "Здоровье", 0xFFFFFF00, 0xFFFF0000,
                b -> this.minecraft.setScreen(new HealthScreen(this))));
        this.addButton(new FramedButton(x0 + 15, y0 + 70, 120, 20, "Создание", 0xFFFFFF00, 0xFFFF0000,
                b -> {
                    ModNetworkHandler.CHANNEL.sendToServer(new OpenCraftingPacket());
                    this.minecraft.setScreen(null);
                }));
        this.addButton(new FramedButton(x0 + 15, y0 + 100, 120, 20, "Информация", 0xFFFFFF00, 0xFFFF0000,
                b -> this.minecraft.setScreen(new InfoScreen(this))));
        super.init();
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float pt) {
        this.renderBackground(ms);
        int x0 = 5;
        int y0 = 5;
        fill(ms, x0 - 1, y0 - 1, x0 + WIDTH + 1, y0 + HEIGHT + 1, 0xFF00FF00);
        fill(ms, x0, y0, x0 + WIDTH, y0 + HEIGHT, 0xFF000000);
        drawCenteredString(ms, this.font, this.title, x0 + WIDTH / 2, y0 + 10, 0xFF00FFFF);
        super.render(ms, mouseX, mouseY, pt);
    }

    @Override
    public void onClose() {
        if (this.minecraft != null && this.minecraft.player != null && this.minecraft.level != null && parent != null) {
            this.minecraft.setScreen(parent);
        } else {
            super.onClose();
        }
    }
}
