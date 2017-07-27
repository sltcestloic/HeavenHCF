package fr.taeron.hcf.listener.fixes;

import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import org.bukkit.event.*;

public class VoidGlitchFixListener implements Listener
{
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerDamage(final EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
            final Entity entity = event.getEntity();
            if (entity instanceof Player) {
                if (entity.getWorld().getEnvironment() == World.Environment.THE_END) {
                    return;
                }
                final Location destination = Bukkit.getWorld("world").getSpawnLocation();
                if (destination == null) {
                    return;
                }
                if (entity.teleport(destination, PlayerTeleportEvent.TeleportCause.PLUGIN)) {
                    event.setCancelled(true);
                    ((Player)entity).sendMessage(ChatColor.YELLOW + "Tu as été sortit du void.");
                }
            }
        }
    }
}
