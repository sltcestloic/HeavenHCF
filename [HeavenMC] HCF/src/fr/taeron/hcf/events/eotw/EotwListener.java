package fr.taeron.hcf.events.eotw;

import fr.taeron.hcf.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.event.entity.*;
import org.bukkit.*;
import fr.taeron.hcf.faction.event.*;
import fr.taeron.hcf.faction.event.cause.*;
import fr.taeron.hcf.faction.type.*;
import fr.taeron.hcf.kits.events.KitApplyEvent;

public class EotwListener implements Listener
{
    private final HCF plugin;
    
    public EotwListener(final HCF plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final EotwHandler.EotwRunnable runnable = this.plugin.getEotwHandler().getRunnable();
        if (runnable != null) {
            runnable.handleDisconnect(event.getPlayer());
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerKick(final PlayerKickEvent event) {
        final EotwHandler.EotwRunnable runnable = this.plugin.getEotwHandler().getRunnable();
        if (runnable != null) {
            runnable.handleDisconnect(event.getPlayer());
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        final EotwHandler.EotwRunnable runnable = this.plugin.getEotwHandler().getRunnable();
        if (runnable != null) {
            runnable.handleDisconnect(event.getEntity());
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onLandClaim(KitApplyEvent event) {
        if (!event.isForce() && this.plugin.getEotwHandler().isEndOfTheWorld()) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "Tu ne peut pas prendre de kit pendant l'EOTW.");
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onFactionClaimChange(final FactionClaimChangeEvent event) {
        if (this.plugin.getEotwHandler().isEndOfTheWorld() && event.getCause() == ClaimChangeCause.CLAIM) {
            final Faction faction = event.getClaimableFaction();
            if (faction instanceof PlayerFaction) {
                event.setCancelled(true);
                event.getSender().sendMessage(ChatColor.RED + "Tu ne peux pas claim durant l'EOTW.");
            }
        }
    }
}
