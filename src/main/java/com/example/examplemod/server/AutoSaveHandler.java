// === FILE src/main/java/com/example/examplemod/AutoSaveHandler.java
package com.example.examplemod.server;

import com.example.examplemod.ExampleMod;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;         // <- именно отсюда

public class AutoSaveHandler {
    public static void onServerStopping(FMLServerStoppingEvent event) {
        // вот этот метод точно есть в FMLServerStoppingEvent
        event.getServer().saveAllChunks(true, true, true);
    }
}
