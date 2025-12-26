package com.example.examplemod.util;

import com.example.examplemod.ModRegistries;
import net.minecraft.item.Item;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.Function;

/**
 * Умная система регистрации предметов для масштабирования до 1000+ предметов.
 * 
 * Вместо:
 * <pre>
 * ModRegistries.ITEMS.register("copper_ingot", () -> new Item(...));
 * ModRegistries.ITEMS.register("tin_ingot", () -> new Item(...));
 * ModRegistries.ITEMS.register("zinc_ingot", () -> new Item(...));
 * // ... 100 строк
 * </pre>
 * 
 * Используй:
 * <pre>
 * SmartItemRegistry.category("ingot", metal -> new Item(...));
 * SmartItemRegistry.variants("ingot", "copper", "tin", "zinc", ...);
 * SmartItemRegistry.registerAll();
 * // 3 строки вместо 100!
 * </pre>
 */
public class SmartItemRegistry {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<String, ItemCategory> CATEGORIES = new LinkedHashMap<>();
    private static final Map<String, Set<Item>> CATEGORY_ITEMS = new HashMap<>();
    
    /**
     * Категория предметов с фабрикой для создания.
     */
    public static class ItemCategory {
        private final String name;
        private final Function<String, Item> itemFactory;
        private final List<String> variants;
        private String namingPattern = "{variant}_{category}"; // По умолчанию: copper_ingot
        
        public ItemCategory(String name, Function<String, Item> factory) {
            this.name = name;
            this.itemFactory = factory;
            this.variants = new ArrayList<>();
        }
        
        /**
         * Добавить варианты (например, металлы для слитков).
         */
        public ItemCategory addVariants(String... variants) {
            this.variants.addAll(Arrays.asList(variants));
            return this;
        }
        
        /**
         * Установить паттерн именования.
         * Поддерживаемые плейсхолдеры: {variant}, {category}
         * 
         * Примеры:
         * - "{variant}_{category}" → copper_ingot
         * - "{category}_{variant}" → ingot_copper
         * - "raw_{variant}" → raw_copper
         */
        public ItemCategory namingPattern(String pattern) {
            this.namingPattern = pattern;
            return this;
        }
        
        /**
         * Сгенерировать ID предмета по паттерну.
         */
        public String generateId(String variant) {
            return namingPattern
                    .replace("{variant}", variant)
                    .replace("{category}", name);
        }
    }
    
    /**
     * Зарегистрировать категорию предметов.
     * 
     * @param categoryName название категории (ingot, ore, plate, etc.)
     * @param itemFactory фабрика для создания предмета (получает название варианта)
     * @return категория для дальнейшей настройки
     */
    public static ItemCategory category(String categoryName, Function<String, Item> itemFactory) {
        if (CATEGORIES.containsKey(categoryName)) {
            LOGGER.warn("Category '{}' already registered, overwriting", categoryName);
        }
        ItemCategory category = new ItemCategory(categoryName, itemFactory);
        CATEGORIES.put(categoryName, category);
        return category;
    }
    
    /**
     * Добавить варианты к категории (сокращённый метод).
     * 
     * @param categoryName название категории
     * @param variants варианты (например, металлы)
     */
    public static void variants(String categoryName, String... variants) {
        ItemCategory category = CATEGORIES.get(categoryName);
        if (category == null) {
            throw new IllegalArgumentException("Category '" + categoryName + "' not registered");
        }
        category.addVariants(variants);
    }
    
    /**
     * Массовая регистрация всех предметов всех категорий.
     * Вызывай после настройки всех категорий.
     */
    public static void registerAll() {
        int totalRegistered = 0;
        
        for (ItemCategory category : CATEGORIES.values()) {
            Set<Item> categorySet = new HashSet<>();
            
            for (String variant : category.variants) {
                String itemId = category.generateId(variant);
                Item item = category.itemFactory.apply(variant);
                
                ModRegistries.ITEMS.register(itemId, () -> item);
                categorySet.add(item);
                totalRegistered++;
                
                LOGGER.debug("Registered item: {}", itemId);
            }
            
            CATEGORY_ITEMS.put(category.name, categorySet);
        }
        
        LOGGER.info("SmartItemRegistry: Registered {} items across {} categories",
                totalRegistered, CATEGORIES.size());
    }
    
    /**
     * Получить все предметы категории (для быстрого поиска).
     * 
     * Использование:
     * <pre>
     * for (Item ingot : SmartItemRegistry.getCategory("ingot")) {
     *     // ... работа со слитками
     * }
     * </pre>
     */
    public static Set<Item> getCategory(String categoryName) {
        return CATEGORY_ITEMS.getOrDefault(categoryName, Collections.emptySet());
    }
    
    /**
     * Проверить, принадлежит ли предмет категории.
     * 
     * Использование:
     * <pre>
     * if (SmartItemRegistry.isInCategory(stack.getItem(), "tool")) {
     *     // Это инструмент
     * }
     * </pre>
     */
    public static boolean isInCategory(Item item, String categoryName) {
        Set<Item> category = CATEGORY_ITEMS.get(categoryName);
        return category != null && category.contains(item);
    }
    
    /**
     * Получить статистику регистрации (для отладки).
     */
    public static String getStats() {
        int totalItems = CATEGORY_ITEMS.values().stream()
                .mapToInt(Set::size)
                .sum();
        
        StringBuilder sb = new StringBuilder();
        sb.append("SmartItemRegistry Statistics:\n");
        sb.append("  Total categories: ").append(CATEGORIES.size()).append("\n");
        sb.append("  Total items: ").append(totalItems).append("\n");
        
        CATEGORY_ITEMS.forEach((category, items) -> {
            sb.append("  - ").append(category).append(": ").append(items.size()).append(" items\n");
        });
        
        return sb.toString();
    }
    
    /**
     * Очистить всю регистрацию (для тестов).
     */
    public static void clear() {
        CATEGORIES.clear();
        CATEGORY_ITEMS.clear();
    }
}

