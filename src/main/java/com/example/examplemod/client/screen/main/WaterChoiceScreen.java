package com.example.examplemod.client.screen.main;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.network.ModNetworkHandler;
import com.example.examplemod.network.DrinkWaterPacket;
import com.example.examplemod.network.MixWaterPacket;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WaterChoiceScreen extends Screen {
    private static final int BTN_WIDTH = 100, BTN_HEIGHT = 20;

    public WaterChoiceScreen() {
        super(new StringTextComponent("Выберите действие"));
    }

    @Override
    protected void init() {
        int x = (this.width - BTN_WIDTH) / 2;
        int y = (this.height / 2) - BTN_HEIGHT - 5;
        // Кнопка «Попить»
        this.addButton(new ExtendedButton(x, y, BTN_WIDTH, BTN_HEIGHT,
                        new StringTextComponent("Пoпить"), btn -> {
                    ModNetworkHandler.CHANNEL.sendToServer(new DrinkWaterPacket());
                    Minecraft.getInstance().setScreen(null);
                })
        );
        // Кнопка «Перемешать»
        this.addButton(new ExtendedButton(x, y + BTN_HEIGHT + 5, BTN_WIDTH, BTN_HEIGHT,
                        new StringTextComponent("Перемешать"), btn -> {
                    ModNetworkHandler.CHANNEL.sendToServer(new MixWaterPacket());
                    Minecraft.getInstance().setScreen(null);
                })
        );
        super.init();
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(ms);
        super.render(ms, mouseX, mouseY, partialTicks);
    }
}
