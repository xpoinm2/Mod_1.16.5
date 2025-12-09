package com.example.examplemod.client.render;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.item.BoneTongsItem;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class BoneTongsModeOverlay {
    private static long lastModeChangeTime = 0;
    private static BoneTongsItem.Mode lastMode = null;
    private static final long DISPLAY_DURATION_MS = 1500; // 1.5 секунды

    @SubscribeEvent
    public static void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }

        ItemStack mainHand = minecraft.player.getMainHandItem();
        if (!(mainHand.getItem() instanceof BoneTongsItem)) {
            return;
        }

        BoneTongsItem.Mode mode = BoneTongsItem.getMode(mainHand);

        // Проверяем, изменился ли режим
        if (lastMode == null || lastMode != mode) {
            lastMode = mode;
            lastModeChangeTime = System.currentTimeMillis();
        }

        // Проверяем, прошло ли меньше 1.5 секунд с момента изменения
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastModeChangeTime > DISPLAY_DURATION_MS) {
            return; // Не показываем текст, если прошло больше 1.5 секунд
        }

        String modeKey;
        switch (mode) {
            case PICK_INPUT:
                modeKey = "overlay.examplemod.tongs_mode.pick_input";
                break;
            case PICK_OUTPUT:
                modeKey = "overlay.examplemod.tongs_mode.pick_output";
                break;
            case PICK_FUEL:
                modeKey = "overlay.examplemod.tongs_mode.pick_fuel";
                break;
            case PLACE_INPUT:
                modeKey = "overlay.examplemod.tongs_mode.place_input";
                break;
            case PLACE_OUTPUT:
                modeKey = "overlay.examplemod.tongs_mode.place_output";
                break;
            case PLACE_FUEL:
                modeKey = "overlay.examplemod.tongs_mode.place_fuel";
                break;
            default:
                modeKey = "overlay.examplemod.tongs_mode.pick_input";
                break;
        }

        String modeText = new TranslationTextComponent(modeKey).getString();

        MatrixStack matrixStack = event.getMatrixStack();
        FontRenderer font = minecraft.font;

        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int screenHeight = minecraft.getWindow().getGuiScaledHeight();

        // Позиция текста - снизу по центру, над иконками
        int x = screenWidth / 2 - font.width(modeText) / 2;
        int y = screenHeight - 70; // Над хотбаром

        // Рисуем текст с тенью
        font.drawShadow(matrixStack, modeText, x, y, 0xFFFFFF);
    }
}
