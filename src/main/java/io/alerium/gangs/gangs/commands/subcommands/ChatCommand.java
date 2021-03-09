package io.alerium.gangs.gangs.commands.subcommands;

import io.alerium.gangs.GangsPlugin;
import io.alerium.gangs.gangs.commands.SubCommand;
import io.alerium.gangs.gangs.objects.Gang;
import org.bukkit.entity.Player;

import java.util.Optional;

public class ChatCommand extends SubCommand {

    public ChatCommand(GangsPlugin plugin) {
        super(plugin, "chat", "gangs.commands.chat");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(plugin.getMessage("messages.commands.chat.usage"));
            return;
        }
        
        Optional<Gang> gang = plugin.getGangsManager().getGang(player.getUniqueId());
        if (!gang.isPresent()) {
            player.sendMessage(plugin.getMessage("messages.commands.chat.noGang"));
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        for (String arg : args)
            sb.append(arg).append(" ");
        
        String s = plugin.getMessage("messages.commands.chat.format", "%player%", player.getName(), "%message%", sb.toString());
        gang.get().getOnlinePlayers().forEach(p -> p.sendMessage(s));
    }

}
