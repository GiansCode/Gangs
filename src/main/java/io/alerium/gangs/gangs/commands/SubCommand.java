package io.alerium.gangs.gangs.commands;

import io.alerium.gangs.GangsPlugin;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor @Getter
public abstract class SubCommand {
    
    @Getter(AccessLevel.NONE) protected final GangsPlugin plugin;
    private final String name;
    private final String permission;
    
    public abstract void execute(Player player, String[] args);
    
}
