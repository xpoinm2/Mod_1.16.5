package com.example.examplemod.client;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.client.screen.WaterChoiceScreen;    // ← ваш экран выбора воды
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent.ClickInputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = ExampleMod.MODID,
        bus   = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT
)
public class ClientInteractionHandler {

    /** При нажатии ПКМ без предмета по воде открываем экран выбора */
    @SubscribeEvent
    public static void onRightClick(ClickInputEvent ev) {
        if (!ev.isUseItem() || ev.isCanceled()) return;

        Minecraft mc = Minecraft.getInstance();
        ClientPlayerEntity player = mc.player;
        if (player == null || !player.getMainHandItem().isEmpty()) return;

        double reach = mc.gameMode.getPickRange();
        RayTraceResult res = player.pick(reach, 0.0F, false);
        if (res.getType() != RayTraceResult.Type.BLOCK) return;

        BlockPos pos = ((BlockRayTraceResult) res).getBlockPos();
        World world = player.level;
        FluidState fs = world.getFluidState(pos);
        // accept both source and flowing water blocks
        // accept both source and flowing water blocks
        if (fs.is(FluidTags.WATER)) {
            mc.setScreen(new WaterChoiceScreen());
            ev.setSwingHand(false); // don't swing animation
            ev.setCanceled(true);

        }
    }

}
