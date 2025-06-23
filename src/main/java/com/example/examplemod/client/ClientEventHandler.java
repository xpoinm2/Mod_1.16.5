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

    // 1) Кнопка «зелёного человечка» в инвентаре
    @SubscribeEvent
    public static void onGuiInit(GuiScreenEvent.InitGuiEvent.Post ev) {
        if (!(ev.getGui() instanceof InventoryScreen)) return;
        InventoryScreen gui = (InventoryScreen) ev.getGui();
        int left = (gui.width - gui.getXSize()) / 2;
        int top  = (gui.height - gui.getYSize()) / 2;

        // Вставляем нашу кнопку
        Button btn = new GreenManButton(
                left + gui.getXSize() - 18,
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
        int x0 = 10, y0 = 10;

        // Полупрозрачный фон
        AbstractGui.fill(ms, x0, y0, x0 + 100, y0 + 60, 0xAA000000);

        // Получаем статы
        mc.player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent((IPlayerStats stats) -> {
            int thirst  = stats.getThirst();
            int fatigue = stats.getFatigue();
            int disease = stats.getDisease();

            int w = 80, h = 8;
            int bx = x0 + 10;
            int by = y0 + 10;

            // Жажда (синяя)
            int filled = thirst * w / 100;
            AbstractGui.fill(ms, bx, by, bx + w, by + h, 0xFF5555FF);
            AbstractGui.fill(ms, bx, by, bx + filled, by + h, 0xFF0000FF);
            font.draw(ms, "Thirst: " + thirst, bx, by - 10, 0xFFFFFF);

            // Усталость (оранжевый)
            by += 16;
            filled = fatigue * w / 100;
            AbstractGui.fill(ms, bx, by, bx + w, by + h, 0xFFFFAA55);
            AbstractGui.fill(ms, bx, by, bx + filled, by + h, 0xFFFF5500);
            font.draw(ms, "Fatigue: " + fatigue, bx, by - 10, 0xFFFFFF);

            // Болезнь (зелёный)
            by += 16;
            filled = disease * w / 100;
            AbstractGui.fill(ms, bx, by, bx + w, by + h, 0xFFAAFFAA);
            AbstractGui.fill(ms, bx, by, bx + filled, by + h, 0xFF00AA00);
            font.draw(ms, "Disease: " + disease, bx, by - 10, 0xFFFFFF);
        });

        // 3) Рисуем пиксельного человечка внизу оверлея
        GreenManButton.drawPixelMan(ms, x0 + 5, y0 + 36);
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
