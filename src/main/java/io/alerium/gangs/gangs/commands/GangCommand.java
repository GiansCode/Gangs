package io.alerium.gangs.gangs.commands;

import io.alerium.gangs.GangsPlugin;
import io.alerium.gangs.gangs.commands.subcommands.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GangCommand implements CommandExecutor {
    
    private final GangsPlugin plugin;
    private final Map<String, SubCommand> commands = new HashMap<>();
    
    public GangCommand(GangsPlugin plugin) {
        this.plugin = plugin;

        Arrays.asList(
                new ChatCommand(plugin),
                new CloseCommand(plugin),
                new CreateCommand(plugin),
                new DeleteCommand(plugin),
                new JoinCommand(plugin), 
                new KickCommand(plugin),
                new LeaveCommand(plugin),
                new OpenCommand(plugin),
                new AcceptCommand(plugin)
        ).forEach(command -> commands.put(command.getName(), command));
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player))
            return true;
        
        if (args.length == 0 || !commands.containsKey(args[0].toLowerCase())) {
            plugin.getListMessage("messages.commands.help").forEach(sender::sendMessage);
            return true;
        }
        
        SubCommand subCommand = commands.get(args[0].toLowerCase());
        if (!sender.hasPermission(subCommand.getPermission())) {
            sender.sendMessage(plugin.getMessage("messages.commands.noPermission"));
            return true;
        }
        
        subCommand.execute((Player) sender, Arrays.copyOfRange(args, 1, args.length));
        return true;
    }
    
}
