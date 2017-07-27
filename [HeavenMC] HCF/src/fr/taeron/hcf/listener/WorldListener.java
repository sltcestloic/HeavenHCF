package fr.taeron.hcf.listener;

import fr.taeron.hcf.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.*;
import org.spigotmc.event.player.*;
import org.bukkit.event.inventory.*;
import org.bukkit.material.*;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.entity.*;

public class WorldListener implements Listener
{
    public static final String DEFAULT_WORLD_NAME = "world";
    private final HCF plugin;
    
    public WorldListener(final HCF plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(ignoreCancelled = false, priority = EventPriority.HIGH)
    public void onEntityExplode(final EntityExplodeEvent event) {
        event.blockList().clear();
        if (event.getEntity() instanceof EnderDragon) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockChange(final BlockFromToEvent event) {
        if (event.getBlock().getType() == Material.DRAGON_EGG) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityPortalEnter(final EntityPortalEvent event) {
        if (event.getEntity() instanceof EnderDragon) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBedEnter(final PlayerBedEnterEvent event) {
        event.setCancelled(true);
        event.getPlayer().sendMessage(ChatColor.RED + "Les lits sont désactivés en HCF.");
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onWitherChangeBlock(final EntityChangeBlockEvent event) {
        final Entity entity = event.getEntity();
        if (entity instanceof Wither || entity instanceof EnderDragon) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockFade(final BlockFadeEvent event) {
        switch (event.getBlock().getType()) {
            case SNOW:
            case ICE: {
                event.setCancelled(true);
                break;
            }
            default: {
            	break;
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerRespawn(final PlayerRespawnEvent event) {
        event.setRespawnLocation(Bukkit.getWorld("world").getSpawnLocation().add(0.5, 0.0, 0.5));
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerSpawn(final PlayerSpawnLocationEvent event) {
        final Player player = event.getPlayer();
        if (!player.hasPlayedBefore()) {
            this.plugin.getEconomyManager().addBalance(player.getUniqueId(), 500);
            event.setSpawnLocation(Bukkit.getWorld("world").getSpawnLocation().add(0.5, 0.0, 0.5));
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onInventoryOpen(final InventoryOpenEvent event) {
        if (event.getInventory() instanceof EnderChest) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockIgnite(final BlockIgniteEvent event) {
        if (event.getCause() == BlockIgniteEvent.IgniteCause.SPREAD) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onCreatureSpawn(final CreatureSpawnEvent event) {
        if (event.getEntity() instanceof Squid) {
            event.setCancelled(true);
        }
    }
}
