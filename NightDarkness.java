package com.example.cncd;

/**
 * Чистая математика затемнения ночи (без зависимостей от Minecraft, чтобы её можно было тестировать отдельно).
 *
 * Работает над ГОТОВОЙ текстурой освещения (lightmap 16x16), которую ваниль собирает в
 * LightTexture.updateLightTexture. Индекс пикселя: sky * 16 + block, формат ABGR (как у NativeImage).
 *
 * Почему именно так, а не подменой skyDarken: ваниль после skyDarken ещё добавляет пол яркости
 * (min 0.05, два подмешивания 0.75 по 4%) и применяет ползунок яркости (гамма-кривая 1-(1-x)^4),
 * которая сильно поднимает тёмные значения. Поэтому любое затемнение «на входе» почти полностью
 * съедается. Здесь затемнение делается «на выходе», когда все эти шаги уже выполнены.
 */
public final class NightDarkness {

    /**
     * 0.0 = день, 1.0 = полная ночь. Та же кривая, что у ванильного skyDarken, но без погоды:
     * дождь и гроза днём не должны превращаться в ночь.
     *
     * @param timeOfDay Level#getTimeOfDay(partialTick), 0..1 (0 = полдень, 0.5 = полночь)
     */
    public static float nightFactor(float timeOfDay) {
        float n = 0.8F - 2.0F * (float) Math.cos(timeOfDay * (Math.PI * 2.0));
        return clamp01(n);
    }

    /**
     * @param px          lightmap: px[sky * 16 + block], ABGR; изменяется на месте
     * @param night       0..1, см. {@link #nightFactor(float)}
     * @param skyRemoval  0..1 - какую долю небесного («лунного») света убрать ночью
     * @param floorDim    0..1 - насколько ещё затемнить базовый «пол» яркости под открытым небом
     *                    (это то, что позволяет сделать ночь темнее, чем ванильная пещера без факела)
     */
    public static void apply(int[] px, float night, float skyRemoval, float floorDim) {
        if (px == null || px.length != 256) return;
        night = clamp01(night);
        skyRemoval = clamp01(skyRemoval);
        floorDim = clamp01(floorDim);
        if (night <= 0.0F || (skyRemoval <= 0.0F && floorDim <= 0.0F)) return;

        final float keepSky = 1.0F - skyRemoval * night;

        // Эталоны: sky = 0 (нет неба). Копируем до изменений.
        final int base = px[0];
        final int[] row0 = new int[16];
        System.arraycopy(px, 0, row0, 0, 16);

        for (int sky = 1; sky < 16; sky++) {
            // чем больше открытого неба над пикселем, тем сильнее затемнение «пола»
            final float keepFloor = 1.0F - floorDim * night * (sky / 15.0F);
            for (int block = 0; block < 16; block++) {
                final int i = sky * 16 + block;
                final int cur = px[i];
                final int c0 = row0[block];
                int out = 0xFF000000;
                for (int shift = 0; shift <= 16; shift += 8) {
                    float b = (base >> shift) & 0xFF;      // пол яркости (нет ни неба, ни факелов)
                    float s0 = (c0 >> shift) & 0xFF;       // тот же блочный свет, но без неба
                    float c = (cur >> shift) & 0xFF;       // как в ванили
                    // блочный свет (s0 - b) не трогаем; небесная добавка (c - s0) ослабляем; пол b затемняем
                    float v = (s0 - b) + b * keepFloor + (c - s0) * keepSky;
                    int iv = Math.round(v);
                    if (iv < 0) iv = 0; else if (iv > 255) iv = 255;
                    out |= iv << shift;
                }
                px[i] = out;
            }
        }
    }

    private static float clamp01(float v) {
        return v < 0.0F ? 0.0F : (v > 1.0F ? 1.0F : v);
    }

    private NightDarkness() {}
}
