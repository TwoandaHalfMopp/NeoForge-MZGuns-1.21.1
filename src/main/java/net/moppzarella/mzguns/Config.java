package net.moppzarella.mzguns;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
public class Config {
    private static final ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();
    private static final ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();

    public enum GunGUILocations {
        HOTBAR_SIDE, HOTBAR_ABOVE, CROSSHAIR
    }

    public static final ModConfigSpec.BooleanValue INFINITE_CLIP_IN_CREATIVE = COMMON_BUILDER
            .comment("Ammo isn't spent while in Creative Mode")
            .define("infiniteClipInCreative", false);


    public static final ModConfigSpec.EnumValue<GunGUILocations> GUN_GUI_LOCATIONS = CLIENT_BUILDER
            .comment("Where the Gun's Info (Clip Size) will show")
            .defineEnum("gunGuiLocations", GunGUILocations.HOTBAR_SIDE);

    static final ModConfigSpec CLIENT_SPEC = CLIENT_BUILDER.build();

    static final ModConfigSpec COMMON_SPEC = COMMON_BUILDER.build();

    private static boolean validateItemName(final Object obj) {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }
}
