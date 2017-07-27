package fr.taeron.hcf.faction.argument;

import fr.taeron.hcf.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.*;
import java.util.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.heavenmc.core.util.command.CommandArgument;

public class FactionClaimArgument extends CommandArgument implements Listener{
	
    public final HCF plugin;
    
    public FactionClaimArgument(final HCF plugin) {
        super("claim", "Claim une partie de la map", new String[] { "claimland" });
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName();
    }
    
    @EventHandler
    public void onClick(InventoryClickEvent e){
    	if(!e.getInventory().getName().equalsIgnoreCase(ChatColor.GREEN + "Claim")){
    		return;
    	}
    	if(e.getSlotType() == SlotType.OUTSIDE){
    		return;
    	}
    	if(e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR)){
    		return;
    	}
    	if(!e.getCurrentItem().hasItemMeta()){
    		return;
    	}
    	if(!e.getCurrentItem().getItemMeta().hasDisplayName()){
    		return;
    	}
    	Player p = (Player) e.getWhoClicked();
    	if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§aClaim à la Wand")){
    		p.performCommand("f claimwand");
    	}
    	if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§aClaim le Chunk")){
    		p.performCommand("f claimchunk");
    	}
    	e.setCancelled(true);
    	p.closeInventory();
    }
    	
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "La console n'est pas supportée.");
            return true;
        }
        final Player player = (Player)sender;
        Inventory menu = Bukkit.createInventory(null, 9, ChatColor.GREEN + "Claim");
        
        ItemStack wand = new ItemStack(Material.DIAMOND_HOE);
        ItemMeta wandm = wand.getItemMeta();
        wandm.setDisplayName("§aClaim à la Wand");
        wandm.setLore(Arrays.asList("§7Sélectionne une zone comme", "§7avec WorldEdit pour claim."));
        wand.setItemMeta(wandm);
        
        ItemStack chunk = new ItemStack(Material.GRASS);
        ItemMeta chunkm = chunk.getItemMeta();
        chunkm.setDisplayName("§aClaim le Chunk");
        chunkm.setLore(Arrays.asList("§7Claim une zone de 16x16 blocs"));
        chunk.setItemMeta(chunkm);
        
        menu.setItem(3, wand);
        menu.setItem(5, chunk);
        
        player.openInventory(menu);
		return true;
    }
    
}
