package org.powernukkitx.magnificentminimap;

import org.powernukkitx.Player;
import org.powernukkitx.permission.Permission;
import org.powernukkitx.scheduler.TaskHandler;
import org.powernukkitx.event.EventHandler;
import org.powernukkitx.event.Listener;
import org.powernukkitx.event.player.PlayerQuitEvent;
import org.powernukkitx.plugin.PluginBase;
import org.powernukkitx.plugin.annotation.PluginMeta;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@PluginMeta(
        name = "MagnificentMinimap",
        version = "1.0.0",
        authors = {
                "kamii",
                "qduoubp",
                "OpenAI",
                "Buddelbubi"
        },
        api = {
                "3.0.4"
        },
        website = "https://github.com/PowerNukkitX-Bundle/MagnificentMinimap"
)
public class MagnificentMinimap extends PluginBase implements Listener {
    private static MagnificentMinimap INSTANCE;

    public static MagnificentMinimap get() {
        return INSTANCE;
    }

    private final Set<UUID> disabledPlayers = new HashSet<>();
    private MinimapRenderer renderer;
    private TaskHandler renderTask;

    @Override
    public void onEnable() {
        INSTANCE = this;
        saveDefaultConfig();
        renderer = new MinimapRenderer(this);
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().addPermission(new Permission(
                "magnificentminimap.admin", "Reload the minimap configuration", Permission.DEFAULT_OP));
        renderTask = getServer().getScheduler().scheduleRepeatingTask(this, this::renderAll, renderPeriodTicks());
        getLogger().info("Enabled. Resource pack is bundled in the plugin JAR.");
    }

    @Override
    public void onDisable() {
        if (renderTask != null) renderTask.cancel();
        for (Player player : getServer().getOnlinePlayers().values()) {
            if (renderer != null) renderer.hide(player);
        }
        if (renderer != null) renderer.clearCache();
        disabledPlayers.clear();
        INSTANCE = null;
    }

    void reloadMinimap() {
        reloadConfig();
        renderer.clearCache();
        if (renderTask != null) renderTask.cancel();
        renderTask = getServer().getScheduler().scheduleRepeatingTask(this, this::renderAll, renderPeriodTicks());
        if (!getConfig().getBoolean("enabled", true)) {
            for (Player player : getServer().getOnlinePlayers().values()) renderer.hide(player);
        }
    }

    private void renderAll() {
        if (!getConfig().getBoolean("enabled", true)) return;
        for (Player player : getServer().getOnlinePlayers().values()) {
            try {
                renderer.render(player);
            } catch (Exception e) {
                getLogger().error("Could not render minimap for " + player.getName(), e);
            }
        }
    }

    int renderPeriodTicks() {
        int fps = Math.max(1, Math.min(20, getConfig().getInt("fps", 5)));
        return Math.max(1, (int) Math.ceil(20.0 / fps));
    }

    boolean isEnabledFor(Player player) {
        return getConfig().getBoolean("enabled", true) && !disabledPlayers.contains(player.getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        disabledPlayers.remove(event.getPlayer().getUniqueId());
    }

    boolean toggleMinimap(Player player) {
        if (!disabledPlayers.add(player.getUniqueId())) disabledPlayers.remove(player.getUniqueId());
        boolean enabled = isEnabledFor(player);
        if (enabled) {
            renderer.render(player);
        } else {
            renderer.hide(player);
        }
        return enabled;
    }
}
