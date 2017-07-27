package fr.taeron.hcf.timer.type;

import fr.taeron.hcf.timer.*;
import java.util.concurrent.*;
import org.bukkit.event.*;
import java.util.*;
import org.bukkit.event.player.*;
import org.bukkit.event.entity.*;
import org.bukkit.entity.*;
import org.bukkit.*;

import fr.taeron.hcf.*;
import fr.taeron.hcf.combatlog.CombatLogListener;

public class LogoutTimer extends PlayerTimer implements Listener
{
    public LogoutTimer() {
        super("Logout", TimeUnit.SECONDS.toMillis(30L), false);
    }
    
    public String getScoreboardPrefix() {
        return ChatColor.RED.toString() + ChatColor.BOLD;
    }
    
    private void checkMovement(final Player player, final Location from, final Location to) {
        if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ()) {
            return;
        }
        if (this.getRemaining(player) > 0L) {
            player.sendMessage(ChatColor.RED + "Tu as bougé d'un block, ton " + this.getDisplayName() + ChatColor.RED + " timer a été annulé.");
            this.clearCooldown(player);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerMove(final PlayerMoveEvent event) {
        this.checkMovement(event.getPlayer(), event.getFrom(), event.getTo());
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
        this.checkMovement(event.getPlayer(), event.getFrom(), event.getTo());
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
        CombatLogListener.safelyDisconnect(player, ChatColor.GREEN + "Tu as été déconnecté en sécurité.");
    }
    
    public void run(final Player player) {
        final long remainingMillis = this.getRemaining(player);
        if (remainingMillis > 0L) {
            player.sendMessage("§9Ton" + this.getDisplayName() + ChatColor.BLUE + " timer va te déconnecter dans " + ChatColor.BOLD + HCF.getRemaining(remainingMillis, true, false) + ChatColor.BLUE + '.');
        }
    }
}
