package org.powernukkitx.magnificentminimap;

final class GlyphPalette {
    static final char TRANSPARENT = '\uE900';
    static final char PLAYER = '\uF90F';

    private GlyphPalette() {}

    static char rgb(int red, int green, int blue) {
        int r = clampNibble(red);
        int g = clampNibble(green);
        int b = clampNibble(blue);
        return (char) (((0xEA + b) << 8) | (r << 4) | g);
    }

    static int toNibble(int color) {
        return Math.max(0, Math.min(15, (color * 15) / 255));
    }

    private static int clampNibble(int value) {
        return Math.max(0, Math.min(15, value));
    }
}
