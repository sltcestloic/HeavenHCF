package fr.taeron.hcf.listeners;

import fr.taeron.hcf.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.*;
import org.bukkit.craftbukkit.v1_7_R4.entity.*;
import net.minecraft.server.v1_7_R4.*;
import net.minecraft.util.org.apache.commons.lang3.text.WordUtils;

import org.bukkit.*;

import com.google.common.base.*;
import org.bukkit.entity.*;
import org.bukkit.entity.Entity;



public class DeathMessageListener implements Listener{
	
    private final HCF plugin;
    
    public DeathMessageListener(final HCF plugin) {
        this.plugin = plugin;
    }
    
    public static String replaceLast(final String text, final String regex, final String replacement) {
        return text.replaceFirst("(?s)" + regex + "(?!.*?" + regex + ')', replacement);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        final String message = event.getDeathMessage();
        if (message == null || message.isEmpty()) {
            return;
        }
        event.setDeathMessage(this.getDeathMessage(message, (Entity)event.getEntity(), (Entity)this.getKiller(event)));
    }
    
    private CraftEntity getKiller(final PlayerDeathEvent event) {
        final EntityLiving lastAttacker = ((CraftPlayer)event.getEntity()).getHandle().aX();
        return (lastAttacker == null) ? null : lastAttacker.getBukkitEntity();
    }
    
    private String getDeathMessage(String input, final Entity entity, final Entity killer) {
        input = input.replaceFirst("\\[", ChatColor.GRAY + "[" + ChatColor.GRAY);
        input = replaceLast(input, "]", ChatColor.GRAY + "]" + ChatColor.GRAY);
        if (entity != null) {
            input = input.replaceFirst("(?i)" + this.getEntityName(entity), ChatColor.RED + this.getDisplayName(entity) + ChatColor.YELLOW);
        }
        if (killer != null && (entity == null || !killer.equals(entity))) {
            input = input.replaceFirst("(?i)" + this.getEntityName(killer), ChatColor.RED + this.getDisplayName(killer) + ChatColor.YELLOW);
        }
        return input;
    }
    
    private String getEntityName(final Entity entity) {
        Preconditions.checkNotNull((Object)entity, (Object)"Entity cannot be null");
        return (entity instanceof Player) ? ((Player)entity).getName() : ((CraftEntity)entity).getHandle().getName();
    }
    
    private String getDisplayName(final Entity entity) {
        Preconditions.checkNotNull((Object)entity, (Object)"Entity cannot be null");
        if (entity instanceof Player) {
            final Player player = (Player)entity;
            /*if(this.plugin.getUserManager().getUser(player.getUniqueId()) == null){
            	
                return "§7(Combat-Logger) §c" + player.getName() + ChatColor.GRAY + '[' + ChatColor.GRAY + (this.plugin.getUserManager().getUser(this.plugin.getNpcManager().getSpawnedNpc(player.getUniqueId()).getEntity().getUniqueId()) == null ? 0 : this.plugin.getUserManager().getUser(this.plugin.getNpcManager().getSpawnedNpc(player.getUniqueId()).getEntity().getUniqueId()).getKills()) + ChatColor.GRAY + ']';
            }*/
            return player.getName() + ChatColor.GRAY + '[' + ChatColor.GRAY + (this.plugin.getUserManager().getUser(player.getUniqueId()) == null ? 0 : this.plugin.getUserManager().getUser(player.getUniqueId()).getKills()) + ChatColor.GRAY + ']';
        }
        return WordUtils.capitalizeFully(entity.getType().name().replace('_', ' '));
    }
}
