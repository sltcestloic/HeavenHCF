package fr.taeron.hcf.listeners.fixes;

import org.bukkit.event.player.*;
import org.bukkit.enchantments.*;
import fr.taeron.hcf.*;
import org.bukkit.*;
import org.bukkit.event.block.*;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.entity.*;

public class NoPermissionClickListener implements Listener
{
    @EventHandler
    public void onClick(final PlayerInteractEvent e) {
        for (final Enchantment enchantment : e.getItem().getEnchantments().keySet()) {
            if (ConfigurationService.ENCHANTMENT_LIMITS.containsKey(enchantment) && e.getItem().getEnchantments().get(enchantment) > ConfigurationService.ENCHANTMENT_LIMITS.get(enchantment)) {
                e.getItem().removeEnchantment(enchantment);
                e.getItem().addEnchantment(enchantment, (int)ConfigurationService.ENCHANTMENT_LIMITS.get(enchantment));
            }
        }
    }
    
    @EventHandler
    public void onInteract(final PlayerInteractEvent e) {
        final Player player = e.getPlayer();
        if (player.getGameMode().equals(GameMode.CREATIVE) && !player.hasPermission("base.command.gamemode")) {
            e.setCancelled(true);
            player.setGameMode(GameMode.SURVIVAL);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockPlaceCreative(final BlockBreakEvent event) {
        final Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE && !player.hasPermission("base.command.gamemode")) {
            event.setCancelled(true);
            player.setGameMode(GameMode.SURVIVAL);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockBreakCreative(final BlockBreakEvent event) {
        final Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE && !player.hasPermission("base.command.gamemode")) {
            event.setCancelled(true);
            player.setGameMode(GameMode.SURVIVAL);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onInventoryCreative(final InventoryCreativeEvent event) {
        final HumanEntity humanEntity = event.getWhoClicked();
        if (humanEntity instanceof Player && !humanEntity.hasPermission("base.command.gamemode")) {
            event.setCancelled(true);
            humanEntity.setGameMode(GameMode.SURVIVAL);
        }
    }
}
