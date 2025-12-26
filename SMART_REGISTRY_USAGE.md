# SmartItemRegistry - Практическое Руководство

## Проблема

При добавлении 1000+ предметов, традиционный подход создаёт огромное количество повторяющегося кода:

```java
// Традиционный подход (100+ строк для 20 металлов × 5 форм)
public static final RegistryObject<Item> COPPER_INGOT = ModRegistries.ITEMS.register("copper_ingot", 
    () -> new Item(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));
public static final RegistryObject<Item> TIN_INGOT = ModRegistries.ITEMS.register("tin_ingot",
    () -> new Item(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));
public static final RegistryObject<Item> ZINC_INGOT = ModRegistries.ITEMS.register("zinc_ingot",
    () -> new Item(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)));
// ... ещё 97 строк
```

## Решение: SmartItemRegistry

`SmartItemRegistry` позволяет регистрировать группы похожих предметов через категории:

```java
// Новый подход (3 строки для 100 предметов!)
SmartItemRegistry.category("ingot", metal -> 
    new Item(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)))
    .addVariants("copper", "tin", "zinc", "bronze", "steel", /* ... ещё 15 металлов */)
    .namingPattern("{variant}_ingot");

SmartItemRegistry.registerAll();
```

## Пошаговое Использование

### Шаг 1: Определить категорию

```java
private static void initSmartRegistry() {
    // Категория для слитков металлов
    SmartItemRegistry.category("ingot", variant -> 
        new Item(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)))
        .addVariants("copper", "tin", "bronze", "steel", "aluminum", "titanium")
        .namingPattern("{variant}_ingot");
}
```

Это создаст предметы:
- `copper_ingot`
- `tin_ingot`
- `bronze_ingot`
- `steel_ingot`
- `aluminum_ingot`
- `titanium_ingot`

### Шаг 2: Паттерны именования

Можно настроить формат имён:

```java
// Паттерн 1: {variant}_{category}
.namingPattern("{variant}_plate")  // → copper_plate, iron_plate

// Паттерн 2: {category}_{variant}
.namingPattern("dust_{variant}")    // → dust_copper, dust_iron

// Паттерн 3: Произвольный формат
.namingPattern("raw_{variant}_ore") // → raw_copper_ore, raw_iron_ore
```

### Шаг 3: Кастомные фабрики

Для предметов с особой логикой:

```java
// Категория для инструментов с прочностью
SmartItemRegistry.category("hammer", material -> {
    int durability = material.equals("stone") ? 131 : material.equals("iron") ? 250 : 59;
    return new Item(new Item.Properties()
        .tab(ModCreativeTabs.EXAMPLE_TAB)
        .durability(durability));
}).addVariants("stone", "iron", "diamond")
  .namingPattern("{variant}_hammer");

// Категория для еды
SmartItemRegistry.category("berry", berry -> {
    int nutrition = 1;
    float saturation = 0.2f;
    return new Item(new Item.Properties()
        .tab(ModCreativeTabs.EXAMPLE_TAB)
        .food(new Food.Builder()
            .nutrition(nutrition)
            .saturationMod(saturation)
            .build()));
}).addVariants("strawberry", "blueberry", "blackberry")
  .namingPattern("{variant}");
```

### Шаг 4: Регистрация

После определения всех категорий:

```java
SmartItemRegistry.registerAll();
```

## Полный Пример

```java
public class ModItems {
    public static void init() {
        initSmartRegistry();
    }
    
    private static void initSmartRegistry() {
        // 1. Слитки металлов (6 предметов)
        SmartItemRegistry.category("ingot", metal -> 
            new Item(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)))
            .addVariants("copper", "tin", "bronze", "steel", "aluminum", "titanium")
            .namingPattern("{variant}_ingot");
        
        // 2. Пластины металлов (6 предметов)
        SmartItemRegistry.category("plate", metal -> 
            new Item(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)))
            .addVariants("copper", "tin", "bronze", "steel", "aluminum", "titanium")
            .namingPattern("{variant}_plate");
        
        // 3. Пыль металлов (6 предметов)
        SmartItemRegistry.category("dust", metal -> 
            new Item(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)))
            .addVariants("copper", "tin", "bronze", "steel", "aluminum", "titanium")
            .namingPattern("{variant}_dust");
        
        // 4. Шестерёнки (5 предметов)
        SmartItemRegistry.category("gear", metal -> 
            new Item(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB)))
            .addVariants("iron", "gold", "copper", "steel", "bronze")
            .namingPattern("{variant}_gear");
        
        // 5. Провода (4 предмета)
        SmartItemRegistry.category("wire", metal -> 
            new Item(new Item.Properties().tab(ModCreativeTabs.EXAMPLE_TAB).stacksTo(64)))
            .addVariants("copper", "gold", "aluminum", "steel")
            .namingPattern("{variant}_wire");
        
        // ИТОГО: 27 предметов за 5 категорий вместо 27 отдельных регистраций!
        
        SmartItemRegistry.registerAll();
    }
}
```

**Результат**: 27 строк кода вместо 135+ строк (экономия 80%)

## Расширение Для Будущего

Когда понадобятся новые металлы, просто добавь их в список:

```java
// Было: 6 металлов
.addVariants("copper", "tin", "bronze", "steel", "aluminum", "titanium")

// Стало: 12 металлов (добавлено 6 новых)
.addVariants(
    "copper", "tin", "bronze", "steel", "aluminum", "titanium",
    "nickel", "silver", "platinum", "lead", "zinc", "cobalt"
)
```

Это автоматически создаст:
- 12 слитков
- 12 пластин
- 12 пылей
- **ИТОГО: 36 новых предметов за одну строку!**

## Утилиты SmartItemRegistry

### Проверка принадлежности к категории

```java
// В событиях или логике игры
if (SmartItemRegistry.isInCategory(itemStack.getItem(), "ingot")) {
    // Это слиток металла
    System.out.println("Player holds a metal ingot!");
}
```

### Получение всех предметов категории

```java
// Для массовых операций
Set<Item> allGears = SmartItemRegistry.getCategory("gear");
for (Item gear : allGears) {
    // Применить общую логику ко всем шестерёнкам
}
```

### Статистика регистрации

```java
// В логах при запуске
System.out.println(SmartItemRegistry.getStats());
// Output:
// SmartItemRegistry Statistics:
//   Total categories: 5
//   Total items: 27
//   - ingot: 6 items
//   - plate: 6 items
//   - dust: 6 items
//   - gear: 5 items
//   - wire: 4 items
```

## Преимущества

1. **Читаемость**: Код стал в 5 раз короче
2. **Масштабируемость**: Легко добавлять новые варианты
3. **Консистентность**: Все предметы категории создаются единообразно
4. **Производительность**: Не влияет на runtime, оптимизация только compile-time
5. **Утилиты**: Встроенные методы для работы с категориями

## Когда НЕ использовать

- Для уникальных предметов с кастомной логикой (например, `HotRoastedOreItem`)
- Для предметов с сильно различающимися свойствами
- Для малого количества предметов (< 5 в группе)

В таких случаях лучше регистрировать вручную для ясности кода.

