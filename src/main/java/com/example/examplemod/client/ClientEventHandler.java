package com.example.examplemod.client;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.client.screen.PlayerInterfaceScreen;
import com.example.examplemod.client.FramedButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.CraftingScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientEventHandler {

    @SubscribeEvent
    public static void onGuiInit(GuiScreenEvent.InitGuiEvent.Post ev) {
        if (!(ev.getGui() instanceof InventoryScreen)) return;
        InventoryScreen gui = (InventoryScreen) ev.getGui();
        int left = (gui.width - gui.getXSize()) / 2;
        int top  = (gui.height - gui.getYSize()) / 2;

        Button btn = new GreenManButton(
                left + gui.getXSize() - 19,
                top + 2,
                16, 20,
                StringTextComponent.EMPTY,
                b -> Minecraft.getInstance().setScreen(new PlayerInterfaceScreen(gui))
        );
        ev.addWidget(btn);
    }

    @SubscribeEvent
    public static void onCraftingInit(GuiScreenEvent.InitGuiEvent.Post ev) {
        if (!(ev.getGui() instanceof CraftingScreen)) return;
        CraftingScreen gui = (CraftingScreen) ev.getGui();
        int left = (gui.width - gui.getXSize()) / 2;
        int top  = (gui.height - gui.getYSize()) / 2;

        Button btn = new FramedButton(left + 5, top + 5, 20, 20, "<", 0xFFFFFF00, 0xFFFFFFFF,
                b -> {
                    Minecraft mc = Minecraft.getInstance();
                    mc.player.closeContainer();
                    mc.setScreen(new PlayerInterfaceScreen(new InventoryScreen(mc.player)));
                });
        ev.addWidget(btn);
    }
}
