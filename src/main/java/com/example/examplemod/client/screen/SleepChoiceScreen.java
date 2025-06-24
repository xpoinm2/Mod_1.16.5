package com.example.examplemod.client.screen;

import com.example.examplemod.network.ActivityPacket;
import com.example.examplemod.network.ModNetworkHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

@OnlyIn(Dist.CLIENT)
public class SleepChoiceScreen extends Screen {
    private static final int BTN_W = 100, BTN_H = 20;
    private TextFieldWidget input;

    public SleepChoiceScreen() {
        super(new StringTextComponent("Сон"));
    }

    @Override
    protected void init() {
        int x = (this.width - BTN_W) / 2;
        int y = (this.height / 2) - 40;
        input = new TextFieldWidget(this.font, x, y, BTN_W, BTN_H, StringTextComponent.EMPTY);
        input.setValue("1");
        this.children.add(input);
        this.setInitialFocus(input);

        this.addButton(new ExtendedButton(x, y + 25, BTN_W, BTN_H,
                new StringTextComponent("Полежать"), btn -> {
            ModNetworkHandler.CHANNEL.sendToServer(new ActivityPacket(ActivityPacket.TYPE_LIE, 0));
            Minecraft.getInstance().setScreen(null);
        }));
        this.addButton(new ExtendedButton(x, y + 50, BTN_W, BTN_H,
                new StringTextComponent("Спать"), btn -> {
            int h = 1;
            try { h = Integer.parseInt(input.getValue()); } catch (NumberFormatException ignored) {}
            h = MathHelper.clamp(h, 1, 8);
            ModNetworkHandler.CHANNEL.sendToServer(new ActivityPacket(ActivityPacket.TYPE_SLEEP, h));
            Minecraft.getInstance().setScreen(null);
        }));
        super.init();
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(ms);
        super.render(ms, mouseX, mouseY, partialTicks);
        input.render(ms, mouseX, mouseY, partialTicks);
    }
}