package fr.taeron.hcf.tablist.listeners;

import org.bukkit.plugin.*;

import fr.taeron.hcf.HCF;

import org.bukkit.event.*;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class TabPlayerListener implements Listener
{
    HCF plugin;
    
    public TabPlayerListener(HCF plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)plugin);
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.plugin.removeTabPlayer(event.getPlayer());
    }
    
    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        this.plugin.removeTabPlayer(event.getPlayer());
    }
}
