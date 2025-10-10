package com.example.examplemod.client;

import com.example.examplemod.ExampleMod;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = ExampleMod.MODID,
        bus   = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT
)
public class ClientInteractionHandler {
    private ClientInteractionHandler() {
        // no-op: класс сохраняется для потенциальных будущих клиентских обработчиков
    }
}
