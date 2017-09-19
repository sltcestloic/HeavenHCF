package fr.taeron.hcf.listeners;

import fr.taeron.hcf.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.heavenmc.core.Core;

public class CoreListener implements Listener
{
    private final HCF plugin;
    
    public CoreListener(final HCF plugin) {
        this.plugin = plugin;
    }

    
    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        e.setJoinMessage(null);
        new BukkitRunnable() {
			
			@Override
			public void run() {
				try {
					java.sql.Connection c = Core.getInstance().getConnection();
					int playerid = 0;
					PreparedStatement s = c.prepareStatement("SELECT playerid FROM `players` WHERE uuid = ?");
					s.setString(1, e.getPlayer().getUniqueId().toString());
					ResultSet rs = s.executeQuery();
					if (rs.next()) {
						playerid = rs.getInt("playerid");
						HCF.getPlugin().getCompatUserManager().registerPlayer(e.getPlayer(), playerid);
				        HCF.getPlugin().getSQLManager().loadPlayer(e.getPlayer());
					} else {
						new BukkitRunnable() {
							@Override
							public void run() {
								e.getPlayer().kickPlayer("§cErreur lors du chargement de tes données.");
							}
						}.runTask(HCF.getPlugin());
					}
					rs.close();
					s.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
			}
		}.runTaskAsynchronously(HCF.getPlugin());
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerKickEvent event) {
        event.setLeaveMessage(null);
        HCF.getPlugin().getSQLManager().saveData(event.getPlayer());
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
