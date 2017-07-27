package fr.taeron.hcf.listener;

import org.bukkit.event.entity.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;

import net.minecraft.util.org.apache.commons.lang3.text.WordUtils;

import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.event.block.*;
import org.bukkit.*;
import org.bukkit.block.*;

public class SkullListener implements Listener
{
    
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final Player killer = player.getKiller();
        if (killer != null) {
        	ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
            final SkullMeta meta = (SkullMeta)skull.getItemMeta();
            meta.setOwner(player.getName());
            skull.setItemMeta((ItemMeta)meta);
            event.getDrops().add(skull);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            final Player player = event.getPlayer();
            final BlockState state = event.getClickedBlock().getState();
            if (state instanceof Skull) {
                final Skull skull = (Skull)state;
                player.sendMessage(ChatColor.DARK_AQUA + "C'est la tête de " + ChatColor.WHITE + ((skull.getSkullType() == SkullType.PLAYER && skull.hasOwner()) ? skull.getOwner() : ("un " + WordUtils.capitalizeFully(skull.getSkullType().name()))) + ChatColor.YELLOW + '.');
            }
        }
    }
}
