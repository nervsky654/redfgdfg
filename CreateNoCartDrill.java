package com.example.cncd;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(CreateNoCartDrill.MODID)
public class CreateNoCartDrill {
    public static final String MODID = "create_no_cart_drill";

    public CreateNoCartDrill(ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, Config.SPEC);
    }
}
