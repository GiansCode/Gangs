package io.alerium.gangs.gangs.commands.subcommands;

import io.alerium.gangs.GangsPlugin;
import io.alerium.gangs.gangs.commands.SubCommand;
import io.alerium.gangs.gangs.objects.Gang;
import org.bukkit.entity.Player;

import java.util.Optional;

public class LeaveCommand extends SubCommand {

    public LeaveCommand(GangsPlugin plugin) {
        super(plugin, "leave", "gangs.commands.leave");
    }

    @Override
    public void execute(Player player, String[] args) {
        Optional<Gang> gang = plugin.getGangsManager().getGang(player.getUniqueId());
        if (!gang.isPresent()) {
            player.sendMessage(plugin.getMessage("messages.commands.leave.noGang"));
            return;
        }

        plugin.getGangsManager().removeMember(gang.get(), player.getUniqueId()).whenComplete((b, e) -> {
            if (!b) {
                player.sendMessage(plugin.getMessage("messages.commands.leave.error"));
                return;
            }

            player.sendMessage(plugin.getMessage("messages.commands.leave.done"));
        });
    }

}
