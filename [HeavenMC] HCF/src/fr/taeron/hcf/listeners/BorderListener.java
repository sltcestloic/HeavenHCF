package fr.taeron.hcf.listeners;

import fr.taeron.hcf.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.*;

public class BorderListener implements Listener
{
    
    public static boolean isWithinBorder(final Location location) {
        final int borderSize = ConfigurationService.BORDER_SIZES.get(location.getWorld().getEnvironment());
        return Math.abs(location.getBlockX()) <= borderSize && Math.abs(location.getBlockZ()) <= borderSize;
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onCreaturePreSpawn(final CreatureSpawnEvent event) {
        if (!isWithinBorder(event.getLocation())) {
            event.setCancelled(true);
        }
        
        if(event.getEntity() instanceof Monster){
        	Location loc = event.getLocation();
        	if(loc.getWorld().getName().equalsIgnoreCase("world_the_end")) return;
        	if(loc.getX() < 300 && loc.getZ() < 300 && loc.getX() > -300 && loc.getZ() > -300){
        		event.setCancelled(true);
        	}
        }
        
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBucketEmpty(final PlayerBucketFillEvent event) {
        if (!isWithinBorder(event.getBlockClicked().getLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "Tu ne peux pas construire en dehors de la bordure.");
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBucketEmpty(final PlayerBucketEmptyEvent event) {
        if (!isWithinBorder(event.getBlockClicked().getLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "Tu ne peux pas construire en dehors de la bordure.");
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockPlace(final BlockPlaceEvent event) {
        if (!isWithinBorder(event.getBlock().getLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "Tu ne peux pas construire en dehors de la bordure.");
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockBreak(final BlockBreakEvent event) {
        if (!isWithinBorder(event.getBlock().getLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "Tu ne peux pas construire en dehors de la bordure.");
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerMove(final PlayerMoveEvent event) {
        final Location from = event.getFrom();
        final Location to = event.getTo();
        if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ()) {
            return;
        }
        if (!isWithinBorder(to) && isWithinBorder(from)) {
            final Player player = event.getPlayer();
            player.sendMessage(ChatColor.RED + "Tu as dépassé la bordure.");
            event.setTo(from);
            final Entity vehicle = player.getVehicle();
            if (vehicle != null) {
                vehicle.eject();
                vehicle.teleport(from);
                vehicle.setPassenger((Entity)player);
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerPortal(final PlayerPortalEvent event) {
        final Location to = event.getTo();
        if (!isWithinBorder(to)) {
            final PlayerTeleportEvent.TeleportCause cause = event.getCause();
            if (cause != PlayerTeleportEvent.TeleportCause.NETHER_PORTAL || (cause == PlayerTeleportEvent.TeleportCause.ENDER_PEARL && isWithinBorder(event.getFrom()))) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "Tu ne peux pas dépasser la bordure.");
            }
            else {
                final World.Environment toEnvironment = to.getWorld().getEnvironment();
                if (toEnvironment != World.Environment.NORMAL) {
                    return;
                }
                final int x = to.getBlockX();
                final int z = to.getBlockZ();
                final int borderSize = ConfigurationService.BORDER_SIZES.get(toEnvironment);
                boolean extended = false;
                if (Math.abs(x) > borderSize) {
                    to.setX((x > 0) ? (borderSize - 50) : (-borderSize + 50));
                    extended = true;
                }
                if (Math.abs(z) > borderSize) {
                    to.setZ((z > 0) ? (borderSize - 50) : (-borderSize + 50));
                    extended = true;
                }
                if (extended) {
                    to.add(0.5, 0.0, 0.5);
                    event.setTo(to);
                    event.getPlayer().sendMessage(ChatColor.RED + "Ce portail téléportait en dehors de la bordure, sa position a donc été modifiée.");
                }
            }
        }
    }
}
