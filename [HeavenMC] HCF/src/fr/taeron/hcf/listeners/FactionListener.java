package fr.taeron.hcf.listeners;

import fr.taeron.hcf.*;
import fr.taeron.hcf.events.factions.*;

import org.bukkit.*;
import org.bukkit.ChatColor;

import fr.taeron.hcf.faction.type.*;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;

import org.bukkit.command.*;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.event.*;
import org.bukkit.entity.*;
import org.bukkit.metadata.*;
import com.google.common.base.Optional;
import fr.taeron.hcf.faction.struct.*;
import fr.taeron.hcf.faction.event.*;
import org.bukkit.event.player.*;

import java.util.List;
import java.util.concurrent.*;

public class FactionListener implements Listener
{
    private static final long FACTION_JOIN_WAIT_MILLIS;
    private static final String FACTION_JOIN_WAIT_WORDS;
    private final HCF plugin;
    
    public FactionListener(final HCF plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFactionCreate(final FactionCreateEvent event) {
        final Faction faction = event.getFaction();
        if (faction instanceof PlayerFaction) {
            final CommandSender sender = event.getSender();
            Bukkit.broadcastMessage(ChatColor.RED + "" + event.getFaction().getName() + ChatColor.YELLOW + " a été créée par " + ChatColor.WHITE + "" + sender.getName());
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFactionRemove(final FactionRemoveEvent event) {
        final Faction faction = event.getFaction();
        if (faction instanceof PlayerFaction) {
            final CommandSender sender = event.getSender();
            Bukkit.broadcastMessage(ChatColor.RED + "" + event.getFaction().getName() + ChatColor.YELLOW + " a été disband par " + ChatColor.WHITE + sender.getName());
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFactionRename(final FactionRenameEvent event) {
        final Faction faction = event.getFaction();
        if (faction instanceof PlayerFaction) {
            Bukkit.broadcastMessage(ChatColor.RED + event.getOriginalName() + ChatColor.YELLOW + " a été rename en " + ChatColor.RED + "" + event.getNewName() + ChatColor.YELLOW + " par " + ChatColor.WHITE + event.getSender().getName());
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFactionRenameMonitor(final FactionRenameEvent event) {
        final Faction faction = event.getFaction();
        if (faction instanceof KothFaction) {
            ((KothFaction)faction).getCaptureZone().setName(event.getNewName());
        }
    }
    
    private long getLastLandChangedMeta(final Player player) {
    	List<MetadataValue> value = player.getMetadata("landChangedMessage");
    	long millis = System.currentTimeMillis();
        long remaining = value == null || value.isEmpty() ? 0L : value.get(0).asLong() - millis;
        if (remaining <= 0L) { // update the metadata.
            player.setMetadata("landChangedMessage", new FixedMetadataValue(plugin, millis + 225L));
        }
        return remaining;
    }
        
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onCaptureZoneEnter(final CaptureZoneEnterEvent event) {
        final Player player = event.getPlayer();
        if (this.getLastLandChangedMeta(player) <= 0L && this.plugin.getUserManager().getUser(player.getUniqueId()).isCapzoneEntryAlerts()) {
            player.sendMessage(ChatColor.YELLOW + "Tu entres dans une zone de capture: " + event.getCaptureZone().getDisplayName() + ChatColor.YELLOW + '(' + event.getFaction().getName() + ChatColor.YELLOW + ')');
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onCaptureZoneLeave(final CaptureZoneLeaveEvent event) {
        final Player player = event.getPlayer();
        if (this.getLastLandChangedMeta(player) <= 0L && this.plugin.getUserManager().getUser(player.getUniqueId()).isCapzoneEntryAlerts()) {
            player.sendMessage(ChatColor.YELLOW + "Tu quittes une zone de capture: " + event.getCaptureZone().getDisplayName() + ChatColor.YELLOW + '(' + event.getFaction().getName() + ChatColor.YELLOW + ')');
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void onPlayerClaimEnter(final PlayerClaimEnterEvent event) {
        final Faction toFaction = event.getToFaction();
        if (toFaction.isSafezone()) {
            final Player player = event.getPlayer();
            CraftPlayer p = (CraftPlayer) event.getPlayer();
            player.setHealth(p.getMaxHealth());
            player.setFoodLevel(20);
            player.setFireTicks(0);
            player.setSaturation(4.0f);
        }
        final Player player = event.getPlayer();
        if (this.getLastLandChangedMeta(player) <= 0L) {
            final Faction fromFaction = event.getFromFaction();
            player.sendMessage(fromFaction.getDisplayName((CommandSender)player) + ChatColor.YELLOW + " (" + (fromFaction.isDeathban() ? (ChatColor.RED + "Deathban") : (ChatColor.GREEN + "Non-Deathban")) + ChatColor.YELLOW + ')' + ChatColor.GRAY + " -> " + toFaction.getDisplayName((CommandSender)player) + ChatColor.YELLOW + " (" + (toFaction.isDeathban() ? (ChatColor.RED + "Deathban") : (ChatColor.GREEN + "Non-Deathban")) + ChatColor.YELLOW + ')');
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerLeftFaction(final PlayerLeftFactionEvent event) {
        final java.util.Optional<Player> optionalPlayer = event.getPlayer();
        if (optionalPlayer.isPresent()) {
            this.plugin.getUserManager().getUser(((Player)optionalPlayer.get()).getUniqueId()).setLastFactionLeaveMillis(System.currentTimeMillis());
        }
    }
    
    @SuppressWarnings("rawtypes")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerPreFactionJoin(final PlayerJoinFactionEvent event) {
        final Faction faction = event.getFaction();
        final Optional optionalPlayer = event.getPlayer();
        if (faction instanceof PlayerFaction && optionalPlayer.isPresent()) {
            final Player player = (Player)optionalPlayer.get();
            final PlayerFaction playerFaction = (PlayerFaction)faction;
            if (!this.plugin.getEotwHandler().isEndOfTheWorld() && playerFaction.getRegenStatus() == RegenStatus.PAUSED) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "Tu ne peux pas rejoindre une faction qui est en DTR freeze.");
                return;
            }
            final long difference = this.plugin.getUserManager().getUser(player.getUniqueId()).getLastFactionLeaveMillis() - System.currentTimeMillis() + FactionListener.FACTION_JOIN_WAIT_MILLIS;
            if (difference > 0L && !player.hasPermission("faction.argument.staff.forcejoin")) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "Tu ne peux pas rejoindre de faction actuellement, tu viens de quitter " + FactionListener.FACTION_JOIN_WAIT_WORDS + ". " + "Tu pourra la rejoindre dans " + DurationFormatUtils.formatDurationWords(difference, true, true) + '.');
            }
        }
    }
    
    @SuppressWarnings("rawtypes")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onFactionLeave(final PlayerLeaveFactionEvent event) {
        final Faction faction = event.getFaction();
        if (faction instanceof PlayerFaction) {
            final Optional optional = event.getPlayer();
            if (optional.isPresent()) {
                final Player player = (Player)optional.get();
                if (this.plugin.getFactionManager().getFactionAt(player.getLocation()).equals(faction)) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "Tu dois sortir des claims de ta faction avant de la quitter.");
                }
            }
        }
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction != null) {
            playerFaction.printDetails((CommandSender)player);
            playerFaction.broadcast(ChatColor.GOLD + "Membre de faction connecté: " + ChatColor.GREEN + playerFaction.getMember(player).getRole().getAstrix() + player.getName() + ChatColor.GOLD + '.', player.getUniqueId());
        }
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction != null) {
            playerFaction.broadcast(ChatColor.GOLD + "Membre de faction déconnecté: " + ChatColor.GREEN + playerFaction.getMember(player).getRole().getAstrix() + player.getName() + ChatColor.GOLD + '.');
        }
    }
    
    static {
        FACTION_JOIN_WAIT_MILLIS = TimeUnit.SECONDS.toMillis(30L);
        FACTION_JOIN_WAIT_WORDS = DurationFormatUtils.formatDurationWords(FactionListener.FACTION_JOIN_WAIT_MILLIS, true, true);
    }
}
