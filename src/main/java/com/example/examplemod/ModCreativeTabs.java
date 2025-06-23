// === FILE src/main/java/com/example/examplemod/ModItems.java src/main/java/com/example/examplemod/ModCreativeTabs.java
package com.example.examplemod;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * Устанавливает и регистрирует креативную вкладку для вашего мода.
 */
public class ModCreativeTabs {
    // Название вкладки = модID
    public static final ItemGroup EXAMPLE_TAB = new ItemGroup(ExampleMod.MODID) {
        @Override
        public ItemStack makeIcon() {
            // Возвращаем иконку вкладки: предмет, зарегистрированный в ModItems
            return new ItemStack(ModItems.EXAMPLE_ICON.get());
        }
    };

    /**
     * Инициализировать вкладку (вызывается из ExampleMod).
     * В 1.16.5 вкладки регистрируются при загрузке класса, поэтому метод может быть пустым,
     * но нужна ссылка, чтобы избежать ошибок "Cannot resolve symbol 'register'".
     */
    public static void register(IEventBus bus) {
        // В 1.16.5 не требуется дополнительная регистрация вкладки,
        // но метод оставлен для консистентности.
    }
}
