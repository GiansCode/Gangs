package io.alerium.gangs.gangs;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.alerium.gangs.GangsPlugin;
import io.alerium.gangs.gangs.objects.Gang;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

@RequiredArgsConstructor
public class GangsManager {
    
    private final GangsPlugin plugin;
    private final List<Gang> gangs = Collections.synchronizedList(new ArrayList<>());
    private final GangsDB database;
    private final Cache<UUID, Integer> requestedJoins = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();
    
    public GangsManager(GangsPlugin plugin) {
        this.plugin = plugin;
        database = new GangsDB(plugin);
    }
    
    public boolean enable() {
        List<Gang> loadedGangs = database.loadGangs();
        if (loadedGangs == null)
            return false;
        
        gangs.addAll(loadedGangs);
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> database.updateGangs(gangs), 20 * 60 * 5, 20 * 60 * 5);
        return true;
    }
    
    public void disable() {
        database.updateGangs(gangs);
    }
    
    public Optional<Gang> getGang(UUID player) {
        for (Gang gang : gangs) {
            if (gang.isMember(player))
                return Optional.of(gang);
        }
        
        return Optional.empty();
    }
    
    public Optional<Gang> getGang(String name) {
        for (Gang gang : gangs) {
            if (gang.getName().equalsIgnoreCase(name))
                return Optional.of(gang);
        }
        
        return Optional.empty();
    }
    
    public void requestJoin(Gang gang, UUID player) {
        requestedJoins.put(player, gang.getId());
    }
    
    public boolean hasRequestedJoin(Gang gang, UUID player) {
        Integer id = requestedJoins.getIfPresent(player);
        return id != null && id == gang.getId();
    }
    
    public CompletableFuture<Boolean> createGang(UUID owner, String name) {
        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        database.createGang(name, owner).whenComplete((i, e) -> {
            if (e != null) {
                plugin.getLogger().log(Level.WARNING, "An error occurred while creating a gang", e);
                cf.complete(false);
                return;
            }
            
            Gang gang = new Gang(i, name, owner, new ArrayList<>(), false);
            gangs.add(gang);
            cf.complete(true);
        });
        return cf;
    }
    
    public CompletableFuture<Boolean> deleteGang(Gang gang) {
        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        database.deleteGang(gang.getId()).whenComplete((i, e) -> {
            if (e != null) {
                plugin.getLogger().log(Level.WARNING, "An error occurred while deleting a gang", e);
                cf.complete(false);
                return;
            }

            gangs.remove(gang);
            cf.complete(true);
        });
        return cf;
    }
    
    public CompletableFuture<Boolean> addMember(Gang gang, UUID member) {
        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        database.addMember(gang, member).whenComplete((i, e) -> {
            if (e != null) {
                plugin.getLogger().log(Level.WARNING, "An error occurred while adding a member to a gang", e);
                cf.complete(false);
                return;
            }

            gang.getMembers().add(member);
            cf.complete(true);
        });
        return cf;
    }
    
    public CompletableFuture<Boolean> removeMember(Gang gang, UUID member) {
        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        database.removeMember(gang, member).whenComplete((i, e) -> {
            if (e != null) {
                plugin.getLogger().log(Level.WARNING, "An error occurred while adding a member to a gang", e);
                cf.complete(false);
                return;
            }

            gang.getMembers().remove(member);
            cf.complete(true);
        });
        return cf;
    }
    
}
