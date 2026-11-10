Create: No Cart Assembler & Drill + Dark Nights (NeoForge 1.21.1)

Сборка:
1. Нужны JDK 21 и Gradle 8.10+ (или скопируй gradlew, gradlew.bat и папку gradle/ из официального NeoForge MDK 1.21.1 в эту папку).
2. В папке проекта: gradle build   (или ./gradlew build)
3. Готовый мод: build/libs/create-no-cart-assembler-drill-1.2.0.jar

Настройки ночи: config/create_no_cart_drill-client.toml
  darkNights           = true/false
  nightDarkness        = 0.0 .. 1.0 (по умолчанию 0.9)  доля убранного лунного света
  nightAmbientDarkness = 0.0 .. 1.0 (по умолчанию 0.5)  доп. затемнение, темнее ванильной пещеры

ВАЖНО: если в config уже есть nightDarkness = 0.85 от версии 1.1.0 - он останется.
Для самой тёмной ночи удали файл конфига, он создастся заново с новыми значениями.

Этот jar заменяет прошлые версии (1.0.0 и 1.1.0), старые удали.
Шейдеры (Iris/Oculus) используют своё освещение - с ними мод ночь не затемнит.
