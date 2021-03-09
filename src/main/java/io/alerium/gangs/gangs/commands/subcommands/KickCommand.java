package io.alerium.gangs.gangs.commands.subcommands;

import io.alerium.gangs.GangsPlugin;
import io.alerium.gangs.gangs.commands.SubCommand;
import io.alerium.gangs.gangs.objects.Gang;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Optional;

public class KickCommand extends SubCommand {

    public KickCommand(GangsPlugin plugin) {
        super(plugin, "kick", "gangs.commands.kick");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(plugin.getMessage("messages.commands.kick.usage"));
            return;
        }
        
        Optional<Gang> gang = plugin.getGangsManager().getGang(player.getUniqueId());
        if (!gang.isPresent()) {
            player.sendMessage(plugin.getMessage("messages.commands.kick.noGang"));
            return;
        }
        
        if (!gang.get().isOwner(player.getUniqueId())) {
            player.sendMessage(plugin.getMessage("messages.commands.kick.noGang"));
            return;
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
        if (offlinePlayer == null) {
            player.sendMessage(plugin.getMessage("messages.commands.kick.playerNotFound"));
            return;
        }
        
        if (!gang.get().isMember(offlinePlayer.getUniqueId())) {
            player.sendMessage(plugin.getMessage("messages.commands.kick.playerNotFound"));
            return;
        }
        
        plugin.getGangsManager().removeMember(gang.get(), offlinePlayer.getUniqueId()).whenComplete((b, e) -> {
            if (!b) {
                player.sendMessage(plugin.getMessage("messages.commands.kick.error"));
                return;
            }

            player.sendMessage(plugin.getMessage("messages.commands.kick.done"));
        });
    }

}
