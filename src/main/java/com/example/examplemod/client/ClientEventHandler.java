package com.example.examplemod.client;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.capability.IPlayerStats;
import com.example.examplemod.capability.PlayerStatsProvider;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

/**
 * Клиентский оверлей и кнопка в инвентаре
 */
@Mod.EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientEventHandler {
    private static boolean overlayOpen = false;

    // 1) Кнопка с сердечком в инвентаре
    @SubscribeEvent
    public static void onGuiInit(GuiScreenEvent.InitGuiEvent.Post ev) {
        if (!(ev.getGui() instanceof InventoryScreen)) return;
        InventoryScreen gui = (InventoryScreen) ev.getGui();
        int left = (gui.width - gui.getXSize()) / 2;
        int top  = (gui.height - gui.getYSize()) / 2;

        // Вставляем нашу кнопку
        Button btn = new GreenManButton(
                left + gui.getXSize() - 19, // немного левее
                top + 2,
                16, 20,
                StringTextComponent.EMPTY,
                b -> overlayOpen = !overlayOpen
        );
        ev.addWidget(btn);
    }

    // 2) Отрисовка HUD-оверлея
    @SubscribeEvent
    public static void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Post ev) {
        if (!overlayOpen || !(ev.getGui() instanceof InventoryScreen)) return;
        MatrixStack ms = ev.getMatrixStack();
        Minecraft mc = Minecraft.getInstance();
        FontRenderer font = mc.font;

        // Позиция оверлея
        int x0 = 0, y0 = 0;

        // Черный фон размером 150x150 с зелёной рамкой 2 пикселя
        AbstractGui.fill(ms, x0, y0, x0 + 150, y0 + 150, 0xFF00FF00); // рамка
        AbstractGui.fill(ms, x0 + 2, y0 + 2, x0 + 148, y0 + 148, 0xFF000000);


        // Размеры полосок
        float baseW = 80 / 1.5f;
        float baseH = 8  / 1.5f;
        float baseSpacing = 16 / 1.5f;
        int w = Math.round(baseW * 1.25f);    // полоски увеличены
        int h = Math.round(baseH * 1.25f);
        int spacing = Math.round(baseSpacing * 1.25f);

        // Получаем статы
        mc.player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent((IPlayerStats stats) -> {
            int thirst  = stats.getThirst();
            int fatigue = stats.getFatigue();
            int disease = stats.getDisease();


            int bx = x0 + 10;
            int by = y0 + 10;

            // Жажда (синяя)
            int filled = thirst * w / 100;
            // рамка
            AbstractGui.fill(ms, bx - 1, by - 1, bx + w + 1, by + h + 1, 0xFFFFFF00);
            AbstractGui.fill(ms, bx, by, bx + w, by + h, 0xFF5555FF);
            AbstractGui.fill(ms, bx, by, bx + filled, by + h, 0xFF0000FF);
            String text = "Thirst: " + thirst + "/100";
            ms.pushPose();
            ms.scale(0.5f, 0.5f, 1f);
            float tx = (bx + (w - font.width(text) * 0.5f) / 2f) * 2f;
            float ty = (by + (h - font.lineHeight * 0.5f) / 2f) * 2f;
            font.draw(ms, text, tx, ty, 0xFFFFFF);
            ms.popPose();

            // Усталость (оранжевый)
            by += spacing;
            filled = fatigue * w / 100;
            AbstractGui.fill(ms, bx - 1, by - 1, bx + w + 1, by + h + 1, 0xFFFFFF00);
            AbstractGui.fill(ms, bx, by, bx + w, by + h, 0xFFFFAA55);
            AbstractGui.fill(ms, bx, by, bx + filled, by + h, 0xFFFF5500);
            text = "Fatigue: " + fatigue + "/100";
            ms.pushPose();
            ms.scale(0.5f, 0.5f, 1f);
            tx = (bx + (w - font.width(text) * 0.5f) / 2f) * 2f;
            ty = (by + (h - font.lineHeight * 0.5f) / 2f) * 2f;
            font.draw(ms, text, tx, ty, 0xFFFFFF);
            ms.popPose();

            // Болезнь (зелёный)
            by += spacing;
            filled = disease * w / 100;
            AbstractGui.fill(ms, bx - 1, by - 1, bx + w + 1, by + h + 1, 0xFFFFFF00);
            AbstractGui.fill(ms, bx, by, bx + w, by + h, 0xFFAAFFAA);
            AbstractGui.fill(ms, bx, by, bx + filled, by + h, 0xFF00AA00);
            text = "Disease: " + disease + "/100";
            ms.pushPose();
            ms.scale(0.5f, 0.5f, 1f);
            tx = (bx + (w - font.width(text) * 0.5f) / 2f) * 2f;
            ty = (by + (h - font.lineHeight * 0.5f) / 2f) * 2f;
            font.draw(ms, text, tx, ty, 0xFFFFFF);
            ms.popPose();
        });

// 3) Раньше тут рисовался зелёный человечек. Теперь ничего не рисуем
    }

    // 4) Закрытие оверлея по ESC
    @SubscribeEvent
    public static void onKeyPressed(GuiScreenEvent.KeyboardKeyPressedEvent ev) {
        if (!overlayOpen || !(ev.getGui() instanceof InventoryScreen)) return;
        if (ev.getKeyCode() == GLFW.GLFW_KEY_ESCAPE) {
            overlayOpen = false;
            ev.setCanceled(true);
        }
    }
}
