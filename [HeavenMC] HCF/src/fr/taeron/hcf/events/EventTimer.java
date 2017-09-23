package fr.taeron.hcf.events;

import fr.taeron.hcf.timer.*;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;

import org.bukkit.scheduler.*;
import fr.taeron.hcf.*;
import fr.taeron.hcf.crate.Key;
import fr.taeron.hcf.events.factions.*;

import java.time.*;
import org.bukkit.command.*;
import org.bukkit.plugin.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import fr.taeron.hcf.faction.type.*;
import fr.taeron.hcf.listeners.EventSignListener;

import org.bukkit.*;

import com.google.common.collect.*;
import java.util.*;
import com.google.common.base.*;
import com.google.common.base.Objects;

import org.bukkit.event.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import fr.taeron.hcf.faction.event.*;
import java.util.concurrent.*;

public class EventTimer extends GlobalTimer implements Listener
{
    private static final long RESCHEDULE_FREEZE_MILLIS;
    private static final String RESCHEDULE_FREEZE_WORDS;
    private final HCF plugin;
    private long startStamp;
    private long lastContestedEventMillis;
    private EventFaction eventFaction;
    
    public EventTimer(final HCF plugin) {
        super("Event", 0L);
        this.plugin = plugin;
        new BukkitRunnable() {
            @SuppressWarnings("deprecation")
			public void run() {
                if (EventTimer.this.eventFaction != null) {
                    EventTimer.this.eventFaction.getEventType().getEventTracker().tick(EventTimer.this, EventTimer.this.eventFaction);
                    return;
                }
                final LocalDateTime now = LocalDateTime.now(DateTimeFormats.SERVER_ZONE_ID);
                final int day = now.getDayOfYear();
                final int hour = now.getHour();
                final int minute = now.getMinute();
                for (final Map.Entry<LocalDateTime, String> entry : plugin.eventScheduler.getScheduleMap().entrySet()) {
                    final LocalDateTime scheduledTime = entry.getKey();
                    if (day == scheduledTime.getDayOfYear() && hour == scheduledTime.getHour()) {
                        if (minute != scheduledTime.getMinute()) {
                            continue;
                        }
                        final Faction faction = plugin.getFactionManager().getFaction(entry.getValue());
                        if (faction instanceof EventFaction && EventTimer.this.tryContesting((EventFaction)faction, (CommandSender)Bukkit.getConsoleSender())) {
                            break;
                        }
                        continue;
                    }
                }
            }
        }.runTaskTimer((Plugin)plugin, 20L, 20L);
    }
    
    public EventFaction getEventFaction() {
        return this.eventFaction;
    }
    
    public String getScoreboardPrefix() {
    	if(this.getName().contains("Citadel")){
            return ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD;
    	}
        return ChatColor.BLUE.toString() + ChatColor.BOLD;
    }
    
    public String getName() {
        return (this.eventFaction == null) ? "Event" : this.eventFaction.getName();
    }
    
    @SuppressWarnings("deprecation")
	@Override
    public boolean clearCooldown() {
        boolean result = super.clearCooldown();
        if (this.eventFaction != null) {
            for (final CaptureZone captureZone : this.eventFaction.getCaptureZones()) {
                captureZone.setCappingPlayer(null);
            }
            this.eventFaction.setDeathban(true);
            this.eventFaction.getEventType().getEventTracker().stopTiming();
            this.eventFaction = null;
            this.startStamp = -1L;
            result = true;
        }
        return result;
    }
    
    @Override
    public long getRemaining() {
        if (this.eventFaction == null) {
            return 0L;
        }
        if (this.eventFaction instanceof KothFaction) {
            return ((KothFaction)this.eventFaction).getCaptureZone().getRemainingCaptureMillis();
        }
        return super.getRemaining();
    }
    
    @SuppressWarnings("deprecation")
	public void handleWinner(final Player winner) {
        if (this.eventFaction == null) {
            return;
        }
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(winner);
        Bukkit.broadcastMessage(ChatColor.YELLOW + "[" + this.eventFaction.getEventType().getDisplayName() + "] " + ChatColor.LIGHT_PURPLE + ((playerFaction == null) ? winner.getName() : playerFaction.getName()) + ChatColor.GOLD + " a capturé " + ChatColor.LIGHT_PURPLE + this.eventFaction.getName() + ChatColor.GOLD + " au bout de " + ChatColor.YELLOW + DurationFormatUtils.formatDurationWords(this.getUptime(), true, true) + ChatColor.GOLD + " !");
        final World world = winner.getWorld();
        final Location location = winner.getLocation();
        final Key key = this.plugin.getKeyManager().getKey(ChatColor.stripColor(this.eventFaction.getEventType().getDisplayName()));
        Preconditions.checkNotNull((Object)key, (Object)"Key on: EventTime error.");
        final ItemStack stack = key.getItemStack().clone();
        final Map<Integer, ItemStack> excess = (Map<Integer, ItemStack>)winner.getInventory().addItem(new ItemStack[] { stack, EventSignListener.getEventSign(this.eventFaction.getName(), winner.getName()) });
        for (final ItemStack entry : excess.values()) {
            world.dropItemNaturally(location, entry);
        }
        this.clearCooldown();
    }
    
    @SuppressWarnings({ "unchecked", "deprecation", "rawtypes" })
	public boolean tryContesting(final EventFaction eventFaction, final CommandSender sender) {
        if (this.eventFaction != null) {
            sender.sendMessage(ChatColor.RED + "Il y a déjà un event en cours, fait /event cancel pour l'annuler.");
            return false;
        }
        if (eventFaction instanceof KothFaction) {
            final KothFaction kothFaction = (KothFaction)eventFaction;
            if (kothFaction.getCaptureZone() == null) {
                sender.sendMessage(ChatColor.RED + "Impossible de définir un schedule pour " + eventFaction.getName() + " car sa zone de capture n'est pas définie.");
                return false;
            }
        }
        else if (eventFaction instanceof ConquestFaction) {
            final ConquestFaction conquestFaction = (ConquestFaction)eventFaction;
            final Collection<ConquestFaction.ConquestZone> zones = conquestFaction.getConquestZones();
            for (final ConquestFaction.ConquestZone zone : ConquestFaction.ConquestZone.values()) {
                if (!zones.contains(zone)) {
                    sender.sendMessage(ChatColor.RED + "Impossible de définir un schedule pour " + eventFaction.getName() + " car la zone de capture '" + zone.getDisplayName() + ChatColor.RED + "' n'est pas définie.");
                    return false;
                }
            }
        }
        final long millis = System.currentTimeMillis();
        if (this.lastContestedEventMillis + EventTimer.RESCHEDULE_FREEZE_MILLIS - millis > 0L) {
            sender.sendMessage(ChatColor.RED + "Impossible de refaire un schedule avant " + EventTimer.RESCHEDULE_FREEZE_WORDS + '.');
            return false;
        }
        this.lastContestedEventMillis = millis;
        this.startStamp = millis;
        this.eventFaction = eventFaction;
        eventFaction.getEventType().getEventTracker().onContest(eventFaction, this);
        if (eventFaction instanceof ConquestFaction) {
            this.setRemaining(1000L, true);
            this.setPaused(true);
        }
        final Collection<CaptureZone> captureZones = eventFaction.getCaptureZones();
        for (final CaptureZone captureZone : captureZones) {
            if (captureZone.isActive()) {
                final Player player = (Player)Iterables.getFirst((Iterable)captureZone.getCuboid().getPlayers(), (Object)null);
                if (player == null) {
                    continue;
                }
                if (!eventFaction.getEventType().getEventTracker().onControlTake(player, captureZone)) {
                    continue;
                }
                captureZone.setCappingPlayer(player);
            }
        }
        eventFaction.setDeathban(true);
        return true;
    }
    
    public long getUptime() {
        return System.currentTimeMillis() - this.startStamp;
    }
    
    public long getStartStamp() {
        return this.startStamp;
    }
    
    @SuppressWarnings("deprecation")
	private void handleDisconnect(final Player player) {
        Preconditions.checkNotNull((Object)player);
        if (this.eventFaction == null) {
            return;
        }
        final Collection<CaptureZone> captureZones = this.eventFaction.getCaptureZones();
        for (final CaptureZone captureZone : captureZones) {
            if (Objects.equal((Object)captureZone.getCappingPlayer(), (Object)player)) {
                this.eventFaction.getEventType().getEventTracker().onControlLoss(player, captureZone, this.eventFaction);
                captureZone.setCappingPlayer(null);
                break;
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        this.handleDisconnect(event.getEntity());
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerLogout(final PlayerQuitEvent event) {
        this.handleDisconnect(event.getPlayer());
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerKick(final PlayerKickEvent event) {
        this.handleDisconnect(event.getPlayer());
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onCaptureZoneEnter(final CaptureZoneEnterEvent event) {
        if (this.eventFaction == null) {
            return;
        }
        final CaptureZone captureZone = event.getCaptureZone();
        if (!this.eventFaction.getCaptureZones().contains(captureZone)) {
            return;
        }
        final Player player = event.getPlayer();
        if (captureZone.getCappingPlayer() == null && this.eventFaction.getEventType().getEventTracker().onControlTake(player, captureZone)) {
            captureZone.setCappingPlayer(player);
        }
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onCaptureZoneLeave(final CaptureZoneLeaveEvent event) {
        if (Objects.equal((Object)event.getFaction(), (Object)this.eventFaction)) {
            final Player player = event.getPlayer();
            final CaptureZone captureZone = event.getCaptureZone();
            if (Objects.equal((Object)player, (Object)captureZone.getCappingPlayer()) && this.eventFaction.getEventType().getEventTracker().onControlLoss(player, captureZone, this.eventFaction)) {
                captureZone.setCappingPlayer(null);
                for (final Player target : captureZone.getCuboid().getPlayers()) {
                    if (target != null && !target.equals(player) && this.eventFaction.getEventType().getEventTracker().onControlTake(target, captureZone)) {
                        captureZone.setCappingPlayer(target);
                        break;
                    }
                }
            }
        }
    }
    
    static {
        RESCHEDULE_FREEZE_MILLIS = TimeUnit.SECONDS.toMillis(15L);
        RESCHEDULE_FREEZE_WORDS = DurationFormatUtils.formatDurationWords(EventTimer.RESCHEDULE_FREEZE_MILLIS, true, true);
    }
}
