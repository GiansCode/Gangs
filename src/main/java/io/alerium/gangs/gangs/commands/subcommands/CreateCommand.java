package io.alerium.gangs.gangs.commands.subcommands;

import io.alerium.gangs.GangsPlugin;
import io.alerium.gangs.gangs.commands.SubCommand;
import org.bukkit.entity.Player;

public class CreateCommand extends SubCommand {

    public CreateCommand(GangsPlugin plugin) {
        super(plugin, "create", "gangs.commands.create");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(plugin.getMessage("messages.commands.create.usage"));
            return;
        }
        
        if (plugin.getGangsManager().getGang(player.getUniqueId()).isPresent()) {
            player.sendMessage(plugin.getMessage("messages.commands.create.alreadyGangMember"));
            return;
        }
        
        if (plugin.getGangsManager().getGang(args[0]).isPresent()) {
            player.sendMessage(plugin.getMessage("messages.commands.create.alreadyExist"));
            return;
        }

        plugin.getGangsManager().createGang(player.getUniqueId(), args[0]).whenComplete((b, e) -> {
            if (!b) {
                player.sendMessage(plugin.getMessage("messages.commands.create.error"));
                return;
            }

            player.sendMessage(plugin.getMessage("messages.commands.create.done"));
        });
    }

}
