package org.mangorage.swiss.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ClientConfig {

    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.ConfigValue<Integer> VISIBLE_ROWS;

    static {
        BUILDER.push("SWISS Client Config");

        VISIBLE_ROWS = BUILDER
                .comment("Number of rows visible in the GUI (default: 3)")
                .defineInRange("visibleRows", 3, 3, 12); // for example, between 3 and 12 rows

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}