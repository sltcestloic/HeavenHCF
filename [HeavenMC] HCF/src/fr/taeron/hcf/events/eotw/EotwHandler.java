package fr.taeron.hcf.events.eotw;

import fr.taeron.hcf.*;
import org.bukkit.plugin.*;
import java.util.concurrent.*;
import org.bukkit.scheduler.*;
import org.bukkit.entity.*;
import fr.taeron.hcf.faction.type.*;
import fr.taeron.hcf.listeners.*;

import org.bukkit.command.*;
import org.bukkit.*;

import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;

import java.util.*;
import org.bukkit.potion.*;

public class EotwHandler
{
    public static final int BORDER_DECREASE_MINIMUM = 1000;
    public static final int BORDER_DECREASE_AMOUNT = 200;
    public static final long BORDER_DECREASE_TIME_MILLIS;
    public static final int BORDER_DECREASE_TIME_SECONDS;
    public static final String BORDER_DECREASE_TIME_WORDS;
    public static final String BORDER_DECREASE_TIME_ALERT_WORDS;
    public static final long EOTW_WARMUP_WAIT_MILLIS;
    public static final int EOTW_WARMUP_WAIT_SECONDS;
    private static final long EOTW_CAPPABLE_WAIT;
    private final HCF plugin;
    private EotwRunnable runnable;
    
    public EotwHandler(final HCF plugin) {
        this.plugin = plugin;
    }
    
    public EotwRunnable getRunnable() {
        return this.runnable;
    }
    
    public boolean isEndOfTheWorld() {
        return this.isEndOfTheWorld(true);
    }
    
    public void setEndOfTheWorld(final boolean yes) {
        if (yes == this.isEndOfTheWorld(false)) {
            return;
        }
        if (yes) {
            (this.runnable = new EotwRunnable(ConfigurationService.BORDER_SIZES.get(World.Environment.NORMAL))).runTaskTimer((Plugin)this.plugin, 1L, 100L);
        }
        else if (this.runnable != null) {
            this.runnable.cancel();
            this.runnable = null;
        }
    }
    
    public boolean isEndOfTheWorld(final boolean ignoreWarmup) {
        return this.runnable != null && (!ignoreWarmup || this.runnable.getElapsedMilliseconds() > 0L);
    }
    
    static {
        BORDER_DECREASE_TIME_MILLIS = TimeUnit.MINUTES.toMillis(5L);
        BORDER_DECREASE_TIME_SECONDS = (int)(EotwHandler.BORDER_DECREASE_TIME_MILLIS / 1000L);
        BORDER_DECREASE_TIME_WORDS = DurationFormatUtils.formatDurationWords(EotwHandler.BORDER_DECREASE_TIME_MILLIS, true, true);
        BORDER_DECREASE_TIME_ALERT_WORDS = DurationFormatUtils.formatDurationWords(EotwHandler.BORDER_DECREASE_TIME_MILLIS / 2L, true, true);
        EOTW_WARMUP_WAIT_MILLIS = TimeUnit.SECONDS.toMillis(30L);
        EOTW_WARMUP_WAIT_SECONDS = (int)(EotwHandler.EOTW_WARMUP_WAIT_MILLIS / 1000L);
        EOTW_CAPPABLE_WAIT = TimeUnit.MINUTES.toMillis(30L);
    }
    
    public static final class EotwRunnable extends BukkitRunnable
    {
        private static final PotionEffect WITHER;
        private final Set<Player> outsideBorder;
        private boolean hasInformedStarted;
        private long startStamp;
        private int borderSize;
        
        public EotwRunnable(final int borderSize) {
            this.outsideBorder = new HashSet<Player>();
            this.hasInformedStarted = false;
            this.borderSize = borderSize;
            this.startStamp = System.currentTimeMillis() + EotwHandler.EOTW_WARMUP_WAIT_MILLIS;
        }
        
        public void handleDisconnect(final Player player) {
            this.outsideBorder.remove(player);
        }
        
        public long getTimeUntilStarting() {
            final long difference = System.currentTimeMillis() - this.startStamp;
            return (difference > 0L) ? 0L : Math.abs(difference);
        }
        
        public long getTimeUntilCappable() {
            return EotwHandler.EOTW_CAPPABLE_WAIT - this.getElapsedMilliseconds();
        }
        
        public long getElapsedMilliseconds() {
            return System.currentTimeMillis() - this.startStamp;
        }
        
		public void run() {
            final long elapsedMillis = this.getElapsedMilliseconds();
            final int elapsedSeconds = (int)Math.round(elapsedMillis / 1000.0);
            if (!this.hasInformedStarted && elapsedSeconds >= 0) {
                for (final Faction faction : HCF.getPlugin().getFactionManager().getFactions()) {
                    if (faction instanceof ClaimableFaction) {
                        final ClaimableFaction claimableFaction = (ClaimableFaction)faction;
                        claimableFaction.removeClaims(claimableFaction.getClaims(), (CommandSender)Bukkit.getConsoleSender());
                    }
                }
                this.hasInformedStarted = true;
                Bukkit.broadcastMessage("§6L'End Of The World §7 a commencé, la bordure va réduire de §e200 §7blocs toutes les §6" + EotwHandler.BORDER_DECREASE_TIME_WORDS + " §7jusqu'à atteindre une taille de §e1000 x 1000");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "f setdtr all -5000");
                return;
            }
            if (elapsedMillis < 0L && elapsedMillis >= -EotwHandler.EOTW_WARMUP_WAIT_MILLIS) {
                Bukkit.broadcastMessage("§6L'End Of The World §7commence dans §e" + HCF.getRemaining(Math.abs(elapsedMillis), true, false).replace("seconds", "secondes"));
                return;
            }
            final Iterator<Player> iterator = this.outsideBorder.iterator();
            while (iterator.hasNext()) {
                final Player player = iterator.next();
                if (BorderListener.isWithinBorder(player.getLocation())) {
                    iterator.remove();
                }
                else {
                    player.sendMessage(ChatColor.RED + "Tu es en dehors de la bordure de l'EOTW, tu as donc reçu un effet de wither.");
                    player.addPotionEffect(EotwRunnable.WITHER, true);
                }
            }
            if(this.borderSize <= EotwHandler.BORDER_DECREASE_MINIMUM){
            	return;
            }
            final int newBorderSize = this.borderSize - 200;
            if (elapsedSeconds % EotwHandler.BORDER_DECREASE_TIME_SECONDS == 0) {
                final Map<World.Environment, Integer> border_SIZES = ConfigurationService.BORDER_SIZES;
                final World.Environment normal = World.Environment.NORMAL;
                final int borderSize = newBorderSize;
                this.borderSize = borderSize;
                border_SIZES.put(normal, borderSize);
                Bukkit.broadcastMessage(ChatColor.GRAY + "La bordure est désormais de " + ChatColor.YELLOW + newBorderSize + ChatColor.GRAY + " blocs.");
                for (final Player player2 : Bukkit.getOnlinePlayers()) {
                    if (!BorderListener.isWithinBorder(player2.getLocation())) {
                        this.outsideBorder.add(player2);
                    }
                }
            }
            else if (elapsedSeconds % (EotwHandler.BORDER_DECREASE_TIME_SECONDS / 2) == 0 && this.borderSize > EotwHandler.BORDER_DECREASE_MINIMUM) {
                Bukkit.broadcastMessage(ChatColor.GRAY + "La bordure sera de " + ChatColor.YELLOW + newBorderSize + ChatColor.GRAY + " blocs dans " + ChatColor.YELLOW + EotwHandler.BORDER_DECREASE_TIME_ALERT_WORDS.replace("seconds", "secondes") + ChatColor.GRAY + '.');
            }
        }
        
        static {
            WITHER = new PotionEffect(PotionEffectType.WITHER, 200, 0);
        }
    }
}
