package fr.taeron.hcf.timer.type;

import fr.taeron.hcf.timer.*;
import fr.taeron.hcf.*;
import java.util.concurrent.*;
import org.bukkit.event.*;
import java.util.*;

import org.bukkit.*;
import fr.taeron.hcf.visualise.*;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;
import fr.taeron.hcf.faction.event.*;
import fr.taeron.hcf.kits.events.KitApplyEvent;

import org.bukkit.command.*;
import org.bukkit.event.entity.*;
import org.bukkit.enchantments.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.heavenmc.core.util.BukkitUtils;

import fr.taeron.hcf.timer.event.*;
import org.bukkit.event.player.*;

public class SpawnTagTimer extends PlayerTimer implements Listener{
	
    private final HCF plugin;
    
    public SpawnTagTimer(final HCF plugin) {
        super("Combat", TimeUnit.SECONDS.toMillis(30L));
        this.plugin = plugin;
    }
    
    public String getScoreboardPrefix() {
        return ChatColor.DARK_RED.toString() + ChatColor.BOLD;
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onKitApply(KitApplyEvent event) {
        final Player player = event.getPlayer();
        if (!event.isForce() && this.getRemaining(player) > 0L) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Tu ne peux pas prendre de kit tant que ton Combat timer est actif.");
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onTimerStop(final TimerClearEvent event) {
        if (event.getTimer().equals(this)) {
            final com.google.common.base.Optional<UUID> optionalUserUUID = event.getUserUUID();
            if (optionalUserUUID.isPresent()) {
                this.onExpire((UUID)optionalUserUUID.get());
            }
        }
    }
    
    @Override
    public void onExpire(final UUID userUUID) {
        final Player player = Bukkit.getPlayer(userUUID);
        if (player == null) {
            return;
        }
        this.plugin.getVisualiseHandler().clearVisualBlocks(player, VisualType.SPAWN_BORDER, null);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onFactionJoin(final PlayerJoinFactionEvent event) {
        final com.google.common.base.Optional<Player> optional = event.getPlayer();
        if (optional.isPresent()) {
            final Player player = (Player)optional.get();
            final long remaining = this.getRemaining(player);
            if (remaining > 0L) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "Tu ne peux pas rejoindre de faction tant que ton Combat timer est actif.");
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onFactionLeave(final PlayerLeaveFactionEvent event) {
        final com.google.common.base.Optional<Player> optional = event.getPlayer();
        if (optional.isPresent()) {
            final Player player = (Player)optional.get();
            if (this.getRemaining(player) > 0L) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "Tu ne peux pas quitter ta faction tant que ton Combat timer est actif");
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPreventClaimEnter(final PlayerClaimEnterEvent event) {
        if (event.getEnterCause() == PlayerClaimEnterEvent.EnterCause.TELEPORT) {
            return;
        }
        final Player player = event.getPlayer();
        if (!event.getFromFaction().isSafezone() && event.getToFaction().isSafezone() && this.getRemaining(player) > 0L) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Tu ne peux pas entrer dans le territoire de " + event.getToFaction().getDisplayName((CommandSender)player) + ChatColor.RED + " tant que ton Combat timer est actif.");
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
        final Player attacker = BukkitUtils.getFinalAttacker((EntityDamageEvent)event, true);
        final Entity entity;
        if (attacker != null && (entity = event.getEntity()) instanceof Player) {
            final Player attacked = (Player)entity;
            boolean weapon = event.getDamager() instanceof Arrow;
            if (!weapon) {
                final ItemStack stack = attacker.getItemInHand();
                weapon = (stack != null && EnchantmentTarget.WEAPON.includes(stack));
            }
            final long duration = weapon ? this.defaultCooldown : 5000L;
            this.setCooldown(attacked, attacked.getUniqueId(), Math.max(this.getRemaining(attacked), duration), true);
            this.setCooldown(attacker, attacker.getUniqueId(), Math.max(this.getRemaining(attacker), duration), true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onTimerStart(final TimerStartEvent event) {
        if (event.getTimer().equals(this)) {
            final com.google.common.base.Optional<Player> optional = event.getPlayer();
            if (optional.isPresent()) {
                final Player player = (Player)optional.get();
                player.sendMessage(ChatColor.YELLOW + "Tu es désormais sous " + "Combat timer" + ChatColor.YELLOW + " pendant " + ChatColor.RED + DurationFormatUtils.formatDurationWords(event.getDuration(), true, true) + ChatColor.YELLOW + '.');
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerRespawn(final PlayerRespawnEvent event) {
        this.clearCooldown(event.getPlayer().getUniqueId());
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPreventClaimEnterMonitor(final PlayerClaimEnterEvent event) {
        if (event.getEnterCause() == PlayerClaimEnterEvent.EnterCause.TELEPORT && !event.getFromFaction().isSafezone() && event.getToFaction().isSafezone()) {
            this.clearCooldown(event.getPlayer());
        }
    }
}
