package com.example.cncd;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue DARK_NIGHTS = BUILDER
            .comment("Включить более тёмные ночи (только на клиенте)")
            .define("darkNights", true);

    public static final ModConfigSpec.DoubleValue NIGHT_DARKNESS = BUILDER
            .comment("Какую долю небесного (лунного) света убрать ночью.",
                    "0.0 = как в ванили, 1.0 = под открытым небом ночью так же темно, как в ванильной пещере.")
            .defineInRange("nightDarkness", 0.9, 0.0, 1.0);

    public static final ModConfigSpec.DoubleValue NIGHT_AMBIENT_DARKNESS = BUILDER
            .comment("Дополнительное затемнение «базовой» яркости под открытым небом ночью (темнее ванильной пещеры).",
                    "0.0 = выключено, 1.0 = почти кромешная тьма. Факелы и другой блочный свет не затрагиваются.")
            .defineInRange("nightAmbientDarkness", 0.5, 0.0, 1.0);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private Config() {}
}
