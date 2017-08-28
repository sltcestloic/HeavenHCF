package fr.taeron.hcf.listeners;

import org.bukkit.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.event.block.*;
import org.bukkit.enchantments.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.event.*;

public class BookDeenchantListener implements Listener
{
    private static final ItemStack EMPTY_BOOK;
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_BLOCK && event.hasItem()) {
            final Player player = event.getPlayer();
            if (event.getClickedBlock().getType() == Material.ENCHANTMENT_TABLE && player.getGameMode() != GameMode.CREATIVE) {
                final ItemStack stack = event.getItem();
                if (stack != null && stack.getType() == Material.ENCHANTED_BOOK) {
                    final ItemMeta meta = stack.getItemMeta();
                    if (meta instanceof EnchantmentStorageMeta) {
                        final EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta)meta;
                        for (final Enchantment enchantment : enchantmentStorageMeta.getStoredEnchants().keySet()) {
                            enchantmentStorageMeta.removeStoredEnchant(enchantment);
                        }
                        event.setCancelled(true);
                        player.setItemInHand(BookDeenchantListener.EMPTY_BOOK);
                        player.sendMessage(ChatColor.GREEN + "Tu as désenchanté ce livre.");
                    }
                }
            }
        }
    }
    
    static {
        EMPTY_BOOK = new ItemStack(Material.BOOK, 1);
    }
}
