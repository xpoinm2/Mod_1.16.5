# Модули механик

Все механики мода теперь живут здесь, в виде отдельных классов, реализующих `IMechanicModule`.

## Как добавить новую механику

1. **Создай новый класс** в этой папке, реализующий `IMechanicModule`:

```java
package com.example.examplemod.server.mechanics.modules;

import com.example.examplemod.server.mechanics.IMechanicModule;
import net.minecraft.entity.player.ServerPlayerEntity;

public class MyNewMechanic implements IMechanicModule {
    @Override
    public String id() {
        return "my_new_mechanic"; // Уникальный ID для логов
    }

    @Override
    public int playerIntervalTicks() {
        return 20; // Вызывать каждые 20 тиков (1 секунда)
    }

    @Override
    public void onPlayerTick(ServerPlayerEntity player) {
        // Твоя логика здесь
    }
}
```

2. **Зарегистрируй модуль** в `ModMechanics.init()`:

```java
register(new MyNewMechanic());
```

3. **Готово!** Твоя механика будет автоматически вызываться с нужной частотой.

## Доступные хуки

### Тиковые события
- `playerIntervalTicks()` + `onPlayerTick(ServerPlayerEntity)` — вызов для каждого игрока с интервалом
- `serverIntervalTicks()` + `onServerTick(MinecraftServer)` — глобальный серверный тик с интервалом

### Жизненный цикл игрока
- `onPlayerLogin(ServerPlayerEntity)` — игрок зашёл на сервер
- `onPlayerLogout(ServerPlayerEntity)` — игрок вышел (чисти Map<UUID, ...> здесь!)

### Forge события (опционально)
- `enableBlockBreak()` + `onBlockBreak(BlockEvent.BreakEvent)`
- `enablePlayerInteract()` + `onPlayerInteract(PlayerInteractEvent)`
- `enableAttackEntity()` + `onAttackEntity(AttackEntityEvent)`
- `enableLivingJump()` + `onLivingJump(LivingJumpEvent)`
- `enableUseItemFinish()` + `onUseItemFinish(LivingEntityUseItemEvent.Finish)`
- `enableWorldTick()` + `onWorldTick(TickEvent.WorldTickEvent)`
- `enableRegisterCommands()` + `onRegisterCommands(RegisterCommandsEvent)`
- И другие (см. `IMechanicModule`)

## Профилирование

В `config/examplemod-common.toml`:
```toml
[mechanics]
    profiling = true
    slow_call_threshold_ms = 5
    profile_log_every_ticks = 1200
```

Включи `profiling = true`, и в логах будет видно, какие механики тормозят.

## Примеры

- `ThirstMechanic.java` — сложная механика с множеством событий (тик, прыжки, атаки, питьё)
- `ColdMechanic.java` — простая механика с одним тиком раз в секунду
- `StatsCommandsMechanic.java` — регистрация команд
- `BlockBreakMechanic.java` — обработка события ломания блоков

## Преимущества этой архитектуры

✅ **Масштабируемость**: легко добавлять 100+ механик без захламления `ExampleMod.java`  
✅ **Производительность**: централизованное throttling, профилирование, батчинг  
✅ **Читаемость**: каждая механика в своём файле, всё логично структурировано  
✅ **Отладка**: профилирование показывает, что тормозит  

