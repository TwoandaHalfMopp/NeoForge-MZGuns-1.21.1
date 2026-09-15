package net.moppzarella.mzguns;

import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
public class Config {
    private static final ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();
    private static final ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue INFINITE_CLIP_IN_CREATIVE = COMMON_BUILDER
            .comment("Ammo isn't spent while in Creative Mode")
            .define("infiniteClipInCreative", false);

    public static final ModConfigSpec.IntValue HITSCAN_PELLET_RANGE = COMMON_BUILDER
            .comment("Max range that hitscan pellets will travel (in blocks)")
            .defineInRange("hitscanPelletRange", 100, 0, Integer.MAX_VALUE);
    public static final ModConfigSpec.DoubleValue HEADSHOT_MULTIPLIER = COMMON_BUILDER
            .comment("Max range that hitscan pellets will travel (in blocks)")
            .defineInRange("hitscanPelletRange", 3D, 1D, Float.MAX_VALUE);

    public static final ModConfigSpec.BooleanValue SHOW_DEBUG_HUD_ELEMENTS = CLIENT_BUILDER
            .comment("Show additional HUD elements for debugging purposes")
            .define("showDebugHudElements", false);

   static final ModConfigSpec CLIENT_SPEC = CLIENT_BUILDER.build();

    static final ModConfigSpec COMMON_SPEC = COMMON_BUILDER.build();
}
