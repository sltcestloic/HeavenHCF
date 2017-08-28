package fr.taeron.hcf.events.trackers;

import fr.taeron.hcf.*;
import org.bukkit.*;

import fr.taeron.hcf.events.*;
import fr.taeron.hcf.events.factions.*;

import org.bukkit.entity.*;
import java.util.concurrent.*;

@Deprecated
public class KothTracker implements EventTracker
{
    public static final long DEFAULT_CAP_MILLIS;
    private static final long MINIMUM_CONTROL_TIME_ANNOUNCE;
    private final HCF plugin;
    
    public KothTracker(final HCF plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public EventType getEventType() {
        return EventType.KOTH;
    }
    
    @Override
    public void tick(final EventTimer eventTimer, final EventFaction eventFaction) {
        final CaptureZone captureZone = ((KothFaction)eventFaction).getCaptureZone();
        final long remainingMillis = captureZone.getRemainingCaptureMillis();
        if (remainingMillis <= 0L) {
            this.plugin.getTimerManager().eventTimer.handleWinner(captureZone.getCappingPlayer());
            eventTimer.clearCooldown();
            return;
        }
        if (remainingMillis == captureZone.getDefaultCaptureMillis()) {
            return;
        }
        final int remainingSeconds = (int)(remainingMillis / 1000L);
        if (remainingSeconds > 0 && remainingSeconds % 30 == 0) {
            Bukkit.broadcastMessage(ChatColor.YELLOW + "[" + eventFaction.getEventType().getDisplayName() + "] " + ChatColor.GOLD + "Quelqu'un controle " + ChatColor.LIGHT_PURPLE + captureZone.getDisplayName() + ChatColor.GOLD + ". " + ChatColor.RED + '(' + DateTimeFormats.KOTH_FORMAT.format(remainingMillis) + ')');
        }
    }
    
    @Override
    public void onContest(final EventFaction eventFaction, final EventTimer eventTimer) {
        Bukkit.broadcastMessage(ChatColor.YELLOW + "[" + eventFaction.getEventType().getDisplayName() + "] " + ChatColor.LIGHT_PURPLE + eventFaction.getName() + ChatColor.GOLD + " peut désormais être capturé. " + ChatColor.RED + '(' + DateTimeFormats.KOTH_FORMAT.format(eventTimer.getRemaining()) + ')');
    }
    
    @Override
    public boolean onControlTake(final Player player, final CaptureZone captureZone) {
        player.sendMessage(ChatColor.GOLD + "Tu controles désormais " + ChatColor.LIGHT_PURPLE + captureZone.getDisplayName() + ChatColor.GOLD + '.');
        return true;
    }
    
    @Override
    public boolean onControlLoss(final Player player, final CaptureZone captureZone, final EventFaction eventFaction) {
        player.sendMessage(ChatColor.GOLD + "Tu ne controles plus " + ChatColor.LIGHT_PURPLE + captureZone.getDisplayName() + ChatColor.GOLD + '.');
        final long remainingMillis = captureZone.getRemainingCaptureMillis();
        if (remainingMillis > 0L && captureZone.getDefaultCaptureMillis() - remainingMillis > KothTracker.MINIMUM_CONTROL_TIME_ANNOUNCE) {
            Bukkit.broadcastMessage(ChatColor.YELLOW + "[" + eventFaction.getEventType().getDisplayName() + "] " + ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.GOLD + " a perdu le controle de " + ChatColor.LIGHT_PURPLE + captureZone.getDisplayName() + ChatColor.GOLD + '.' + ChatColor.RED + " (" + DateTimeFormats.KOTH_FORMAT.format(captureZone.getRemainingCaptureMillis()) + ')');
        }
        return true;
    }
    
    @Override
    public void stopTiming() {
    }
    
    static {
        MINIMUM_CONTROL_TIME_ANNOUNCE = TimeUnit.SECONDS.toMillis(30L);
        DEFAULT_CAP_MILLIS = TimeUnit.MINUTES.toMillis(10L);
    }
}
