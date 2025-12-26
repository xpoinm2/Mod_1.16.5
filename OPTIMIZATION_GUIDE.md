# 🚀 Руководство по оптимизации для 1000+ предметов и 100+ механик

## ✅ Уже реализовано

- ✅ Модульная система механик с throttling
- ✅ Профилирование механик
- ✅ Унификация данных через PlayerStats capability
- ✅ Централизованная регистрация контента (DeferredRegister)

---

## 🎯 Рекомендуемые оптимизации

### 1. 🌐 **КРИТИЧНО: Батчинг сетевых пакетов**

**Проблема**: Сейчас при логине отправляется **5 отдельных пакетов** для синхронизации статов:
- `SyncStatsPacket` (thirst + fatigue)
- `SyncColdPacket`
- `SyncHypothermiaPacket`
- `SyncVirusPacket`
- `SyncPoisonPacket`

**Влияние**: С ростом количества механик (100+) это может превратиться в 50+ пакетов при логине!

**Решение**: Создать один универсальный `SyncAllStatsPacket`, который отправляет все статы разом.

**Пример реализации**:

```java
// network/SyncAllStatsPacket.java
public class SyncAllStatsPacket {
    private final int thirst, fatigue, cold, hypothermia, virus, poison, disease, blood;

    public SyncAllStatsPacket(IPlayerStats stats) {
        this.thirst = stats.getThirst();
        this.fatigue = stats.getFatigue();
        this.cold = stats.getCold();
        this.hypothermia = stats.getHypothermia();
        this.virus = stats.getVirus();
        this.poison = stats.getPoison();
        this.disease = stats.getDisease();
        this.blood = stats.getBlood();
    }

    public static void encode(SyncAllStatsPacket pkt, PacketBuffer buf) {
        buf.writeInt(pkt.thirst);
        buf.writeInt(pkt.fatigue);
        buf.writeInt(pkt.cold);
        buf.writeInt(pkt.hypothermia);
        buf.writeInt(pkt.virus);
        buf.writeInt(pkt.poison);
        buf.writeInt(pkt.disease);
        buf.writeInt(pkt.blood);
    }

    public static SyncAllStatsPacket decode(PacketBuffer buf) {
        return new SyncAllStatsPacket(
            buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(),
            buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt()
        );
    }

    public static void handle(SyncAllStatsPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft.getInstance().player
                .getCapability(PlayerStatsProvider.PLAYER_STATS_CAP)
                .ifPresent(stats -> {
                    stats.setThirst(pkt.thirst);
                    stats.setFatigue(pkt.fatigue);
                    stats.setCold(pkt.cold);
                    stats.setHypothermia(pkt.hypothermia);
                    stats.setVirus(pkt.virus);
                    stats.setPoison(pkt.poison);
                    stats.setDisease(pkt.disease);
                    stats.setBlood(pkt.blood);
                });
        });
        ctx.get().setPacketHandled(true);
    }
}
```

**Использование**:
```java
// CapabilityHandler.java
player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
    ModNetworkHandler.CHANNEL.send(
        PacketDistributor.PLAYER.with(() -> player),
        new SyncAllStatsPacket(stats)  // Один пакет вместо 5!
    );
});
```

**Выигрыш**: 
- 5 пакетов → 1 пакет = **80% меньше сетевого трафика**
- Меньше overhead на обработку пакетов
- Атомарная синхронизация всех данных

---

### 2. 📦 **Data-driven регистрация предметов**

**Проблема**: При 1000+ предметах файл `ModItems.java` станет огромным (10000+ строк).

**Решение**: Использовать JSON/конфиги для массовой регистрации схожих предметов.

**Пример**: Создать категории предметов в JSON:

```json
// resources/data/examplemod/items/ores.json
{
  "category": "ores",
  "items": [
    { "id": "copper_ore", "stack_size": 64, "tab": "materials" },
    { "id": "tin_ore", "stack_size": 64, "tab": "materials" },
    { "id": "bronze_ore", "stack_size": 64, "tab": "materials" }
    // ... 100+ руд
  ]
}
```

**Загрузка**:
```java
public class DataDrivenItems {
    public static void registerFromJson() {
        JsonObject json = loadJson("data/examplemod/items/ores.json");
        for (JsonElement item : json.getAsJsonArray("items")) {
            String id = item.getAsJsonObject().get("id").getAsString();
            int stackSize = item.getAsJsonObject().get("stack_size").getAsInt();
            ModRegistries.ITEMS.register(id, () -> new Item(
                new Item.Properties().stacksTo(stackSize).tab(ModCreativeTabs.MATERIALS)
            ));
        }
    }
}
```

**Выигрыш**:
- `ModItems.java` остаётся компактным
- Легко добавлять/удалять предметы без перекомпиляции
- Можно генерировать JSON автоматически (например, из таблиц)

---

### 3. 💾 **Кэширование тяжёлых вычислений**

**Проблема**: `ColdMechanic` и `HypothermiaMechanic` каждую секунду вызывают `getAmbientTemperature()`, который делает:
- `world.getBiome()` - дорогой вызов
- `biome.getBiomeCategory()` - дополнительная логика
- Switch-case на 15 веток

**Решение**: Кэшировать температуру биома на 10-30 секунд.

```java
public class ColdMechanic implements IMechanicModule {
    private static final Map<UUID, CachedTemp> TEMP_CACHE = new HashMap<>();
    
    private static class CachedTemp {
        int temperature;
        long expiry; // world.getGameTime() + 600 (30 секунд)
    }
    
    @Override
    public void onPlayerTick(ServerPlayerEntity player) {
        UUID id = player.getUUID();
        long now = player.level.getGameTime();
        
        // Используем кэш, если не истёк
        CachedTemp cached = TEMP_CACHE.get(id);
        int temp;
        if (cached != null && now < cached.expiry) {
            temp = cached.temperature;
        } else {
            temp = getAmbientTemperature(player);
            cached = new CachedTemp();
            cached.temperature = temp;
            cached.expiry = now + 600; // 30 секунд
            TEMP_CACHE.put(id, cached);
        }
        
        // ... логика холода с temp
    }
    
    @Override
    public void onPlayerLogout(ServerPlayerEntity player) {
        TEMP_CACHE.remove(player.getUUID());
    }
}
```

**Выигрыш**:
- Меньше вызовов `world.getBiome()` (один из самых дорогих методов)
- Можно настроить TTL кэша через конфиг

---

### 4. 🎛️ **Отложенная синхронизация (debouncing)**

**Проблема**: Механики отправляют пакеты каждый раз при изменении стата, даже если он меняется несколько раз за тик.

**Решение**: Собирать "грязные" флаги и отправлять один пакет в конце тика.

```java
public class StatsSyncManager {
    private static final Map<UUID, DirtyFlags> DIRTY = new HashMap<>();
    
    static class DirtyFlags {
        boolean anyDirty;
    }
    
    public static void markDirty(UUID playerId) {
        DIRTY.computeIfAbsent(playerId, k -> new DirtyFlags()).anyDirty = true;
    }
    
    // Вызывается в конце WorldTickEvent
    public static void flushAll(ServerWorld world) {
        for (ServerPlayerEntity player : world.players()) {
            DirtyFlags flags = DIRTY.get(player.getUUID());
            if (flags != null && flags.anyDirty) {
                player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
                    ModNetworkHandler.CHANNEL.send(
                        PacketDistributor.PLAYER.with(() -> player),
                        new SyncAllStatsPacket(stats)
                    );
                });
                flags.anyDirty = false;
            }
        }
    }
}
```

**В механиках**:
```java
stats.setThirst(newThirst);
StatsSyncManager.markDirty(player.getUUID()); // Вместо немедленной отправки пакета
```

**Выигрыш**:
- Если 5 механик изменили статы за один тик → отправляется 1 пакет вместо 5
- Меньше нагрузка на сеть

---

### 5. 🏷️ **Индексирование предметов по категориям**

**Проблема**: С 1000+ предметами поиск "всех инструментов" или "всех еды" становится O(n).

**Решение**: Создать индексы при регистрации.

```java
public class ItemRegistry {
    private static final Map<String, Set<Item>> CATEGORIES = new HashMap<>();
    
    public static void registerWithCategory(String id, Item item, String category) {
        ModRegistries.ITEMS.register(id, () -> item);
        CATEGORIES.computeIfAbsent(category, k -> new HashSet<>()).add(item);
    }
    
    public static Set<Item> getCategory(String category) {
        return CATEGORIES.getOrDefault(category, Collections.emptySet());
    }
}

// Использование:
ItemRegistry.registerWithCategory("stone_hammer", new HammerItem(...), "tools");
ItemRegistry.registerWithCategory("bone_hammer", new HammerItem(...), "tools");

// Быстрый поиск:
for (Item tool : ItemRegistry.getCategory("tools")) {
    // O(1) вместо O(1000)
}
```

**Выигрыш**:
- Быстрый поиск предметов по категории
- Можно использовать для проверок типа "это инструмент?"

---

### 6. ⚡ **Оптимизация NBT для сохранений**

**Проблема**: Все статы сохраняются как `int` (4 байта), но большинство - 0-100 (1 байт достаточно).

**Решение**: Использовать `byte` для статов 0-100.

```java
// PlayerStatsStorage.java
@Override
public CompoundNBT writeNBT(Capability<IPlayerStats> cap, IPlayerStats stats, Direction side) {
    CompoundNBT nbt = new CompoundNBT();
    nbt.putByte("thirst", (byte) stats.getThirst());      // 4 байта → 1 байт
    nbt.putByte("fatigue", (byte) stats.getFatigue());
    nbt.putByte("cold", (byte) stats.getCold());
    nbt.putByte("hypothermia", (byte) stats.getHypothermia());
    nbt.putByte("virus", (byte) stats.getVirus());
    nbt.putByte("poison", (byte) stats.getPoison());
    nbt.putByte("disease", (byte) stats.getDisease());
    nbt.putShort("blood", (short) stats.getBlood());      // Если > 255
    return nbt;
}

@Override
public void readNBT(Capability<IPlayerStats> cap, IPlayerStats stats, Direction side, INBT nbt) {
    if (nbt instanceof CompoundNBT) {
        CompoundNBT tag = (CompoundNBT) nbt;
        stats.setThirst(tag.getByte("thirst") & 0xFF);    // Unsigned byte
        stats.setFatigue(tag.getByte("fatigue") & 0xFF);
        // ...
    }
}
```

**Выигрыш**:
- 8 int статов: 32 байта → 8 байт = **75% экономия памяти**
- Быстрее запись/чтение NBT
- Меньше размер сохранений

---

### 7. 🔧 **Ленивая инициализация для редких механик**

**Проблема**: Некоторые механики нужны только в специфических ситуациях (например, для end-game контента).

**Решение**: Регистрировать механики условно через конфиг.

```java
// ModMechanics.java
public static void init() {
    if (initialized) return;
    initialized = true;

    // Базовые механики - всегда
    register(new ThirstMechanic());
    register(new RestMechanic());
    
    // End-game механики - только если включены в конфиге
    if (Config.ENABLE_RADIATION.get()) {
        register(new RadiationMechanic());
    }
    
    if (Config.ENABLE_MAGIC_SYSTEM.get()) {
        register(new ManaMechanic());
        register(new SpellCastingMechanic());
    }
}
```

**Выигрыш**:
- Игроки могут отключить тяжёлые механики
- Меньше нагрузка на старте игры
- Модульность: можно собирать "лёгкую" версию мода

---

## 📊 Приоритеты внедрения

| Приоритет | Оптимизация | Сложность | Выигрыш |
|-----------|-------------|-----------|---------|
| 🔴 **ВЫСОКИЙ** | Батчинг пакетов | Низкая | 80% меньше трафика |
| 🔴 **ВЫСОКИЙ** | Кэширование биома/температуры | Низкая | 50% меньше вызовов world.getBiome() |
| 🟡 **СРЕДНИЙ** | Data-driven предметы | Средняя | Масштабируемость |
| 🟡 **СРЕДНИЙ** | Отложенная синхронизация | Средняя | 50% меньше пакетов |
| 🟢 **НИЗКИЙ** | Оптимизация NBT | Низкая | 75% меньше сохранения |
| 🟢 **НИЗКИЙ** | Индексирование предметов | Низкая | Удобство разработки |
| 🟢 **НИЗКИЙ** | Ленивая инициализация | Низкая | Гибкость конфига |

---

## 🎯 Рекомендуемый порядок внедрения

1. **Сначала**: Батчинг сетевых пакетов (максимальный эффект, минимум усилий)
2. **Затем**: Кэширование биома/температуры (заметный прирост производительности)
3. **Потом**: Data-driven регистрация (когда начнёшь добавлять сотни предметов)
4. **Опционально**: Остальные оптимизации по мере необходимости

---

## 📈 Ожидаемые результаты

С этими оптимизациями твой мод сможет комфортно поддерживать:
- ✅ **1000+ предметов** без захламления кода
- ✅ **100+ механик** без просадок FPS
- ✅ **20+ игроков** на сервере без лагов
- ✅ **Сохранения < 1 MB** на игрока

---

## 🛠️ Дополнительные инструменты

### Профилирование производительности
```bash
# Spark - лучший профайлер для Minecraft
# https://spark.lucko.me/
/spark profiler start
# играй 1-2 минуты
/spark profiler stop
# смотри отчёт, какие методы тормозят
```

### Мониторинг памяти
```java
// В MechanicScheduler добавить логирование
if (Config.MECHANICS_PROFILING.get() && serverTickCounter % 6000 == 0) { // Каждые 5 минут
    Runtime runtime = Runtime.getRuntime();
    long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
    LOGGER.info("Memory usage: {} MB", usedMemory);
}
```

---

**Главный принцип**: Оптимизируй то, что реально тормозит (используй профайлер), а не гадай! 🎯

