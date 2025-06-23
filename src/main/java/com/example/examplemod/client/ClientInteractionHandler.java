package com.example.examplemod.client;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.client.screen.WaterChoiceScreen;    // ← ваш экран выбора воды
import net.minecraft.client.Minecraft;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = ExampleMod.MODID,
        bus   = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT
)
public class ClientInteractionHandler {

    /** При ПКМ по источнику воды открываем экран выбора */
    @SubscribeEvent
    public static void onRightClickBlock(RightClickBlock ev) {
        World world = ev.getWorld();
        if (!world.isClientSide()) return;

        BlockPos pos = ev.getPos();
        FluidState fs = world.getFluidState(pos);
        if (fs.getType() == Fluids.WATER && fs.isSource()) {
            // открываем ваш WaterChoiceScreen
            Minecraft.getInstance().setScreen(new WaterChoiceScreen());
            ev.setCanceled(true);
        }
    }

}
