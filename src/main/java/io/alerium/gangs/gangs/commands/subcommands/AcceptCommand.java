package io.alerium.gangs.gangs.commands.subcommands;

import io.alerium.gangs.GangsPlugin;
import io.alerium.gangs.gangs.commands.SubCommand;
import io.alerium.gangs.gangs.objects.Gang;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;

public class AcceptCommand extends SubCommand {
    
    public AcceptCommand(GangsPlugin plugin) {
        super(plugin, "accept", "gangs.commands.accept");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(plugin.getMessage("messages.commands.accept.usage"));
            return;
        }

        Optional<Gang> gang = plugin.getGangsManager().getGang(player.getUniqueId());
        if (!gang.isPresent()) {
            player.sendMessage(plugin.getMessage("messages.commands.accept.noGang"));
            return;
        }

        if (!gang.get().isOwner(player.getUniqueId())) {
            player.sendMessage(plugin.getMessage("messages.commands.accept.noGang"));
            return;
        }
        
        Player toAccept = Bukkit.getPlayer(args[0]);
        if (toAccept == null || !toAccept.isOnline()) {
            player.sendMessage(plugin.getMessage("messages.commands.accept.playerOffline"));
            return;
        }
        
        if (!plugin.getGangsManager().hasRequestedJoin(gang.get(), toAccept.getUniqueId())) {
            player.sendMessage(plugin.getMessage("messages.commands.accept.noJoinRequested"));
            return;
        }

        plugin.getGangsManager().addMember(gang.get(), toAccept.getUniqueId()).whenComplete((b, e) -> {
            if (!b) {
                player.sendMessage(plugin.getMessage("messages.commands.accept.error"));
                return;
            }

            player.sendMessage(plugin.getMessage("messages.commands.accept.done"));
        });
    }
    
}
