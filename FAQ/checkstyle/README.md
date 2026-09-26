# Checkstyle и Sun Code Conventions в Gradle

Checkstyle проверяет исходный Java-код по набору правил. В этой инструкции используется базовая конфигурация Sun Code Conventions, которая уже поставляется внутри JAR-файла Checkstyle. Не нужно вручную скачивать или копировать `sun_checks.xml`: версия конфигурации всегда совпадает с версией линтера.

Инструкция рассчитана на Gradle Kotlin DSL (`build.gradle.kts`).

## Подключение к одному модулю

Добавьте `checkstyle` в блок `plugins` нужного Java-модуля и настройте его после блока `java`:

```kotlin
plugins {
    checkstyle
}

val sunChecks = configurations.detachedConfiguration(
    dependencies.create("com.puppycrawl.tools:checkstyle:14.1.0")
).apply {
    isTransitive = false
}

checkstyle {
    toolVersion = "14.1.0"
    config = resources.text.fromArchiveEntry(
        sunChecks,
        "sun_checks.xml"
    )
    isShowViolations = true
    isIgnoreFailures = false
    maxWarnings = 0
}

tasks.withType<Checkstyle>().configureEach {
    reports {
        html.required = true
        xml.required = false
    }
}
```

Плагин Gradle сам добавляет Checkstyle в конфигурацию `checkstyle`. Отдельная нетранзитивная конфигурация `sunChecks` содержит ровно один JAR — это важно, так как `fromArchiveEntry` не умеет выбирать файл из classpath с несколькими JAR. `maxWarnings = 0` обязателен для строгого режима: Sun-конфигурация сообщает нарушения с уровнем `warning`, а не `error`.

При обновлении версии Checkstyle обновите версию и в `toolVersion`, и в зависимости `sunChecks`; так встроенный Sun-конфиг и сам линтер останутся одной версии. Перед обновлением стоит запустить проверку в CI: новые правила могут выявить нарушения в существующем коде.

## Запуск

Для одного модуля:

```bash
./gradlew :modules:practise-3:checkstyleMain
./gradlew :modules:practise-3:checkstyleTest
./gradlew :modules:practise-3:check
```

`checkstyleMain` проверяет production-код, `checkstyleTest` — тесты, а `check` запускает оба задания вместе с обычными проверками проекта. HTML-отчёт находится в каталоге модуля, например:

```text
modules/practise-3/build/reports/checkstyle/main.html
```

## Подключение ко всем Java-модулям

Если правила должны применяться ко всем модулям, поместите конфигурацию в корневой `build.gradle.kts`:

```kotlin
import org.gradle.api.plugins.quality.CheckstyleExtension

subprojects {
    pluginManager.withPlugin("java") {
        apply(plugin = "checkstyle")
        val sunChecks = configurations.detachedConfiguration(
            dependencies.create("com.puppycrawl.tools:checkstyle:14.1.0")
        ).apply {
            isTransitive = false
        }

        extensions.configure<CheckstyleExtension> {
            config = resources.text.fromArchiveEntry(
                sunChecks,
                "sun_checks.xml"
            )
            isShowViolations = true
            isIgnoreFailures = false
            maxWarnings = 0
        }
    }
}
```

В этом варианте `checkstyle` добавлять в `plugins` каждого дочернего модуля не нужно: он применяется после обнаружения Java-плагина.

## Внедрение в существующий проект

Sun Code Conventions достаточно строгий, поэтому для уже написанного проекта удобно внедрять его в два этапа:

1. Запустите `checkstyleMain` и исправьте нарушения в production-коде.
2. Добавьте `./gradlew check` в CI и оставьте `isIgnoreFailures = false` и `maxWarnings = 0`, чтобы новые нарушения останавливали сборку.

Не рекомендуется надолго включать `isIgnoreFailures = true`: отчёт будет создан, но сборка станет успешной даже при нарушениях. Для редкого обоснованного исключения используйте точечное подавление, которое поддерживает Sun-конфигурация:

```java
// CHECKSTYLE.SUPPRESS: LineLength
String longMessage = "...";
```

Перед подавлением проверьте название правила в отчёте и оставьте короткое объяснение, почему правило нельзя соблюсти в этом месте.

## Частые проблемы

### Конфигурация не найдена

Проверьте, что `config` задан через `fromArchiveEntry(sunChecks, "sun_checks.xml")`, а плагин `checkstyle` подключён. Не используйте путь к файлу `sun_checks.xml` в проекте: такого файла в репозитории нет.

### Линтер не понимает синтаксис используемой Java

Обновите `toolVersion` до версии Checkstyle с поддержкой вашего синтаксиса. Checkstyle запускается на JVM Gradle; при необходимости можно назначить ему отдельный Java toolchain:

```kotlin
tasks.withType<Checkstyle>().configureEach {
    javaLauncher = javaToolchains.launcherFor {
        languageVersion = JavaLanguageVersion.of(25)
    }
}
```

Этот toolchain нужен для запуска самого Checkstyle и не меняет версию Java, которой компилируется приложение.

## Источники

Подробнее о заданиях, отчётах и настройке Gradle-плагина: [документация Gradle](https://docs.gradle.org/current/userguide/checkstyle_plugin.html). Checkstyle поставляет `sun_checks.xml` вместе с линтером: [документация Checkstyle](https://checkstyle.org/config.html).
