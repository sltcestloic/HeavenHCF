package fr.taeron.hcf.flip;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.ItemStack;

import fr.taeron.hcf.HCF;
import fr.taeron.hcf.user.FactionUser;

public class FlipListener implements Listener{

	@EventHandler
	public void GUIClick(InventoryClickEvent e){
		if(e.getSlotType() != SlotType.CONTAINER){
			return;
		}
		if(!e.getInventory().getTitle().equalsIgnoreCase("§eFlip")){
			return;
		}
		if(e.getCurrentItem() == null || e.getCurrentItem().getType() == null || e.getCurrentItem().getType() == Material.AIR){
			return;
		}
		e.setCancelled(true);
		Player p = (Player) e.getWhoClicked();
		if(!e.getCurrentItem().hasItemMeta() || !e.getCurrentItem().getItemMeta().hasDisplayName()){
			return;
		}
		if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§f8 blocs de fer")){
			this.tryWin(p, Material.IRON_BLOCK, 8);
		}
		if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§a4 blocs d'emeraude")){
			this.tryWin(p, Material.EMERALD_BLOCK, 4);	
		}
		if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§e6 blocs d'or")){
			this.tryWin(p, Material.GOLD_BLOCK, 6);
		}
		if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§b4 blocs de diamant")){
			this.tryWin(p, Material.DIAMOND_BLOCK, 4);	
		}
	}
	
	public boolean isInventoryFull(Player p){
    	for(int i = 0; i < 36; i ++){
    		if(p.getInventory().getItem(i) == null){
    			return false;
    		}
    	}
    	return true;
    }
	
	public void tryWin(Player p, Material i, int amount){
		p.closeInventory();
		int randInt = this.randInt(1, 3);
		if(randInt == 3){
			if(this.isInventoryFull(p)){
				p.getWorld().dropItemNaturally(p.getLocation(), new ItemStack(i , amount));
			} else {
				p.getInventory().addItem(new ItemStack(i, amount));
			}
			p.sendMessage("§eTu as gagné ! §6Félicitations §e!");
		} else {
			p.sendMessage("§cTu as perdu !");
		}
		FactionUser user = HCF.getPlugin().getUserManager().getUser(p.getUniqueId());
		user.hasUsedFlip = true;
	}
	
	public int randInt(int min, int max) {

	    // NOTE: This will (intentionally) not run as written so that folks
	    // copy-pasting have to think about how to initialize their
	    // Random instance.  Initialization of the Random instance is outside
	    // the main scope of the question, but some decent options are to have
	    // a field that is initialized once and then re-used as needed or to
	    // use ThreadLocalRandom (if using at least Java 1.7).
	    Random rand = new Random();

	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}
}
