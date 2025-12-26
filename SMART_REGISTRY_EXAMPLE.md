# 📦 Пример использования SmartItemRegistry

## Зачем это нужно?

При добавлении 1000+ предметов **традиционная регистрация** превращается в кошмар:

```java
// ModItems.java - традиционный способ
public static final RegistryObject<Item> COPPER_INGOT = 
    ModRegistries.ITEMS.register("copper_ingot", () -> new Item(...));
public static final RegistryObject<Item> TIN_INGOT = 
    ModRegistries.ITEMS.register("tin_ingot", () -> new Item(...));
public static final RegistryObject<Item> ZINC_INGOT = 
    ModRegistries.ITEMS.register("zinc_ingot", () -> new Item(...));
// ... 1000 строк копипасты
```

**Проблемы**:
- 🔴 10000+ строк кода для 1000 предметов
- 🔴 Трудно добавить новый металл (нужно найти и изменить 10+ мест)
- 🔴 Легко допустить ошибку в именовании
- 🔴 Сложно поддерживать консистентность

---

## ✅ Решение: SmartItemRegistry

### Простой пример: 50 металлов × 4 типа = 200 предметов

```java
// ModItems.java - с SmartItemRegistry
public class ModItems {
    // Список всех металлов (можешь вынести в отдельный файл или JSON)
    private static final String[] METALS = {
        "copper", "tin", "zinc", "bronze", "brass", "steel",
        "aluminum", "titanium", "platinum", "silver", "gold",
        "iron", "nickel", "cobalt", "lead", "uranium",
        // ... ещё 35 металлов
    };
    
    public static void register() {
        // 1. Регистрируем категории с фабриками
        SmartItemRegistry.category("ingot", 
            metal -> new Item(new Item.Properties().tab(ModCreativeTabs.MATERIALS))
        );
        
        SmartItemRegistry.category("ore", 
            metal -> new Item(new Item.Properties().tab(ModCreativeTabs.MATERIALS))
        );
        
        SmartItemRegistry.category("plate", 
            metal -> new Item(new Item.Properties().tab(ModCreativeTabs.MATERIALS))
        );
        
        SmartItemRegistry.category("wire", 
            metal -> new Item(new Item.Properties().tab(ModCreativeTabs.MATERIALS))
        );
        
        // 2. Добавляем варианты ко всем категориям
        SmartItemRegistry.variants("ingot", METALS);
        SmartItemRegistry.variants("ore", METALS);
        SmartItemRegistry.variants("plate", METALS);
        SmartItemRegistry.variants("wire", METALS);
        
        // 3. Регистрируем всё одной строкой
        SmartItemRegistry.registerAll();
        
        // Итого: 200 предметов в ~15 строках кода!
    }
}
```

**Результат**: 
- `copper_ingot`, `copper_ore`, `copper_plate`, `copper_wire`
- `tin_ingot`, `tin_ore`, `tin_plate`, `tin_wire`
- ... для всех 50 металлов

---

## 🎨 Продвинутые возможности

### 1. Кастомные паттерны именования

```java
// По умолчанию: {variant}_{category} → copper_ingot
SmartItemRegistry.category("ingot", metal -> new Item(...));

// Можно изменить паттерн:
SmartItemRegistry.category("raw_ore", metal -> new Item(...))
    .namingPattern("raw_{variant}") // → raw_copper
    .addVariants("copper", "tin", "zinc");

SmartItemRegistry.category("dust", metal -> new Item(...))
    .namingPattern("{variant}_dust") // → copper_dust
    .addVariants("copper", "tin", "zinc");
```

### 2. Разные фабрики для разных категорий

```java
// Простые предметы
SmartItemRegistry.category("ingot", 
    metal -> new Item(new Item.Properties().tab(ModCreativeTabs.MATERIALS))
);

// Инструменты с кастомными классами
SmartItemRegistry.category("pickaxe", 
    material -> new CustomPickaxeItem(getTier(material), ...)
);

// Еда с разными свойствами
SmartItemRegistry.category("cooked_meat", 
    animal -> new Item(new Item.Properties()
        .food(getFoodProperties(animal))
        .tab(ModCreativeTabs.FOOD))
);
```

### 3. Быстрый поиск по категориям

```java
// Проверка, является ли предмет инструментом
if (SmartItemRegistry.isInCategory(item, "tool")) {
    // Это инструмент!
}

// Получить все слитки для крафта
Set<Item> allIngots = SmartItemRegistry.getCategory("ingot");
for (Item ingot : allIngots) {
    // Работаем со всеми слитками
}
```

---

## 🌟 Реальный пример: Мод с технологическим деревом

```java
public class ModItems {
    // Эпохи: каменный век → бронзовый век → железный век → современность
    private static final String[] STONE_AGE = {"flint", "obsidian", "bone"};
    private static final String[] BRONZE_AGE = {"copper", "tin", "bronze"};
    private static final String[] IRON_AGE = {"iron", "steel", "carbon_steel"};
    private static final String[] MODERN = {"aluminum", "titanium", "tungsten", "platinum"};
    
    public static void register() {
        // Инструменты для каждой эпохи
        registerTools("stone_age", STONE_AGE, StoneTierHelper::getTier);
        registerTools("bronze_age", BRONZE_AGE, BronzeTierHelper::getTier);
        registerTools("iron_age", IRON_AGE, IronTierHelper::getTier);
        registerTools("modern", MODERN, ModernTierHelper::getTier);
        
        // Ресурсы для каждой эпохи
        registerResources("stone_age", STONE_AGE);
        registerResources("bronze_age", BRONZE_AGE);
        registerResources("iron_age", IRON_AGE);
        registerResources("modern", MODERN);
        
        SmartItemRegistry.registerAll();
        
        // Статистика для отладки
        LOGGER.info(SmartItemRegistry.getStats());
    }
    
    private static void registerTools(String era, String[] materials, 
                                     Function<String, IItemTier> tierSupplier) {
        SmartItemRegistry.category(era + "_pickaxe", 
            mat -> new PickaxeItem(tierSupplier.apply(mat), 1, -2.8F, props())
        ).addVariants(materials);
        
        SmartItemRegistry.category(era + "_axe", 
            mat -> new AxeItem(tierSupplier.apply(mat), 6, -3.1F, props())
        ).addVariants(materials);
        
        SmartItemRegistry.category(era + "_sword", 
            mat -> new SwordItem(tierSupplier.apply(mat), 3, -2.4F, props())
        ).addVariants(materials);
    }
    
    private static void registerResources(String era, String[] materials) {
        SmartItemRegistry.category(era + "_ingot", mat -> new Item(props()))
            .addVariants(materials);
        SmartItemRegistry.category(era + "_ore", mat -> new Item(props()))
            .addVariants(materials);
    }
    
    private static Item.Properties props() {
        return new Item.Properties().tab(ModCreativeTabs.MATERIALS);
    }
}
```

**Результат**: Сотни предметов организованы по эпохам, легко добавлять новые эпохи/материалы.

---

## 📊 Сравнение

| Метрика | Традиционная регистрация | SmartItemRegistry |
|---------|--------------------------|-------------------|
| **Строк кода для 200 предметов** | ~1000 строк | ~20 строк |
| **Время добавления нового металла** | 5-10 минут (найти все места) | 5 секунд (добавить в массив) |
| **Вероятность ошибки** | Высокая (копипаста) | Низкая (автоматика) |
| **Консистентность именования** | Вручную | Автоматическая |
| **Поиск предметов по категории** | O(n) перебор всех | O(1) через Set |

---

## 🚀 Как начать использовать

### Шаг 1: Копируй `SmartItemRegistry.java` в свой проект

Файл уже создан: `src/main/java/com/example/examplemod/util/SmartItemRegistry.java`

### Шаг 2: Пример простой регистрации

```java
// ModItems.java
public static void register() {
    // Регистрируем категорию
    SmartItemRegistry.category("gem", 
        name -> new Item(new Item.Properties().tab(ModCreativeTabs.MATERIALS))
    );
    
    // Добавляем варианты
    SmartItemRegistry.variants("gem", 
        "ruby", "sapphire", "emerald", "topaz", "amethyst"
    );
    
    // Регистрируем всё
    SmartItemRegistry.registerAll();
}
```

### Шаг 3: Проверь результат

```java
// В игре или в логе:
LOGGER.info(SmartItemRegistry.getStats());

// Вывод:
// SmartItemRegistry Statistics:
//   Total categories: 1
//   Total items: 5
//   - gem: 5 items
```

---

## ⚠️ Важные замечания

1. **Вызывай `registerAll()` ОДИН РАЗ** - после настройки всех категорий
2. **Не смешивай** традиционную регистрацию и SmartItemRegistry для одной категории
3. **Используй для схожих предметов** - не обязательно регистрировать ВСЁ через систему
4. **Текстуры всё равно нужны** - система не создаёт текстуры автоматически

---

## 🎯 Когда использовать

### ✅ Используй SmartItemRegistry для:
- Металлы (руды, слитки, пластины, провода, шестерни)
- Камни (все виды булыжника, гладкого камня, кирпичей)
- Инструменты (наборы инструментов для разных материалов)
- Еда (вариации блюд)
- Ресурсы с вариациями (дерево разных типов)

### ❌ НЕ используй для:
- Уникальные предметы (артефакты, квестовые вещи)
- Предметы с сложной логикой (каждый уникален)
- Малое количество предметов (< 10)

---

## 📈 Итоговый выигрыш

При добавлении **1000 предметов**:
- **10000 строк → 100 строк кода** (99% меньше)
- **Добавление нового металла: 10 минут → 10 секунд** (100x быстрее)
- **Автоматическая консистентность** именования
- **Встроенный поиск** по категориям O(1)

**Это превращает поддержку огромного мода из кошмара в удовольствие!** 🎉

