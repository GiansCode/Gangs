package io.alerium.gangs.gangs.commands.subcommands;

import io.alerium.gangs.GangsPlugin;
import io.alerium.gangs.gangs.commands.SubCommand;
import io.alerium.gangs.gangs.objects.Gang;
import org.bukkit.entity.Player;

import java.util.Optional;

public class DeleteCommand extends SubCommand {

    public DeleteCommand(GangsPlugin plugin) {
        super(plugin, "delete", "gangs.commands.delete");
    }

    @Override
    public void execute(Player player, String[] args) {
        Optional<Gang> gang = plugin.getGangsManager().getGang(player.getUniqueId());
        if (!gang.isPresent()) {
            player.sendMessage(plugin.getMessage("messages.commands.delete.noGang"));
            return;
        }
        
        if (!gang.get().isOwner(player.getUniqueId())) {
            player.sendMessage(plugin.getMessage("messages.commands.delete.noGang"));
            return;
        }

        plugin.getGangsManager().deleteGang(gang.get()).whenComplete((b, e) -> {
            if (!b) {
                player.sendMessage(plugin.getMessage("messages.commands.delete.error"));
                return;
            }

            player.sendMessage(plugin.getMessage("messages.commands.delete.done"));
        });
    }

}
