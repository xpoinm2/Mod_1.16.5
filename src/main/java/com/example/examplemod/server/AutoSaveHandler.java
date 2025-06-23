// === FILE src/main/java/com/example/examplemod/AutoSaveHandler.java
package com.example.examplemod.server;

import com.example.examplemod.ExampleMod;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;         // <- именно отсюда
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = ExampleMod.MODID,
        bus   = Mod.EventBusSubscriber.Bus.FORGE
)
public class AutoSaveHandler {
    @SubscribeEvent
    public static void onServerStopping(FMLServerStoppingEvent event) {
        // вот этот метод точно есть в FMLServerStoppingEvent
        event.getServer().saveAllChunks(true, true, true);
    }
}
