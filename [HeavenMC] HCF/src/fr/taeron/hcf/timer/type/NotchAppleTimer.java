package fr.taeron.hcf.timer.type;

import fr.taeron.hcf.timer.*;
import org.bukkit.plugin.java.*;
import java.util.concurrent.*;
import org.bukkit.event.player.*;
import org.bukkit.*;
import org.bukkit.inventory.*;
import org.bukkit.event.*;

public class NotchAppleTimer extends PlayerTimer implements Listener
{
    public NotchAppleTimer(final JavaPlugin plugin) {
        super("GApple", TimeUnit.HOURS.toMillis(6L));
    }
    
    public String getScoreboardPrefix() {
        return ChatColor.GOLD.toString() + ChatColor.BOLD;
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerConsume(final PlayerItemConsumeEvent event) {
        final ItemStack stack = event.getItem();
        if (stack != null && stack.getType() == Material.GOLDEN_APPLE && stack.getDurability() == 1) {
        	event.setCancelled(true);
            /*final Player player = event.getPlayer();
            if (!this.setCooldown(player, player.getUniqueId(), this.defaultCooldown, false)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "Tu est sous " + this.getDisplayName() + ChatColor.RED + " cooldown pendant " + ChatColor.BOLD + HCF.getRemaining(this.getRemaining(player), true, false) + ChatColor.RED + '.');
            }*/
        }
    }
}
