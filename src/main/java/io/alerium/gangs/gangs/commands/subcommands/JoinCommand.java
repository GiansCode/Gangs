package io.alerium.gangs.gangs.commands.subcommands;

import io.alerium.gangs.GangsPlugin;
import io.alerium.gangs.gangs.commands.SubCommand;
import io.alerium.gangs.gangs.objects.Gang;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;

public class JoinCommand extends SubCommand {

    public JoinCommand(GangsPlugin plugin) {
        super(plugin, "join", "gangs.commands.join");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(plugin.getMessage("messages.commands.join.usage"));
            return;
        }

        if (plugin.getGangsManager().getGang(player.getUniqueId()).isPresent()) {
            player.sendMessage(plugin.getMessage("messages.commands.join.alreadyGangMember"));
        }
        
        Optional<Gang> gang = plugin.getGangsManager().getGang(args[0]);
        if (!gang.isPresent()) {
            player.sendMessage(plugin.getMessage("messages.commands.join.notFound"));
            return;
        }
        
        if (gang.get().isOpen()) {
            plugin.getGangsManager().addMember(gang.get(), player.getUniqueId()).whenComplete((b, e) -> {
                if (!b) {
                    player.sendMessage(plugin.getMessage("messages.commands.join.error"));
                    return;
                }

                player.sendMessage(plugin.getMessage("messages.commands.join.joined", "%gang%", gang.get().getName()));
            });
            return;
        }
        
        Player owner = Bukkit.getPlayer(gang.get().getOwner());
        if (owner == null || !owner.isOnline()) {
            player.sendMessage(plugin.getMessage("messages.commands.join.ownerOffline"));
            return;
        }
        
        player.sendMessage(plugin.getMessage("messages.commands.join.requested"));
        owner.sendMessage(plugin.getMessage("messages.commands.join.request", "%player%", player.getName()));
    }

}
