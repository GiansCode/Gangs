package io.alerium.gangs.gangs.commands.subcommands;

import io.alerium.gangs.GangsPlugin;
import io.alerium.gangs.gangs.commands.SubCommand;
import io.alerium.gangs.gangs.objects.Gang;
import org.bukkit.entity.Player;

import java.util.Optional;

public class OpenCommand extends SubCommand {

    public OpenCommand(GangsPlugin plugin) {
        super(plugin, "open", "gangs.commands.open");
    }

    @Override
    public void execute(Player player, String[] args) {
        Optional<Gang> gang = plugin.getGangsManager().getGang(player.getUniqueId());
        if (!gang.isPresent()) {
            player.sendMessage(plugin.getMessage("messages.commands.open.noGang"));
            return;
        }
        
        if (!gang.get().isOwner(player.getUniqueId())) {
            player.sendMessage(plugin.getMessage("messages.commands.open.noGang"));
            return;
        }

        gang.get().setOpen(true);
        player.sendMessage(plugin.getMessage("messages.commands.open.done"));
    }

}
