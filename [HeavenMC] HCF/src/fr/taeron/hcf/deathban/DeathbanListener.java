package fr.taeron.hcf.deathban;

import fr.taeron.hcf.*;

import java.util.concurrent.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.event.player.*;
import org.bukkit.plugin.*;
import org.bukkit.entity.*;
import fr.taeron.hcf.user.*;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;

import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.scheduler.*;
public class DeathbanListener implements Listener{
	
    private static final long LIFE_USE_DELAY_MILLIS;
    private static final String LIFE_USE_DELAY_WORDS;
    static final String DEATH_BAN_BYPASS_PERMISSION = "deathban.bypass";
    private HashMap<UUID, Long> lastAttemptedJoin = new HashMap<UUID, Long>();
    private final HCF plugin;
    
    static {
        LIFE_USE_DELAY_MILLIS = TimeUnit.SECONDS.toMillis(30L);
        LIFE_USE_DELAY_WORDS = DurationFormatUtils.formatDurationWords(DeathbanListener.LIFE_USE_DELAY_MILLIS, true, true);
    }
    
    public DeathbanListener(final HCF plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerLogin(final PlayerLoginEvent event) {
        final Player player = event.getPlayer();
        final FactionUser user = this.plugin.getUserManager().getUser(player.getUniqueId());
        final Deathban deathban = user.getDeathban();
        if (deathban == null || !deathban.isActive()) {
            return;
        }
        if (player.hasPermission("hcf.deathban.bypass")) {
            new LoginMessageRunnable(player, ChatColor.RED + "Tu devrais être death-ban pour " + deathban.getReason() + ChatColor.RED + ", mais tu as la permission de bypass.").runTask((Plugin)this.plugin);
            return;
        }
        if (this.plugin.getEotwHandler().isEndOfTheWorld()) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "Tu es deathban jusqu'a la fin de la map car tu es mort pendant l'EOTW.\nReviens pour le prochain SOTW !");
        }
        else {
            final UUID uuid = player.getUniqueId();
            int lives = this.plugin.getDeathbanManager().getLives(uuid);
            final String formattedDuration = HCF.getRemaining(deathban.getRemaining(), true, false);
            final String reason = deathban.getReason();
            final String prefix = ChatColor.RED + "Tu es actuellement deathban " + ((reason != null) ? (" pour " + reason + ".\n") : ".") + ChatColor.YELLOW + formattedDuration + " restantes.\n" + ChatColor.RED + "Tu " + ((lives <= 0) ? "n'as pas de vies." : "a actuellement " + lives + " vies.");
            if (lives > 0) {
                final long millis = System.currentTimeMillis();
                final Long lastAttemptedJoinMillis = (Long) this.lastAttemptedJoin.get(uuid);
                if (lastAttemptedJoinMillis != null && lastAttemptedJoinMillis - System.currentTimeMillis() < DeathbanListener.LIFE_USE_DELAY_MILLIS) {
                    this.lastAttemptedJoin.remove(uuid);
                    user.removeDeathban();
                    lives = this.plugin.getDeathbanManager().takeLives(uuid, 1);
                    event.setResult(PlayerLoginEvent.Result.ALLOWED);
                    new LoginMessageRunnable(player, ChatColor.YELLOW + "Tu as utilisé une vie pour bypass ton deathban. Tu as désormais " + ChatColor.GOLD + lives + ChatColor.YELLOW + " vies.").runTask((Plugin)this.plugin);
                }
                else {
                    this.lastAttemptedJoin.put(uuid, millis + DeathbanListener.LIFE_USE_DELAY_MILLIS);
                    event.disallow(PlayerLoginEvent.Result.KICK_OTHER, String.valueOf(prefix) + ChatColor.GOLD + "\n\n" + "Tu peux utiliser une vie en te reconnectant dans les " + ChatColor.YELLOW + DeathbanListener.LIFE_USE_DELAY_WORDS + ChatColor.GOLD + " à venir.");
                }
                return;
            }
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "Tu est encore deathban pendant " + formattedDuration + ": " + ChatColor.YELLOW + deathban.getReason() + ChatColor.RED + '.' + "\nTu peux acheter une vie sur " + ConfigurationService.DONATE_URL + " pour bypass le deathban.");
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final Deathban deathban = this.plugin.getDeathbanManager().applyDeathBan(player, event.getDeathMessage());
        HCF.getPlugin().getUserManager().getUser(player.getUniqueId()).setDeaths(HCF.getPlugin().getUserManager().getUser(player.getUniqueId()).getDeaths() + 1);
        final String durationString = HCF.getRemaining(deathban.getRemaining(), true, false);
        new BukkitRunnable() {
            public void run() {
                if (DeathbanListener.this.plugin.getEotwHandler().isEndOfTheWorld()) {
                    player.kickPlayer(ChatColor.RED + "Tu es deathban jusqu'à la fin de la map car tu es mort durant l'EOTW.\nReviens au prochain SOTW!");
                }
                else {
                    player.kickPlayer(ChatColor.RED + "Tu es deathban pendant " + durationString + ": " + ChatColor.YELLOW + deathban.getReason());
                }
            }
        }.runTaskLater((Plugin)this.plugin, 1L);
    }
    
    //KITMAP
    
	/*@EventHandler
    public void disableDTRChange(PlayerDeathEvent e){
    	Player p = e.getEntity();
    	PlayerFaction f = HCF.getPlugin().getFactionManager().getPlayerFaction(p.getUniqueId());
    	if(f != null){
    		f.setDeathsUntilRaidable(f.getMaximumDeathsUntilRaidable());
    	}
    }*/
    
    private static class LoginMessageRunnable extends BukkitRunnable
    {
        private final Player player;
        private final String message;
        
        public LoginMessageRunnable(final Player player, final String message) {
            this.player = player;
            this.message = message;
        }
        
        public void run() {
            this.player.sendMessage(this.message);
        }
    }
}
