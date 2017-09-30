package fr.taeron.hcf.command;

import org.bukkit.plugin.*;
import org.bukkit.command.*;
import org.bukkit.enchantments.*;

import fr.taeron.hcf.ConfigurationService;
import fr.taeron.hcf.HCF;

import org.bukkit.potion.*;
import org.heavenmc.core.util.ItemBuilder;
import org.bukkit.*;
import org.bukkit.inventory.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.*;
import org.bukkit.event.server.*;
import org.bukkit.entity.*;
import java.util.*;

public class MapKitCommand implements CommandExecutor, TabCompleter, Listener
{
    private final Set<Inventory> tracking;
    
    public MapKitCommand(final HCF plugin) {
        this.tracking = new HashSet<Inventory>();
        plugin.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)plugin);
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        this.openGUI((Player)sender);
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return Collections.emptyList();
    }
    
    public void openGUI(Player p){
    	Inventory i = Bukkit.createInventory(null, 54, "§eKit de la map 4");
    	ItemStack grayPane = new ItemBuilder(Material.STAINED_GLASS_PANE).data((short)15).displayName("").build();
    	ItemStack whitePane = new ItemBuilder(Material.STAINED_GLASS_PANE).data((short)0).displayName("").build();
    	ItemStack redPane = new ItemBuilder(Material.STAINED_GLASS_PANE).data((short)14).displayName("").build();
    	ItemStack helmet = new ItemBuilder(Material.DIAMOND_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).build();
    	ItemStack chestplate = new ItemBuilder(Material.DIAMOND_CHESTPLATE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).build();
    	ItemStack leggings = new ItemBuilder(Material.DIAMOND_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).build();
    	ItemStack boots = new ItemBuilder(Material.DIAMOND_BOOTS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).build();
    	ItemStack sword = new ItemBuilder(Material.DIAMOND_SWORD).enchant(Enchantment.DAMAGE_ALL, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.DAMAGE_ALL)).build();
    	ItemStack bow = new ItemBuilder(Material.BOW).enchant(Enchantment.ARROW_DAMAGE, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.ARROW_DAMAGE)).enchant(Enchantment.ARROW_FIRE, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.ARROW_FIRE)).build();
    	i.setItem(0, grayPane);
    	i.setItem(1, grayPane);
    	i.setItem(2, grayPane);
    	i.setItem(3, grayPane);
    	i.setItem(4, grayPane);
    	i.setItem(5, grayPane);
    	i.setItem(6, grayPane);
    	i.setItem(7, grayPane);
    	i.setItem(8, grayPane);
    	i.setItem(9, grayPane);
    	i.setItem(10, whitePane);
    	i.setItem(11, redPane);
    	i.setItem(12, whitePane);
    	i.setItem(13, helmet);
    	i.setItem(14, whitePane);
    	i.setItem(15, redPane);
    	i.setItem(16, whitePane);
    	i.setItem(17, grayPane);
    	i.setItem(18, grayPane);
    	i.setItem(19, whitePane);
    	i.setItem(20, redPane);
    	i.setItem(21, new ItemBuilder(new Potion(PotionType.INVISIBILITY).toItemStack(1)).build());
    	i.setItem(22, chestplate);
    	i.setItem(23, sword);
    	i.setItem(24, redPane);
    	i.setItem(25, whitePane);
    	i.setItem(26, grayPane);
    	i.setItem(27, grayPane);
    	i.setItem(28, whitePane);
    	i.setItem(29, redPane);
    	i.setItem(30, new ItemBuilder(new Potion(PotionType.POISON).splash().toItemStack(1)).build());
    	i.setItem(31, leggings);
    	i.setItem(32, bow);
    	i.setItem(33, redPane);
    	i.setItem(34, whitePane);
    	i.setItem(35, grayPane);
    	i.setItem(36, grayPane);
    	i.setItem(37, whitePane);
    	i.setItem(38, redPane);
    	i.setItem(39, whitePane);
    	i.setItem(40, boots);
    	i.setItem(41, whitePane);
    	i.setItem(42, redPane);
    	i.setItem(43, whitePane);
    	i.setItem(44, grayPane);
    	i.setItem(45, grayPane);
    	i.setItem(46, grayPane);
    	i.setItem(47, grayPane);
    	i.setItem(48, grayPane);
    	i.setItem(49, grayPane);
    	i.setItem(50, grayPane);
    	i.setItem(51, grayPane);
    	i.setItem(52, grayPane);
    	i.setItem(53, grayPane);
    	p.openInventory(i);
    	this.tracking.add(i);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onInventoryClick(final InventoryClickEvent event) {
        if (this.tracking.contains(event.getInventory())) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPluginDisable(final PluginDisableEvent event) {
        for (final Inventory inventory : this.tracking) {
            final Collection<HumanEntity> viewers = new HashSet<HumanEntity>(inventory.getViewers());
            for (final HumanEntity viewer : viewers) {
                viewer.closeInventory();
            }
        }
    }
}
