# Рефакторинг системы механик

## Что изменилось

### Новая архитектура: Модульные механики

Вся логика механик теперь живёт в отдельных модулях в `server/mechanics/modules/`, реализующих интерфейс `IMechanicModule`. Это решает проблему масштабируемости для большого мода с 100+ механиками.

### Удалённые файлы (логика перенесена в модули)

- `server/StatsCommands.java` → `modules/StatsCommandsMechanic.java`
- `server/PyramidDebugCommands.java` → `modules/PyramidDebugCommandsMechanic.java`
- `server/BiomeTeleportCommands.java` → `modules/BiomeTeleportCommandsMechanic.java`
- `server/BlockTeleportCommand.java` → `modules/BlockTeleportCommandsMechanic.java`
- `server/QuestCommands.java` → `modules/QuestCommandsMechanic.java`
- `server/ColdHandler.java` → `modules/ColdMechanic.java`
- `server/HypothermiaHandler.java` → `modules/HypothermiaMechanic.java`
- `server/BlockBreakHandler.java` → `modules/BlockBreakMechanic.java`
- `server/DayNightCycleHandler.java` → `modules/DayNightCycleMechanic.java`
- `server/HotOreDamageHandler.java` → `modules/HotOreDamageMechanic.java`
- `server/VirusHandler.java` → `modules/VirusMechanic.java`

### Изменённые файлы (теперь только фасады для пакетов)

- `server/ThirstHandler.java` — оставлены только методы `onDrinkButton()`, `onMixWater()`, `tick()`, `logout()` для вызова из пакетов
- `server/RestHandler.java` — оставлены только методы `startSitting()`, `tick()` для вызова из пакетов

### Новые файлы

- `server/mechanics/modules/` — папка со всеми модулями механик
- `server/mechanics/modules/README.md` — документация по добавлению новых механик
- `server/mechanics/modules/ThirstMechanic.java` — модуль механики жажды
- `server/mechanics/modules/RestMechanic.java` — модуль механики отдыха
- `server/mechanics/modules/ColdMechanic.java` — модуль механики холода
- `server/mechanics/modules/HypothermiaMechanic.java` — модуль механики гипотермии
- `server/mechanics/modules/BlockBreakMechanic.java` — модуль обработки ломания блоков
- `server/mechanics/modules/DayNightCycleMechanic.java` — модуль изменения цикла дня/ночи
- `server/mechanics/modules/HotOreDamageMechanic.java` — модуль урона от горячих руд
- `server/mechanics/modules/VirusMechanic.java` — модуль механики вирусов
- `server/mechanics/modules/StatsCommandsMechanic.java` — модуль команд статистики
- `server/mechanics/modules/PyramidDebugCommandsMechanic.java` — модуль команд отладки пирамид
- `server/mechanics/modules/BiomeTeleportCommandsMechanic.java` — модуль команд телепортации по биомам
- `server/mechanics/modules/BlockTeleportCommandsMechanic.java` — модуль команд телепортации к блокам
- `server/mechanics/modules/QuestCommandsMechanic.java` — модуль команд квестов

## Преимущества новой архитектуры

### 1. Масштабируемость
- Легко добавлять новые механики без изменения `ExampleMod.java`
- Каждая механика в своём файле — чистая структура
- Нет захламления кода сотнями `@SubscribeEvent` классов

### 2. Производительность
- Централизованное throttling (механики вызываются с заданным интервалом)
- Встроенное профилирование (можно включить в конфиге)
- Логирование медленных вызовов (> 5ms по умолчанию)
- Батчинг событий (один обработчик для всех модулей)

### 3. Читаемость и поддержка
- Вся логика механики в одном файле
- Понятная структура: `server/mechanics/modules/ИмяМеханики.java`
- Документация в `modules/README.md`

### 4. Отладка
- Профилирование показывает, какая механика тормозит
- Логи с ID механики для быстрого поиска проблем
- Можно отключить отдельные механики, закомментировав `register()` в `ModMechanics.init()`

## Как добавить новую механику

1. Создай файл `server/mechanics/modules/МояМеханика.java`:

```java
package com.example.examplemod.server.mechanics.modules;

import com.example.examplemod.server.mechanics.IMechanicModule;
import net.minecraft.entity.player.ServerPlayerEntity;

public class МояМеханика implements IMechanicModule {
    @Override
    public String id() {
        return "моя_механика";
    }

    @Override
    public int playerIntervalTicks() {
        return 20; // Вызывать каждую секунду
    }

    @Override
    public void onPlayerTick(ServerPlayerEntity player) {
        // Твоя логика здесь
    }
}
```

2. Зарегистрируй в `ModMechanics.init()`:

```java
register(new МояМеханика());
```

3. Готово!

## Профилирование

В `config/examplemod-common.toml`:

```toml
[mechanics]
    # Включить профилирование (влияет на производительность)
    profiling = true
    # Порог для логирования медленных вызовов (в мс)
    slow_call_threshold_ms = 5
    # Как часто выводить сводку профилирования в лог (в тиках)
    profile_log_every_ticks = 1200  # 1 минута
```

Включи `profiling = true`, и в логах будет видно:
```
[INFO] --- Mechanic Profiling Summary ---
[INFO]   thirst: Total 1234 ms, Avg 12 ms (100 calls)
[INFO]   cold: Total 234 ms, Avg 2 ms (100 calls)
[INFO] ----------------------------------
```

## Миграция существующих механик

Все основные механики уже перенесены:
- ✅ Жажда и усталость (`ThirstMechanic`)
- ✅ Отдых (`RestMechanic`)
- ✅ Холод (`ColdMechanic`)
- ✅ Гипотермия (`HypothermiaMechanic`)
- ✅ Ломание блоков (`BlockBreakMechanic`)
- ✅ Цикл дня/ночи (`DayNightCycleMechanic`)
- ✅ Урон от горячих руд (`HotOreDamageMechanic`)
- ✅ Вирусы (`VirusMechanic`)
- ✅ Все команды (Stats, Pyramid, Biome/Block Teleport, Quest)

Остальные механики (GravelOreWash, IronClusterWash, CraftingBlocker, FirepitStructure, FlaxSoak, FlaxDrying, SharpBone, RedMushroom, BigBoneDrop, HewnStoneSpawn, NaturalRegenDisable, AutoSave) пока оставлены как `HandlerModule` адаптеры в `ModMechanics.init()`. Их можно перенести по тому же принципу при необходимости.

## Обратная совместимость

- Старые `ThirstHandler` и `RestHandler` остались как фасады для вызовов из пакетов (`DrinkWaterPacket`, `MixWaterPacket`, `ActivityPacket`)
- Все сохранения/загрузки данных работают как раньше (через `PlayerStats` capability)
- Никаких breaking changes для игроков

## Тестирование

Проект успешно компилируется:
```bash
.\gradlew.bat -q compileJava
# BUILD SUCCESSFUL
```

Рекомендуется протестировать в игре:
1. Проверить работу жажды/усталости (прыжки, бег, атаки)
2. Проверить работу холода/гипотермии (холодные биомы)
3. Проверить ломание блоков (руды, листья, песок)
4. Проверить команды `/stats`, `/pyramid`, `/biometp`, `/blocktp`, `/quest`
5. Проверить урон от горячих руд
6. Проверить заражение вирусом (еда, ломание блоков)

## Следующие шаги

1. Протестировать все механики в игре
2. При необходимости перенести оставшиеся `HandlerModule` адаптеры в отдельные модули
3. Добавить новые механики по мере разработки мода (просто создавай новые файлы в `modules/`)
4. Использовать профилирование для оптимизации медленных механик

---

**Итог**: Теперь у тебя есть масштабируемая, производительная и удобная архитектура для добавления сотен механик без проблем с производительностью и читаемостью кода! 🚀

