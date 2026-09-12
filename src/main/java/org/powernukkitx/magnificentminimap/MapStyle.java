package org.powernukkitx.magnificentminimap;

enum MapStyle {
    SIMPLE,
    VANILLA,
    MODERN,
    HEIGHT,
    MAGNIFICENT;

    static MapStyle parse(String value) {
        if (value == null) return VANILLA;
        try {
            return valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ignored) {
            return VANILLA;
        }
    }
}
