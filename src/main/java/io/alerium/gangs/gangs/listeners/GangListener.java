package io.alerium.gangs.gangs.listeners;

import io.alerium.gangs.GangsPlugin;
import io.alerium.gangs.gangs.objects.Gang;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Optional;

@RequiredArgsConstructor
public class GangListener implements Listener {
    
    private final GangsPlugin plugin;
 
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player))
            return;
        
        Player player = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();
        Optional<Gang> damagerGang = plugin.getGangsManager().getGang(damager.getUniqueId());
        if (damagerGang.isPresent() && damagerGang.get().isMember(player.getUniqueId()))
            event.setCancelled(true);
    }
    
}
