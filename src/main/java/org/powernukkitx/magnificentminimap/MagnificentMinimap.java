package org.powernukkitx.magnificentminimap;

import org.powernukkitx.plugin.PluginBase;
import org.powernukkitx.plugin.annotation.PluginMeta;

@PluginMeta(
        name = "MagnificentMinimap",
        version = "1.0.0",
        authors = {
                "PowerNukkitX-Bundle"
        },
        api = {
                "3.0.0"
        },
        website = "https://github.com/PowerNukkitX-Bundle/MagnificentMinimap"
)
public class MagnificentMinimap extends PluginBase {

    private static MagnificentMinimap INSTANCE;

    @Override
    public void onEnable() {
        INSTANCE = this;
    }

    public static MagnificentMinimap get() {
        return INSTANCE;
    }
}