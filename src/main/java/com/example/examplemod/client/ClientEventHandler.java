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
        int x0 = 0, y0 = 0;

        // Черный фон размером 150x150
        AbstractGui.fill(ms, x0, y0, x0 + 150, y0 + 150, 0xFF000000);

        int mx = ev.getMouseX();
        int my = ev.getMouseY();

        // Размеры полосок
        int w = Math.round(80 / 1.5f); // полоски стали меньше
        int h = Math.round(8  / 1.5f);
        int spacing = Math.round(16 / 1.5f);

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
            if (mx >= bx && mx <= bx + w && my >= by && my <= by + h) {
                font.draw(ms, "Thirst: " + thirst + "/100", bx, by - 10, 0xFFFFFF);
            }

            // Усталость (оранжевый)
            by += spacing;
            filled = fatigue * w / 100;
            AbstractGui.fill(ms, bx - 1, by - 1, bx + w + 1, by + h + 1, 0xFFFFFF00);
            AbstractGui.fill(ms, bx, by, bx + w, by + h, 0xFFFFAA55);
            AbstractGui.fill(ms, bx, by, bx + filled, by + h, 0xFFFF5500);
            if (mx >= bx && mx <= bx + w && my >= by && my <= by + h) {
                font.draw(ms, "Fatigue: " + fatigue + "/100", bx, by - 10, 0xFFFFFF);
            }

            // Болезнь (зелёный)
            by += spacing;
            filled = disease * w / 100;
            AbstractGui.fill(ms, bx - 1, by - 1, bx + w + 1, by + h + 1, 0xFFFFFF00);
            AbstractGui.fill(ms, bx, by, bx + w, by + h, 0xFFAAFFAA);
            AbstractGui.fill(ms, bx, by, bx + filled, by + h, 0xFF00AA00);
            if (mx >= bx && mx <= bx + w && my >= by && my <= by + h) {
                font.draw(ms, "Disease: " + disease + "/100", bx, by - 10, 0xFFFFFF);
            }
        });

        // 3) Рисуем пиксельного человечка чуть ниже полосок
        int manY = y0 + 10 + (h + spacing) * 2 + h + 10;
        GreenManButton.drawPixelMan(ms, x0 + 5, manY);
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
