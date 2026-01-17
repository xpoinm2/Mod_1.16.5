package com.example.examplemod.client.screen.main;

import com.example.examplemod.network.ActivityPacket;
import com.example.examplemod.network.DrinkWaterPacket;
import com.example.examplemod.network.MixWaterPacket;
import com.example.examplemod.network.ModNetworkHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.fluid.FluidState;
import net.minecraft.tags.FluidTags;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

@OnlyIn(Dist.CLIENT)
public class ActivityScreen extends Screen {
    private static final int BTN_W = 100, BTN_H = 20;

    private ExtendedButton drinkButton;
    private ExtendedButton mixButton;

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

        int waterButtonY = y + BTN_H + 5;
        this.drinkButton = this.addButton(new ExtendedButton(x, waterButtonY, BTN_W, BTN_H,
                new StringTextComponent("Попить"), btn -> {
            ModNetworkHandler.CHANNEL.sendToServer(new DrinkWaterPacket());
            Minecraft.getInstance().setScreen(null);
        }));

        this.mixButton = this.addButton(new ExtendedButton(x, waterButtonY + BTN_H + 5, BTN_W, BTN_H,
                new StringTextComponent("Перемешать"), btn -> {
            ModNetworkHandler.CHANNEL.sendToServer(new MixWaterPacket());
            Minecraft.getInstance().setScreen(null);
        }));

        updateWaterButtonsState();

// The old "Лечь" option is removed, only sitting is available
        super.init();
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(ms);
        super.render(ms, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
    @Override
    public void tick() {
        super.tick();
        updateWaterButtonsState();
    }

    private void updateWaterButtonsState() {
        boolean canUseWater = isLookingAtWater();
        if (this.drinkButton != null) {
            this.drinkButton.active = canUseWater;
        }
        if (this.mixButton != null) {
            this.mixButton.active = canUseWater;
        }
    }

    private boolean isLookingAtWater() {
        Minecraft mc = Minecraft.getInstance();
        World world = mc.level;

        if (world == null || mc.player == null || mc.gameMode == null) {
            return false;
        }

        double reach = mc.gameMode.getPickRange();
        RayTraceResult hitResult = mc.player.pick(reach, 0.0F, true);

        if (hitResult.getType() != RayTraceResult.Type.BLOCK) {
            return false;
        }

        BlockPos pos = ((BlockRayTraceResult) hitResult).getBlockPos();
        FluidState fluid = world.getFluidState(pos);

        return fluid.is(FluidTags.WATER);
    }
}