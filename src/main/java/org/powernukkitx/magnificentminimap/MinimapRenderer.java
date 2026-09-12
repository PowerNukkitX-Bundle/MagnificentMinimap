package org.powernukkitx.magnificentminimap;

import org.powernukkitx.Player;
import org.powernukkitx.level.Level;
import org.powernukkitx.utils.BlockColor;

import java.util.HashMap;
import java.util.Map;

final class MinimapRenderer {
    private static final String ROTATION_NAME = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ[1";
    private static final char[] COMPASS = {'\uE901', '\uE902', '\uE903', '\uE904'};

    private final MagnificentMinimap plugin;
    private final Map<CacheKey, MapSample> cache = new HashMap<>();

    MinimapRenderer(MagnificentMinimap plugin) {
        this.plugin = plugin;
    }

    void hide(Player player) {
        // Send a syntactically valid disabled frame.
        // Header: x d a 8 1 => disabled, square, not sneaking, scale 8, valid direction 1.
        // The HUD stores it, #is_loaded becomes false, and no invalid scale/texture value is parsed.
        player.sendTitle("xda81mm_render_0", "", 0, 0, 0);
    }

    void render(Player player) {
        if (!plugin.isEnabledFor(player)) return;

        int detail = Math.max(0, Math.min(3, plugin.getConfig().getInt("render-detail", 0)));
        int radius = switch (detail) {
            case 1 -> 24;
            case 2 -> 36;
            case 3 -> 48;
            default -> 18;
        };
        double detailMultiplier = switch (detail) {
            case 1 -> 3.0;
            case 2 -> 2.0;
            case 3 -> 1.5;
            default -> 4.0;
        };
        double zoom = Math.max(0.05, plugin.getConfig().getDouble("zoom", 7.0) / 20.0 * detailMultiplier);
        boolean rotate = plugin.getConfig().getBoolean("rotate-minimap", false);
        boolean round = plugin.getConfig().getBoolean("round-minimap", false);
        boolean enlarge = plugin.getConfig().getBoolean("enlarge-on-sneak", true) && player.isSneaking();
        boolean renderPlayers = plugin.getConfig().getBoolean("render-players", true);
        MapStyle style = MapStyle.parse(plugin.getConfig().getString("style", "VANILLA"));

        double yaw = player.getYaw();
        double mapRotation = rotate ? -yaw + 180.0 : 0.0;
        int size = radius * 2 + 1;
        char[][] pixels = new char[size][size];
        Level level = player.getLevel();

        for (int sx = -radius; sx <= radius; sx++) {
            for (int sz = -radius; sz <= radius; sz++) {
                int px = sx + radius;
                int pz = sz + radius;
                if (round && sx * sx + sz * sz > radius * radius) {
                    pixels[pz][px] = GlyphPalette.TRANSPARENT;
                    continue;
                }

                double[] offset = rotate(sx * zoom, sz * zoom, mapRotation);
                int worldX = (int) Math.round(player.getX() + offset[0]);
                int worldZ = (int) Math.round(player.getZ() + offset[1]);
                pixels[pz][px] = renderWorldPixel(level, worldX, worldZ, style);
            }
        }

        if (renderPlayers) {
            overlayPlayers(player, pixels, radius, zoom, mapRotation);
        }

        StringBuilder out = new StringBuilder(size * (size + 1));
        for (int z = 0; z < size; z++) {
            if (z != 0) out.append('\n');
            out.append(pixels[z]);
        }

        String enlargeFlag = enlarge ? "b" : "a";
        String roundFlag = round ? "c" : "d";
        String sneakingFlag = player.isSneaking() ? "b" : "a";
        int renderScale = switch (detail) {
            case 1 -> 6;
            case 2 -> 4;
            case 3 -> 3;
            default -> 8;
        };
        char direction = '1';
        if (!rotate) {
            double rot = ((-yaw + 180.0) + 360.0) % 360.0;
            int index = Math.max(0, Math.min(ROTATION_NAME.length() - 1, (int) Math.round(1 + rot / 10.0)));
            direction = ROTATION_NAME.charAt(index);
        }

        String title = enlargeFlag + roundFlag + sneakingFlag + renderScale + direction + out + "mm_render_0";
        String compass = drawCompass(rotate ? ((-yaw + 180.0) + 360.0) : 0.0, round);
        // The title/subtitle are only a data transport for the JSON HUD.
        // Keep the vanilla title animation at zero length so the glyph payload itself is never rendered full-screen.
        player.sendTitle(title, compass, 0, 0, 0);
    }

    char renderWorldPixel(Level level, int x, int z, MapStyle style) {
        MapSample current = sample(level, x, z);
        int r = GlyphPalette.toNibble(current.red());
        int g = GlyphPalette.toNibble(current.green());
        int b = GlyphPalette.toNibble(current.blue());

        MapSample north = null;
        if (style != MapStyle.SIMPLE) north = sample(level, x, z - 1);

        double shade = 1.0;
        switch (style) {
            case SIMPLE -> shade = 1.0;
            case VANILLA -> {
                int delta = current.height() - north.height();
                boolean checker = ((x + (z & 1)) & 1) != 0;
                if ((delta > 0 && checker) || delta > 1) shade = 1.0;
                else if ((delta < 0 && checker) || delta < -1) shade = 0.8;
                else shade = 0.9;
            }
            case MODERN -> {
                int delta = current.height() - north.height();
                if (delta > 0) shade = 1.0;
                else if (delta < 0) shade = (1.0 / Math.abs(delta)) * 0.5 + 0.4;
                else shade = 0.9;
            }
            case HEIGHT -> {
                double height = Math.min(1.0, Math.max(0.0, (current.height() - 32.0) / 312.0) + 0.5) * 15.0;
                int h = Math.max(0, Math.min(15, (int) Math.floor(height * 0.9)));
                return GlyphPalette.rgb(h, h, h);
            }
            case MAGNIFICENT -> {
                MapSample diagonal = sample(level, x - 1, z - 1);
                int light = Math.max(0, current.height() - diagonal.height());
                int shadow = Math.max(0, north.height() - current.height());
                if (shadow > 0) shade = (1.0 / (shadow + 1.0)) * 0.5 + 0.4;
                else if (light > 0) shade = 1.0;
                else shade = 0.9;
            }
        }

        return GlyphPalette.rgb((int) Math.floor(r * shade), (int) Math.floor(g * shade), (int) Math.floor(b * shade));
    }

    private MapSample sample(Level level, int x, int z) {
        long now = System.currentTimeMillis();
        CacheKey key = new CacheKey(level.getName(), level.getDimension(), x, z);
        MapSample value = cache.get(key);
        if (value != null && value.expiresAt() > now) return value;

        BlockColor color = level.getMapColorAt(x, z);
        int height = level.getHighestBlockAt(x, z);
        long ttl = Math.max(250L, plugin.getConfig().getLong("cache-ttl-ms", 5000L));
        value = new MapSample(color.getRed(), color.getGreen(), color.getBlue(), height, now + ttl);
        cache.put(key, value);

        if (cache.size() > 250_000) {
            cache.entrySet().removeIf(e -> e.getValue().expiresAt() <= now);
        }
        return value;
    }

    private void overlayPlayers(Player owner, char[][] pixels, int radius, double zoom, double mapRotation) {
        for (Player other : owner.getLevel().getPlayers().values()) {
            if (other == owner || !other.isOnline()) continue;
            double dx = other.getX() - owner.getX();
            double dz = other.getZ() - owner.getZ();
            double[] screen = rotate(dx, dz, -mapRotation);
            int sx = (int) Math.round(screen[0] / zoom);
            int sz = (int) Math.round(screen[1] / zoom);
            if (Math.abs(sx) > radius || Math.abs(sz) > radius) continue;
            drawCross(pixels, sx + radius, sz + radius, GlyphPalette.PLAYER);
        }
    }

    private static void drawCross(char[][] pixels, int x, int z, char glyph) {
        int[][] points = {{0,0},{1,0},{-1,0},{0,1},{0,-1}};
        for (int[] p : points) {
            int px = x + p[0], pz = z + p[1];
            if (pz >= 0 && pz < pixels.length && px >= 0 && px < pixels[pz].length) pixels[pz][px] = glyph;
        }
    }

    private static double[] rotate(double x, double z, double degrees) {
        double rot = Math.toRadians(degrees);
        double sin = Math.sin(rot), cos = Math.cos(rot);
        return new double[]{z * sin + x * cos, z * cos - x * sin};
    }

    private static String drawCompass(double rot, boolean round) {
        char[][] canvas = new char[15][15];
        for (int z = 0; z < 15; z++) for (int x = 0; x < 15; x++) canvas[z][x] = GlyphPalette.TRANSPARENT;
        double distance = round ? 6.0 : 6.5;
        double[][] cardinals = {{0,-1},{1,0},{0,1},{-1,0}};
        for (int i = 0; i < cardinals.length; i++) {
            double[] p = rotate(cardinals[i][0] * distance, cardinals[i][1] * distance, rot);
            int x = Math.max(0, Math.min(14, (int) Math.round(p[0] + 7)));
            int z = Math.max(0, Math.min(14, (int) Math.round(p[1] + 7)));
            canvas[z][x] = COMPASS[i];
        }
        StringBuilder out = new StringBuilder(239);
        for (int z = 0; z < 15; z++) {
            if (z != 0) out.append('\n');
            out.append(canvas[z]);
        }
        return out.toString();
    }

    void clearCache() {
        cache.clear();
    }

    private record CacheKey(String levelName, int dimension, int x, int z) {}
}
