package io.alerium.gangs;

import io.alerium.gangs.gangs.GangsManager;
import io.alerium.gangs.gangs.commands.GangCommand;
import io.alerium.gangs.gangs.listeners.GangListener;
import io.alerium.gangs.utils.MySQL;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class GangsPlugin extends JavaPlugin {
    
    @Getter private MySQL mySQL;
    @Getter private GangsManager gangsManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        
        mySQL = new MySQL(getConfig().getString("hostname"), getConfig().getString("username"), getConfig().getString("password"), getConfig().getString("database"), getConfig().getInt("port"));
        try {
            mySQL.connect();
        } catch (SQLException e) {
            getLogger().log(Level.SEVERE, "An error occurred while connecting to the database", e);
            return;
        }
        
        gangsManager = new GangsManager(this);
        if (!gangsManager.enable())
            return;
        
        getCommand("gang").setExecutor(new GangCommand(this));
        Bukkit.getPluginManager().registerEvents(new GangListener(this), this);
        
        getLogger().info("Plugin enabled!");
    }

    @Override
    public void onDisable() {
        gangsManager.disable();
        mySQL.disconnect();
    }

    public String getMessage(String path, String... placeholders) {
        return parsePlaceholders(getConfig().getString(path), placeholders);
    }
    
    public List<String> getListMessage(String path, String... placeholders) {
        return getConfig().getStringList(path).stream().map(s -> parsePlaceholders(s, placeholders)).collect(Collectors.toList());
    }
    
    public void async(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(this, runnable);
    }
    
    private String parsePlaceholders(String s, String... placeholders) {
        String newS = s;
        for (int i = 0; i < placeholders.length; i+=2)
            newS = newS.replaceAll(placeholders[i], placeholders[i + 1]);

        return ChatColor.translateAlternateColorCodes('&', newS);
    }
    
}
