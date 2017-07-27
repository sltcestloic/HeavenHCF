package fr.taeron.hcf.listener;

import org.bukkit.event.entity.*;
import org.bukkit.*;
import org.bukkit.enchantments.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.player.*;
import org.bukkit.projectiles.*;
import org.bukkit.event.inventory.*;

public class ExpMultiplierListener implements Listener
{
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onEntityDeath(final EntityDeathEvent event) {
        final double amount = event.getDroppedExp();
        final Player killer = event.getEntity().getKiller();
        if (killer != null && amount > 0.0) {
            final ItemStack stack = killer.getItemInHand();
            if (stack != null && stack.getType() != Material.AIR) {
                final int enchantmentLevel = stack.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);
                if (enchantmentLevel > 0L) {
                    final double multiplier = enchantmentLevel * 1.5;
                    final int result = (int)Math.ceil(amount * multiplier);
                    event.setDroppedExp(result);
                }
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onBlockBreak(final BlockBreakEvent event) {
        final double amount = event.getExpToDrop();
        final Player player = event.getPlayer();
        final ItemStack stack = player.getItemInHand();
        if (stack != null && stack.getType() != Material.AIR && amount > 0.0) {
            final int enchantmentLevel = stack.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
            if (enchantmentLevel > 0) {
                final double multiplier = enchantmentLevel * 1.5;
                final int result = (int)Math.ceil(amount * multiplier);
                event.setExpToDrop(result);
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerPickupExp(final PlayerExpChangeEvent event) {
        final double amount = event.getAmount();
        if (amount > 0.0) {
            final int result = (int)Math.ceil(amount * 2.0);
            event.setAmount(result);
        }
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerFish(final PlayerFishEvent event) {
        double amount = event.getExpToDrop();
        if (amount > 0.0) {
            amount = Math.ceil(amount * 2.0);
            final ProjectileSource projectileSource = event.getHook().getShooter();
            if (projectileSource instanceof Player) {
                final ItemStack stack = ((Player)projectileSource).getItemInHand();
                final int enchantmentLevel = stack.getEnchantmentLevel(Enchantment.LUCK);
                if (enchantmentLevel > 0L) {
                    amount = Math.ceil(amount * (enchantmentLevel * 1.5));
                }
            }
            event.setExpToDrop((int)amount);
        }
    }
    
    @SuppressWarnings("unused")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onFurnaceExtract(final FurnaceExtractEvent event) {
        final double amount = event.getExpToDrop();
        if (amount > 0.0) {
            final double multiplier = 2.0;
            final int result = (int)Math.ceil(amount * 2.0);
            event.setExpToDrop(result);
        }
    }
}
