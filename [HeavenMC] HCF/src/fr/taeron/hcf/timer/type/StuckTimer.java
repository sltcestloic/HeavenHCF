package fr.taeron.hcf.timer.type;

import java.util.concurrent.*;

import java.util.*;
import fr.taeron.hcf.timer.*;
import javax.annotation.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.event.entity.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import fr.taeron.hcf.faction.*;
import fr.taeron.hcf.*;

public class StuckTimer extends PlayerTimer implements Listener
{
	private final Map<UUID, Location> startedLocations = new HashMap<>();
	
    public StuckTimer() {
        super("Stuck", TimeUnit.MINUTES.toMillis(2L), false);
    }
    
    public String getScoreboardPrefix() {
        return ChatColor.RED.toString() + ChatColor.BOLD;
    }
    
    @Override
    public TimerRunnable clearCooldown(final UUID uuid) {
        final TimerRunnable runnable = super.clearCooldown(uuid);
        if (runnable != null) {
            this.startedLocations.remove(uuid);
            return runnable;
        }
        return null;
    }
    
    @Override
    public boolean setCooldown(@Nullable final Player player, final UUID playerUUID, final long millis, final boolean force) {
        if (player != null && super.setCooldown(player, playerUUID, millis, force)) {
            this.startedLocations.put(playerUUID, player.getLocation());
            return true;
        }
        return false;
    }
    
    private void checkMovement(final Player player, final Location from, final Location to) {
        final UUID uuid = player.getUniqueId();
        if (this.getRemaining(uuid) > 0L) {
            if (from == null) {
                this.clearCooldown(uuid);
                return;
            }
            final int xDiff = Math.abs(from.getBlockX() - to.getBlockX());
            final int yDiff = Math.abs(from.getBlockY() - to.getBlockY());
            final int zDiff = Math.abs(from.getBlockZ() - to.getBlockZ());
            if (xDiff > 5 || yDiff > 5 || zDiff > 5) {
                this.clearCooldown(uuid);
                player.sendMessage(ChatColor.RED + "Tu as bougé de plus de " + ChatColor.BOLD + 5 + ChatColor.RED + " blocks. Ton " + this.getDisplayName() + ChatColor.RED + " timer a été annulé.");
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerMove(final PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();
        if (this.getRemaining(uuid) > 0L) {
            final Location from = (Location) this.startedLocations.get(uuid);
            this.checkMovement(player, from, event.getTo());
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();
        if (this.getRemaining(uuid) > 0L) {
            final Location from = (Location) this.startedLocations.get(uuid);
            this.checkMovement(player, from, event.getTo());
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerKick(final PlayerKickEvent event) {
        final UUID uuid = event.getPlayer().getUniqueId();
        if (this.getRemaining(event.getPlayer().getUniqueId()) > 0L) {
            this.clearCooldown(uuid);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final UUID uuid = event.getPlayer().getUniqueId();
        if (this.getRemaining(event.getPlayer().getUniqueId()) > 0L) {
            this.clearCooldown(uuid);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDamage(final EntityDamageEvent event) {
        final Entity entity = event.getEntity();
        if (entity instanceof Player) {
            final Player player = (Player)entity;
            if (this.getRemaining(player) > 0L) {
                player.sendMessage(ChatColor.RED + "Tu as reçu un dégât, ton " + this.getDisplayName() + ChatColor.RED + " timer a été annulé.");
                this.clearCooldown(player);
            }
        }
    }
    
    @Override
    public void onExpire(final UUID userUUID) {
        final Player player = Bukkit.getPlayer(userUUID);
        if (player == null) {
            return;
        }
        final Location nearest = LandMap.getNearestSafePosition(player, player.getLocation(), 24);
        if (nearest == null) {
        	Location spawn = player.getWorld().getSpawnLocation().clone().add(0.5, 0.5, 0.5);
        	player.teleport(spawn);
            //CombatLogListener.safelyDisconnect(player, ChatColor.RED + "Unable to find a safe location, you have been safely logged out.");
            player.sendMessage(ChatColor.RED + "Aucun endroit sur n'a été trouvé dans les environs, tu as été téléporté au spawn.");
            return;
        }
        if (player.teleport(nearest, PlayerTeleportEvent.TeleportCause.PLUGIN)) {
            player.sendMessage(ChatColor.YELLOW + "Ton " + this.getDisplayName() + ChatColor.YELLOW + " timer t'a téléporté à la position sure la plus proche.");
        }
    }
    
    public void run(final Player player) {
        final long remainingMillis = this.getRemaining(player);
        if (remainingMillis > 0L) {
            player.sendMessage("§9Ton " + this.getDisplayName() + ChatColor.BLUE + " timer va te téléporter dans " + ChatColor.BOLD + HCF.getRemaining(remainingMillis, true, false) + ChatColor.BLUE + '.');
        }
    }
}
