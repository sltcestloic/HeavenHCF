package fr.taeron.hcf.listener;

import fr.taeron.hcf.*;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

public class CoreListener implements Listener
{
    private final HCF plugin;
    
    public CoreListener(final HCF plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
       HCF.getPlugin().getPlayerManager().loadPlayer(event.getPlayer());
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerKickEvent event) {
        event.setLeaveMessage(null);
        HCF.getPlugin().getPlayerManager().removePlayer(event.getPlayer());
    }
    
    @EventHandler
    public void tracker(PlayerCommandPreprocessEvent e){
    	if(e.getMessage().endsWith("tracker")){
    		e.setCancelled(true);
    		e.getPlayer().performCommand("tracker gui");
    	}
    }
    
    @EventHandler
    public void onSpawnerPlaced(final BlockPlaceEvent evt) {
        final ItemStack item = evt.getItemInHand();
        if (item == null) {
            return;
        }
        if (item.getType().equals((Object)Material.MOB_SPAWNER)) {
            String sType;
            if (item.getItemMeta().getLore() == null) {
                sType = "Pig Spawner";
            }
            else {
                sType = item.getItemMeta().getLore().get(0);
            }
            final Block setBlock = evt.getBlock();
            setBlock.setType(Material.MOB_SPAWNER);
            final CreatureSpawner s = (CreatureSpawner)setBlock.getState();
            s.setSpawnedType(EntityType.valueOf(sType.split(" ")[0].toUpperCase()));
        }
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        event.setQuitMessage(null);
        final Player player = event.getPlayer();
        this.plugin.getVisualiseHandler().clearVisualBlocks(player, null, null, false);
        this.plugin.getUserManager().getUser(player.getUniqueId()).setShowClaimMap(false);
        this.plugin.getUserManager().getUser(player.getUniqueId()).lastLogoutTime = System.currentTimeMillis();
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerChangedWorld(final PlayerChangedWorldEvent event) {
        final Player player = event.getPlayer();
        this.plugin.getVisualiseHandler().clearVisualBlocks(player, null, null, false);
        this.plugin.getUserManager().getUser(player.getUniqueId()).setShowClaimMap(false);
    }
}
