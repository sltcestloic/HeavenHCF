package fr.taeron.hcf.listeners.fixes;

import org.bukkit.event.player.*;
import org.bukkit.event.block.*;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.material.*;
import org.bukkit.*;
import org.bukkit.inventory.*;
import java.util.*;

public class EnderChestRemovalListener implements Listener
{
    public EnderChestRemovalListener() {
        this.removeRecipe();
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEnderChestOpen(final PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.ENDER_CHEST) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onInventoryOpen(final InventoryOpenEvent event) {
        if (event.getInventory() instanceof EnderChest) {
            event.setCancelled(true);
        }
    }
    
    private void removeRecipe() {
        final Iterator<Recipe> iterator = (Iterator<Recipe>)Bukkit.recipeIterator();
        while (iterator.hasNext()) {
            if (iterator.next().getResult().getType() == Material.ENDER_CHEST) {
                iterator.remove();
            }
        }
    }
}
