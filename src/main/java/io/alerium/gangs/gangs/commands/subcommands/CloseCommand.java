package io.alerium.gangs.gangs.commands.subcommands;

import io.alerium.gangs.GangsPlugin;
import io.alerium.gangs.gangs.commands.SubCommand;
import io.alerium.gangs.gangs.objects.Gang;
import org.bukkit.entity.Player;

import java.util.Optional;

public class CloseCommand extends SubCommand {

    public CloseCommand(GangsPlugin plugin) {
        super(plugin, "close", "gangs.commands.close");
    }

    @Override
    public void execute(Player player, String[] args) {
        Optional<Gang> gang = plugin.getGangsManager().getGang(player.getUniqueId());
        if (!gang.isPresent()) {
            player.sendMessage(plugin.getMessage("messages.commands.close.noGang"));
            return;
        }
        
        if (!gang.get().isOwner(player.getUniqueId())) {
            player.sendMessage(plugin.getMessage("messages.commands.close.noGang"));
            return;
        }

        gang.get().setOpen(false);
        player.sendMessage(plugin.getMessage("messages.commands.close.done"));
    }

}
