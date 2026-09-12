package org.powernukkitx.magnificentminimap;

import org.powernukkitx.Player;
import org.powernukkitx.command.Command;
import org.powernukkitx.command.CommandContext;
import org.powernukkitx.command.CommandResult;
import org.powernukkitx.command.PluginIdentifiableCommand;
import org.powernukkitx.command.SenderType;
import org.powernukkitx.command.route.RouteTree;
import org.powernukkitx.command.route.node.RouteNode;
import org.powernukkitx.plugin.annotation.CommandDefinition;

@CommandDefinition(
        name = "minimap",
        description = "Toggle the minimap or reload configuration",
        usage = "/minimap [toggle|reload]",
        permission = "magnificentminimap.command"
)
public final class MinimapCommand extends Command implements PluginIdentifiableCommand {
    @Override
    public MagnificentMinimap getPlugin() {
        return MagnificentMinimap.get();
    }

    @Override
    protected void buildCommandTree(RouteTree tree) {
        tree.getRoot()
                .exec(this::toggle)
                .then(RouteNode.literal("toggle")
                        .senderType(SenderType.PLAYER)
                        .exec(this::toggle))
                .then(RouteNode.literal("reload")
                        .permission("magnificentminimap.admin",
                                "You do not have permission to reload Magnificent Minimap.")
                        .exec(this::reload));
    }

    private CommandResult toggle(CommandContext context) {
        MagnificentMinimap plugin = getPlugin();
        if (plugin == null || !plugin.isEnabled()) return CommandResult.fail();
        if (!(context.getSender() instanceof Player player)) {
            return CommandResult.fail("Use /minimap reload from console.");
        }
        boolean enabled = plugin.toggleMinimap(player);
        player.sendMessage("Magnificent Minimap: " + (enabled ? "enabled" : "disabled"));
        return CommandResult.success();
    }

    private CommandResult reload(CommandContext context) {
        MagnificentMinimap plugin = getPlugin();
        if (plugin == null || !plugin.isEnabled()) return CommandResult.fail();
        plugin.reloadMinimap();
        context.getSender().sendMessage("Magnificent Minimap config reloaded.");
        return CommandResult.success();
    }
}
