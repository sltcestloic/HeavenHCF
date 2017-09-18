package fr.taeron.hcf.timer.type;

import fr.taeron.hcf.timer.*;
import fr.taeron.hcf.*;
import java.util.concurrent.*;
import fr.taeron.hcf.timer.event.*;
import org.bukkit.*;
import fr.taeron.hcf.pvpclass.archer.*;

import java.util.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.entity.*;

public class ArcherTimer extends PlayerTimer implements Listener
{
    
    public String getScoreboardPrefix() {
        return ChatColor.YELLOW.toString();
    }
    
    public ArcherTimer(final HCF plugin) {
        super("Archer Tag", TimeUnit.SECONDS.toMillis(8L));
    }
    
    public void run() {
    }
    
	@EventHandler
    public void onExpire(final TimerExpireEvent e) {
        if (e.getUserUUID().isPresent() && e.getTimer().equals(this)) {
            final UUID userUUID = (UUID)e.getUserUUID().get();
            final Player player = Bukkit.getPlayer(userUUID);
            if (player == null) {
                return;
            }
            if(Bukkit.getPlayer((UUID)ArcherClass.tagged.get(userUUID)) != null){
            	Bukkit.getPlayer((UUID)ArcherClass.tagged.get(userUUID)).sendMessage(ChatColor.YELLOW + "Ton archer tag sur " + ChatColor.RED + player.getName() + ChatColor.YELLOW + " a expiré.");
            }
            player.sendMessage(ChatColor.YELLOW + "Tu n'es plus sous archer tag.");
            ArcherClass.tagged.remove(player.getUniqueId());
        }
    }
    
    @SuppressWarnings({ "unused", "deprecation" })
	@EventHandler
    public void onHit(final EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            final Player entity = (Player)e.getEntity();
            final Entity damager = e.getDamager();
            if (this.getRemaining(entity) > 0L) {
                final Double damage = e.getDamage() * 0.25;
                e.setDamage(e.getDamage() + damage);
            }
        }
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Arrow) {
            final Player entity = (Player)e.getEntity();
            final Entity damager = (Entity)((Arrow)e.getDamager()).getShooter();
            if (damager instanceof Player) {
                //if (ArcherClass.tagged.get(entity.getUniqueId()).equals(damager.getUniqueId())) {
            	this.setCooldown(entity, entity.getUniqueId(), this.defaultCooldown, true);
                //}
                final Double damage = e.getDamage() * 0.25;
                e.setDamage(e.getDamage() + damage);
            }
        }
    }
}
