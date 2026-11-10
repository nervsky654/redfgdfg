package com.example.cncd.mixin;

import com.example.cncd.Config;
import com.example.cncd.NightDarkness;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightTexture.class)
public class LightTextureMixin {

    @Shadow @Final private NativeImage lightPixels;
    @Shadow @Final private DynamicTexture lightTexture;
    @Shadow @Final private Minecraft minecraft;
    @Shadow private boolean updateLightTexture;

    /** Ванильный метод пересобирает текстуру только если флаг стоит; запоминаем это на входе. */
    @Unique
    private boolean cncd$willRebuild;

    @Inject(method = "updateLightTexture", at = @At("HEAD"))
    private void cncd$begin(float partialTicks, CallbackInfo ci) {
        this.cncd$willRebuild = this.updateLightTexture;
    }

    /**
     * Ванильная текстура освещения уже готова (со всеми полами яркости и ползунком гаммы).
     * Ослабляем в ней небесную составляющую ночью. Блочный свет (факелы, динамический свет) не трогаем.
     */
    @Inject(method = "updateLightTexture", at = @At("TAIL"))
    private void cncd$darkenNight(float partialTicks, CallbackInfo ci) {
        if (!this.cncd$willRebuild) return;
        this.cncd$willRebuild = false;

        ClientLevel level = this.minecraft.level;
        if (level == null || !level.dimensionType().hasSkyLight()) return;

        float skyRemoval;
        float floorDim;
        try {
            if (!Config.DARK_NIGHTS.get()) return;
            skyRemoval = Config.NIGHT_DARKNESS.get().floatValue();
            floorDim = Config.NIGHT_AMBIENT_DARKNESS.get().floatValue();
        } catch (RuntimeException e) {
            return; // конфиг ещё не загружен
        }

        float night = NightDarkness.nightFactor(level.getTimeOfDay(1.0F));
        if (night <= 0.001F) return; // день: ничего не делаем, нулевые накладные расходы

        int[] px = new int[256];
        for (int sky = 0; sky < 16; sky++) {
            for (int block = 0; block < 16; block++) {
                px[sky * 16 + block] = this.lightPixels.getPixelRGBA(block, sky);
            }
        }

        NightDarkness.apply(px, night, skyRemoval, floorDim);

        for (int sky = 0; sky < 16; sky++) {
            for (int block = 0; block < 16; block++) {
                this.lightPixels.setPixelRGBA(block, sky, px[sky * 16 + block]);
            }
        }
        this.lightTexture.upload();
    }
}
