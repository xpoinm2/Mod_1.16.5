package com.example.examplemod.client.screen;

import com.example.examplemod.network.ActivityPacket;
import com.example.examplemod.network.ModNetworkHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

@OnlyIn(Dist.CLIENT)
public class ActivityScreen extends Screen {
    private static final int BTN_W = 100, BTN_H = 20;

    public ActivityScreen() {
        super(new StringTextComponent("Выберите действие"));
    }

    @Override
    protected void init() {
        int x = (this.width - BTN_W) / 2;
        int y = (this.height / 2) - BTN_H - 5;
        this.addButton(new ExtendedButton(x, y, BTN_W, BTN_H,
                new StringTextComponent("Сесть"), btn -> {
            ModNetworkHandler.CHANNEL.sendToServer(new ActivityPacket());
            Minecraft.getInstance().setScreen(null);
        }));

// The old "Лечь" option is removed, only sitting is available
        super.init();
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(ms);
        super.render(ms, mouseX, mouseY, partialTicks);
    }
}