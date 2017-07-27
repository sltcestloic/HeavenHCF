package fr.taeron.hcf.listener;

import net.minecraft.util.gnu.trove.map.*;
import java.util.*;
import fr.taeron.hcf.*;
import net.minecraft.util.gnu.trove.map.hash.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.potion.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import fr.taeron.hcf.timer.*;

public class PortalListener implements Listener
{
    private final Location endExit;
    private final TObjectLongMap<UUID> messageDelays;
    private final HCF plugin;
    private HashMap<Player, Long> endExitTime;
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public PortalListener(final HCF plugin) {
        this.endExit = new Location(Bukkit.getWorld("world"), 0.0, 77, 300.0);
        this.messageDelays = (TObjectLongMap<UUID>)new TObjectLongHashMap();
        this.plugin = plugin;
        this.endExitTime = new HashMap<Player, Long>();
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onEntityPortal(final EntityPortalEvent event) {
        if (event.getEntity() instanceof EnderDragon) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerPortal(final PlayerPortalEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.END_PORTAL) {
            return;
        }
        final World toWorld = event.getTo().getWorld();
        if (toWorld != null && toWorld.getEnvironment() == World.Environment.THE_END) {
            event.useTravelAgent(false);
            event.setTo(toWorld.getSpawnLocation());
            return;
        }
        final World fromWorld = event.getFrom().getWorld();
        if (fromWorld != null && fromWorld.getEnvironment() == World.Environment.THE_END) {
            event.useTravelAgent(false);
            event.setTo(this.endExit);
            this.endExitTime.put(event.getPlayer(), System.currentTimeMillis());
            event.getPlayer().sendMessage("§9Tu es sorti de l'end, tu es désormais imunnisé contre les dégâts de chutes et de projectiles pendant 5 secondes.");
        }
    }
    
    @EventHandler
    public void cancelFall(EntityDamageEvent e){
    	if(e.getCause() != DamageCause.FALL && e.getCause() != DamageCause.POISON && e.getCause() != DamageCause.PROJECTILE){
    		return;
    	}
    	if(!(e.getEntity() instanceof Player)){
    		return;
    	}
    	Player p = (Player) e.getEntity();
    	if(this.endExitTime.containsKey(p)){
    		long elapsed = System.currentTimeMillis() - this.endExitTime.get(p);
    		if(elapsed < 5000L){
    			e.setCancelled(true);
    		}
    	}
    }
    
    @EventHandler
    public void cancelSplash(PotionSplashEvent e){
    	for(Entity ent : e.getAffectedEntities()){
    		if(ent instanceof Player){
    			Player p = (Player) ent;
    			if(this.endExitTime.containsKey(p)){
    	    		long elapsed = System.currentTimeMillis() - this.endExitTime.get(p);
    	    		if(elapsed < 5000L){
    	    			for(PotionEffect ef : e.getPotion().getEffects()){
    	    				if(ef.getType() == PotionEffectType.SLOW){
    	    	    			e.setIntensity(p, 0);
    	    				}
    	    			}
    	    		}
    	    	}
    		}
    	}
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onWorldChanged(final PlayerChangedWorldEvent event) {
        final Player player = event.getPlayer();
        final World from = event.getFrom();
        final World to = player.getWorld();
        if (from.getEnvironment() != World.Environment.THE_END && to.getEnvironment() == World.Environment.THE_END && player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
            player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPortalEnter(final PlayerPortalEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.END_PORTAL && event.getCause() != TeleportCause.NETHER_PORTAL) {
            return;
        }
        final Location to = event.getTo();
        final World toWorld = to.getWorld();
        if (toWorld == null) {
            return;
        }
        if (toWorld.getEnvironment() == World.Environment.THE_END) {
            final Player player = event.getPlayer();
            PlayerTimer timer = this.plugin.getTimerManager().spawnTagTimer;
            long remaining;
            if ((remaining = timer.getRemaining(player)) > 0L) {
                this.message(player, ChatColor.RED + "Tu ne peux pas entrer dans l'end car ton " + timer.getDisplayName() + ChatColor.RED + " timer est actif [" + ChatColor.BOLD + HCF.getRemaining(remaining, true, false) + ChatColor.RED + " restant]");
                event.setCancelled(true);
                return;
            }
            timer = this.plugin.getTimerManager().pvpProtectionTimer;
            if ((remaining = timer.getRemaining(player)) > 0L) {
                this.message(player, ChatColor.RED + "Tu ne peux pas entrer dans l'end car ton " + timer.getDisplayName() + ChatColor.RED + " timer est actif [" + ChatColor.BOLD + HCF.getRemaining(remaining, true, false) + ChatColor.RED + " restant]");
                event.setCancelled(true);
                return;
            }
            event.useTravelAgent(false);
            event.setTo(toWorld.getSpawnLocation().add(0.5, 0.0, 0.5));
        }
        if (toWorld.getEnvironment() == World.Environment.NETHER) {
            final Player player = event.getPlayer();
            PlayerTimer timer = this.plugin.getTimerManager().spawnTagTimer;
            long remaining;
            if ((remaining = timer.getRemaining(player)) > 0L) {
                this.message(player, ChatColor.RED + "Tu ne peux pas entrer dans le nether car ton " + timer.getDisplayName() + ChatColor.RED + " timer est actif [" + ChatColor.BOLD + HCF.getRemaining(remaining, true, false) + ChatColor.RED + " restant]");
                event.setCancelled(true);
                return;
            }
            timer = this.plugin.getTimerManager().pvpProtectionTimer;
            if ((remaining = timer.getRemaining(player)) > 0L) {
                this.message(player, ChatColor.RED + "Tu ne peux pas entrer dans le nether car ton " + timer.getDisplayName() + ChatColor.RED + " timer est actif [" + ChatColor.BOLD + HCF.getRemaining(remaining, true, false) + ChatColor.RED + " restant]");
                event.setCancelled(true);
                return;
            }
            event.useTravelAgent(false);
            event.setTo(new Location(toWorld, 0, 34, 300));
        }
    }
    
    private void message(final Player player, final String message) {
        final long last = this.messageDelays.get((Object)player.getUniqueId());
        final long millis = System.currentTimeMillis();
        if (last != this.messageDelays.getNoEntryValue() && last + 2500L - millis > 0L) {
            return;
        }
        this.messageDelays.put(player.getUniqueId(), millis);
        player.sendMessage(message);
    }
}
