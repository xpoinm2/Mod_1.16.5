# 🚀 Продвинутые оптимизации для масштабирования

## Уже реализовано
- ✅ Модульная система механик с throttling
- ✅ Батчинг сетевых пакетов (80% меньше трафика)
- ✅ Кэширование биома/температуры (50-70% меньше getBiome())
- ✅ Централизованная регистрация через DeferredRegister

---

## 🎨 1. Оптимизация текстур и моделей (КРИТИЧНО для 1000+ предметов)

### Проблема
При 1000+ предметах все текстуры/модели загружаются в память сразу → **гигабайты RAM**

### Решение A: Atlasing (Текстурные атласы)

Minecraft автоматически создаёт атласы, но можно помочь:

```java
// Группируй предметы с одинаковым base-текстурам
// resources/assets/examplemod/textures/item/ores/
//   ore_base.png
//   ore_copper_overlay.png
//   ore_tin_overlay.png

// Используй один base + overlay вместо 100 отдельных текстур
```

**Выигрыш**: 1000 текстур 16x16 = ~1 MB → с атласами ~200-300 KB

### Решение B: Процедурная генерация текстур

Для схожих предметов (руды, слитки) генерируй текстуры программно:

```java
public class ProceduralTextureGenerator {
    public static void generateOreTextures() {
        BufferedImage base = loadTexture("ore_base.png");
        for (String metal : List.of("copper", "tin", "zinc", "bronze")) {
            BufferedImage overlay = loadTexture("ore_" + metal + "_overlay.png");
            BufferedImage result = combineTextures(base, overlay);
            saveTexture(result, "generated_" + metal + "_ore.png");
        }
    }
}
```

**Выигрыш**: Меньше файлов в jar, проще добавлять новые металлы

---

## 📦 2. Умная регистрация предметов

### Паттерн: Автоматическая регистрация по категориям

```java
public class SmartItemRegistry {
    private static final Map<String, ItemCategory> CATEGORIES = new HashMap<>();
    
    public static class ItemCategory {
        private final String name;
        private final Function<String, Item> itemFactory;
        private final List<String> variants;
        
        public ItemCategory(String name, Function<String, Item> factory) {
            this.name = name;
            this.itemFactory = factory;
            this.variants = new ArrayList<>();
        }
        
        public void addVariant(String variant) {
            variants.add(variant);
        }
    }
    
    // Регистрация категории
    public static void registerCategory(String category, Function<String, Item> factory) {
        CATEGORIES.put(category, new ItemCategory(category, factory));
    }
    
    // Добавление вариантов
    public static void addVariants(String category, String... variants) {
        ItemCategory cat = CATEGORIES.get(category);
        for (String variant : variants) {
            cat.addVariant(variant);
        }
    }
    
    // Массовая регистрация
    public static void registerAll() {
        CATEGORIES.forEach((categoryName, category) -> {
            category.variants.forEach(variant -> {
                String itemId = variant + "_" + categoryName;
                Item item = category.itemFactory.apply(variant);
                ModRegistries.ITEMS.register(itemId, () -> item);
            });
        });
    }
}

// Использование:
public class ModItems {
    public static void init() {
        // Регистрируем категорию "ingot" с фабрикой
        SmartItemRegistry.registerCategory("ingot", 
            metal -> new Item(new Item.Properties().tab(ModCreativeTabs.MATERIALS))
        );
        
        // Добавляем 50 металлов одной строкой
        SmartItemRegistry.addVariants("ingot", 
            "copper", "tin", "zinc", "bronze", "brass", "steel", 
            "aluminum", "titanium", "platinum", "silver", "gold"
            // ... ещё 40 металлов
        );
        
        // Аналогично для руд, пластин, проводов, шестерён
        SmartItemRegistry.registerCategory("ore", metal -> new OreItem(...));
        SmartItemRegistry.addVariants("ore", /* те же 50 металлов */);
        
        SmartItemRegistry.registerCategory("plate", metal -> new Item(...));
        SmartItemRegistry.addVariants("plate", /* те же 50 металлов */);
        
        // Итого: 50 металлов × 4 категории = 200 предметов в 10 строках кода!
        SmartItemRegistry.registerAll();
    }
}
```

**Выигрыш**: 
- 1000 строк регистрации → 50 строк
- Легко добавить новый металл везде сразу
- Автоматическая консистентность названий

---

## 🔧 3. Использование примитивов вместо боксированных типов

### Проблема

```java
// ПЛОХО: каждый Integer = 16+ байт (объект в heap)
private Map<UUID, Integer> thirstValues = new HashMap<>();

// При 100 игроках: 100 × 16 = 1.6 KB только на Integer'ы
// При 1000 механиках: 1.6 MB только на обёртки!
```

### Решение: Используй специализированные коллекции

```java
// ХОРОШО: примитивы напрямую (библиотека fastutil или Eclipse Collections)
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

private Object2IntOpenHashMap<UUID> thirstValues = new Object2IntOpenHashMap<>();

// Или для int → int:
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

private Int2IntOpenHashMap tickCounters = new Int2IntOpenHashMap();
```

**Добавь в build.gradle**:
```gradle
dependencies {
    implementation 'it.unimi.dsi:fastutil:8.5.12'
}
```

**Выигрыш**:
- **50-70% меньше памяти** для числовых значений
- **Быстрее доступ** (нет boxing/unboxing)
- Критично при 100+ механиках с Map<UUID, Integer>

---

## ⚡ 4. Оптимизация итераций по игрокам

### Проблема

```java
// ПЛОХО: каждый тик 100 механик итерируют по всем игрокам
for (ServerPlayerEntity player : world.players()) {
    // проверки...
}
```

При 100 механиках = 100 итераций по списку игроков **каждый тик**!

### Решение: Централизованная итерация

```java
// MechanicScheduler.java
@SubscribeEvent
public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
    if (event.phase != TickEvent.Phase.END || 
        !(event.player instanceof ServerPlayerEntity)) return;

    ServerPlayerEntity player = (ServerPlayerEntity) event.player;
    
    // Одна итерация по модулям для каждого игрока
    for (IMechanicModule module : ModMechanics.modules()) {
        int interval = module.playerIntervalTicks();
        if (interval > 0 && player.tickCount % interval == 0) {
            module.onPlayerTick(player);
        }
    }
}
```

**Уже реализовано в твоём проекте!** ✅

Но важно понимать: это **в 100 раз эффективнее**, чем если бы каждая механика сама итерировала по игрокам.

---

## 🗂️ 5. Правильный выбор коллекций

### Частые ошибки

```java
// ПЛОХО для энумов:
Map<BiomeCategory, Integer> temps = new HashMap<>();

// ПЛОХО для маленьких списков с частыми поисками:
List<Item> tools = new ArrayList<>();
if (tools.contains(item)) { ... } // O(n) каждый раз
```

### Правильно

```java
// ХОРОШО для энумов (намного быстрее HashMap):
import java.util.EnumMap;
Map<BiomeCategory, Integer> temps = new EnumMap<>(BiomeCategory.class);

// ХОРОШО для маленьких множеств с частыми поисками:
Set<Item> tools = new HashSet<>();
if (tools.contains(item)) { ... } // O(1)

// ХОРОШО для фиксированных списков:
Set<Item> tools = Set.of(HAMMER, AXE, PICKAXE); // Неизменяемый, быстрый
```

**Выигрыш**:
- `EnumMap` в **2-3 раза быстрее** `HashMap` для энумов
- `Set.of()` создаёт оптимизированные неизменяемые коллекции
- `HashSet.contains()` = O(1) vs `ArrayList.contains()` = O(n)

---

## 🎮 6. Оптимизация для клиента (рендеринг)

### Проблема
При 1000+ предметов рендеринг GUI/инвентаря может лагать

### Решение A: Batch rendering

```java
// ClientProxy.java или клиентский ивент
@SubscribeEvent
public void onRenderInventory(RenderInventoryEvent event) {
    // Группируй айтемы с одинаковыми текстурами
    Map<ResourceLocation, List<ItemStack>> batches = new HashMap<>();
    
    for (ItemStack stack : inventory) {
        ResourceLocation texture = getTexture(stack);
        batches.computeIfAbsent(texture, k -> new ArrayList<>()).add(stack);
    }
    
    // Рендери батчами (меньше bind/unbind текстур)
    batches.forEach((texture, stacks) -> {
        bindTexture(texture);
        stacks.forEach(this::renderItem);
    });
}
```

### Решение B: LOD (Level of Detail) для моделей

```java
// Упрощённые модели для предметов, которые далеко
public class LODItemRenderer {
    public void render(ItemStack stack, float distance) {
        if (distance > 10.0f) {
            renderSimpleIcon(stack); // Простая текстура
        } else if (distance > 5.0f) {
            renderLowPolyModel(stack); // Упрощённая модель
        } else {
            renderFullModel(stack); // Полная детализация
        }
    }
}
```

---

## 💾 7. Ленивая инициализация для тяжёлых ресурсов

### Паттерн: Lazy Singleton

```java
public class HeavyResource {
    private static HeavyResource INSTANCE;
    
    // Не создаём, пока не понадобится
    public static HeavyResource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HeavyResource();
        }
        return INSTANCE;
    }
    
    private HeavyResource() {
        // Дорогая инициализация
        loadHugeDataset();
        precomputeExpensiveTables();
    }
}
```

### Применение к механикам

```java
// ModMechanics.java
public static void init() {
    // Базовые механики - всегда
    register(new ThirstMechanic());
    
    // Тяжёлые механики - только если включены
    if (Config.ENABLE_WEATHER_SYSTEM.get()) {
        register(new WeatherMechanic()); // Загружает много данных
    }
    
    if (Config.ENABLE_MAGIC_SYSTEM.get()) {
        register(new ManaMechanic());
        register(new SpellSystemMechanic()); // Очень тяжёлая
    }
}
```

**Выигрыш**: Игроки могут отключить тяжёлые системы в конфиге

---

## 🧪 8. Профилирование и мониторинг

### Встроенный мониторинг производительности

```java
// util/PerformanceMonitor.java
public class PerformanceMonitor {
    private static final Map<String, PerformanceStats> STATS = new HashMap<>();
    
    public static class PerformanceStats {
        long totalTime;
        long callCount;
        long maxTime;
        
        public void record(long time) {
            totalTime += time;
            callCount++;
            maxTime = Math.max(maxTime, time);
        }
        
        public double getAverage() {
            return callCount == 0 ? 0 : (double) totalTime / callCount;
        }
    }
    
    public static void startTimer(String name) {
        // Записываем время начала в ThreadLocal
    }
    
    public static void stopTimer(String name) {
        long duration = /* вычислить */;
        STATS.computeIfAbsent(name, k -> new PerformanceStats()).record(duration);
    }
    
    public static void logReport() {
        LOGGER.info("=== Performance Report ===");
        STATS.forEach((name, stats) -> {
            LOGGER.info("{}: avg={} ms, max={} ms, calls={}",
                name, stats.getAverage(), stats.maxTime / 1000000, stats.callCount);
        });
    }
}

// Использование:
public void expensiveOperation() {
    PerformanceMonitor.startTimer("biome_calculation");
    try {
        // ... твой код
    } finally {
        PerformanceMonitor.stopTimer("biome_calculation");
    }
}
```

### Автоматическое логирование медленных операций

```java
// MechanicScheduler.java - улучшение
public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
    // ...
    for (IMechanicModule module : ModMechanics.modules()) {
        long start = System.nanoTime();
        module.onPlayerTick(player);
        long duration = System.nanoTime() - start;
        
        // Автоматически логируем медленные вызовы
        if (duration > 10_000_000) { // > 10ms
            LOGGER.warn("SLOW MECHANIC: {} took {} ms for player {}",
                module.id(), duration / 1_000_000, player.getName().getString());
        }
    }
}
```

---

## 🌐 9. Оптимизация сети (дополнительно)

### Компрессия пакетов для больших данных

```java
// network/CompressedPacket.java
public class CompressedPacket {
    private final byte[] compressedData;
    
    public CompressedPacket(CompoundNBT nbt) {
        byte[] rawData = serializeNBT(nbt);
        this.compressedData = compress(rawData); // GZIP/LZ4
    }
    
    public static void encode(CompressedPacket pkt, PacketBuffer buf) {
        buf.writeVarInt(pkt.compressedData.length);
        buf.writeBytes(pkt.compressedData);
    }
    
    private static byte[] compress(byte[] data) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             GZIPOutputStream gzip = new GZIPOutputStream(baos)) {
            gzip.write(data);
            gzip.finish();
            return baos.toByteArray();
        }
    }
}
```

**Когда использовать**: Для больших структур данных (квесты, статистика, большие инвентари)

**Выигрыш**: 50-80% меньше размер пакета для текстовых данных

---

## 📊 10. Метрики и телеметрия

### Dashboard для разработки

```java
// debug/DebugMetrics.java
public class DebugMetrics {
    public static void logMetrics() {
        if (!Config.DEBUG_MODE.get()) return;
        
        Runtime rt = Runtime.getRuntime();
        long usedMemory = (rt.totalMemory() - rt.freeMemory()) / 1024 / 1024;
        
        LOGGER.info("=== METRICS ===");
        LOGGER.info("Memory: {} MB / {} MB", usedMemory, rt.maxMemory() / 1024 / 1024);
        LOGGER.info("Registered items: {}", ModRegistries.ITEMS.getEntries().size());
        LOGGER.info("Active mechanics: {}", ModMechanics.modules().size());
        LOGGER.info("Biome cache size: {}", BiomeTemperatureCache.getCacheSize());
        LOGGER.info("Average TPS: {}", getAverageTPS());
    }
}

// Вызывай каждые 5 минут в MechanicScheduler
```

---

## 🎯 Приоритеты внедрения (дополнительно)

| Приоритет | Оптимизация | Когда критично |
|-----------|-------------|----------------|
| 🔴 **ВЫСОКИЙ** | Текстурные атласы/процедурная генерация | При 500+ предметах с текстурами |
| 🔴 **ВЫСОКИЙ** | Умная регистрация (SmartItemRegistry) | При 200+ предметах одного типа |
| 🟡 **СРЕДНИЙ** | Fastutil коллекции | Если профайлер показывает boxing/unboxing |
| 🟡 **СРЕДНИЙ** | EnumMap для энумов | Сразу (простая замена) |
| 🟡 **СРЕДНИЙ** | Встроенный мониторинг | Для отладки производительности |
| 🟢 **НИЗКИЙ** | Компрессия пакетов | Только для очень больших пакетов |
| 🟢 **НИЗКИЙ** | LOD рендеринг | Если есть проблемы с FPS на клиенте |

---

## 📈 Итоговый чеклист оптимизации

### Уже реализовано ✅
- ✅ Модульная система механик
- ✅ Централизованная регистрация (DeferredRegister)
- ✅ Throttling механик (не каждый тик)
- ✅ Батчинг сетевых пакетов (SyncAllStatsPacket)
- ✅ Кэширование биома/температуры
- ✅ Профилирование механик (опционально)
- ✅ Унификация данных (PlayerStats capability)

### Рекомендовано для будущего 📋
- 📌 **SmartItemRegistry** - когда начнёшь добавлять сотни схожих предметов
- 📌 **Fastutil коллекции** - замени HashMap<UUID, Integer> на Object2IntOpenHashMap
- 📌 **EnumMap вместо HashMap** - для всех энумов (простая замена)
- 📌 **Ленивая инициализация** - для опциональных тяжёлых систем
- 📌 **PerformanceMonitor** - добавь логирование медленных операций
- 📌 **Текстурные оптимизации** - при 500+ предметах с уникальными текстурами

### Опционально 🎨
- Процедурная генерация текстур
- Компрессия больших пакетов
- LOD для рендеринга
- Batch rendering клиентских GUI

---

## 🧮 Финальная математика

### Сценарий: 1000 предметов, 100 механик, 20 игроков

**Без оптимизаций:**
- 5 пакетов × 20 игроков = 100 пакетов при логине
- `world.getBiome()` × 2 механики × 20 TPS × 20 игроков = 800 вызовов/сек
- HashMap<UUID, Integer> × 100 механик × 20 игроков = 32 KB только на Integer'ы
- Отдельная регистрация 1000 предметов = 10000+ строк кода

**С оптимизациями:**
- 1 пакет × 20 игроков = 20 пакетов (↓80%)
- `world.getBiome()` с кэшем = ~5-10 вызовов/сек (↓95%)
- Object2IntOpenHashMap = 10 KB (↓70%)
- SmartItemRegistry = ~100 строк кода (↓99%)

**Итог: ~80-90% снижение нагрузки** 🎉

---

## 🎓 Главные принципы

1. **Профилируй, не гадай** - используй Spark для поиска узких мест
2. **Оптимизируй рано** - архитектура важнее микрооптимизаций
3. **Измеряй результаты** - встроенная телеметрия
4. **Масштабируй постепенно** - не оптимизируй всё сразу
5. **Документируй** - через год забудешь, почему так сделано

**Помни**: Преждевременная оптимизация - корень зла, но хорошая архитектура с самого начала = залог успеха! 🚀

