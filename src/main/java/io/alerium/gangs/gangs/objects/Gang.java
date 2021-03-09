package io.alerium.gangs.gangs.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor @Getter
public class Gang {
    
    private final int id;
    private final String name;
    private final UUID owner;
    private final List<UUID> members;
    
    @Setter private boolean open;
    
    public boolean isOwner(UUID uuid) {
        return owner.equals(uuid);
    }
    
    public boolean isMember(UUID uuid) {
        if (isOwner(uuid))
            return true;
        
        return members.contains(uuid);
    }
    
    public List<Player> getOnlinePlayers() {
        List<Player> players = members.stream().map(Bukkit::getPlayer).filter(player -> player != null && player.isOnline()).collect(Collectors.toList());
        Player ownerPlayer = Bukkit.getPlayer(owner);
        if (ownerPlayer != null && ownerPlayer.isOnline())
            players.add(ownerPlayer);
        
        return players;
    }
    
}
