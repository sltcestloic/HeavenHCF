package fr.taeron.hcf.listeners;

import fr.taeron.hcf.*;
import fr.taeron.hcf.kits.events.KitApplyEvent;

import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.event.entity.*;

public class KitMapListener implements Listener{
	
    final HCF plugin;
    
    public KitMapListener(final HCF plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onCreatureSpawn(final CreatureSpawnEvent event) {
    }
    
    @EventHandler
    public void onJoin(final PlayerJoinEvent e) {
    }
    
    @EventHandler
    public void onDeath(final PlayerDeathEvent e) {
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerDropItem(final PlayerDropItemEvent event) {
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onKitApplyMonitor(final KitApplyEvent event) {
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onItemSpawn(final ItemSpawnEvent event) {
    }
}
