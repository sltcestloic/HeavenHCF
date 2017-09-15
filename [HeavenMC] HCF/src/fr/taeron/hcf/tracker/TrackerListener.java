package fr.taeron.hcf.tracker;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.heavenmc.core.util.ItemBuilder;

import fr.taeron.hcf.HCF;
import fr.taeron.hcf.user.FactionUser;

public class TrackerListener implements Listener{

	public ItemStack grayPane = new ItemBuilder(Material.STAINED_GLASS_PANE).data((short)7).displayName("").build();
	public ItemStack redPane = new ItemBuilder(Material.STAINED_GLASS_PANE).data((short)14).displayName("").build();
	public ItemStack whitePane = new ItemBuilder(Material.STAINED_GLASS_PANE).data((short)0).displayName("").build();
	
	@EventHandler
	public void clickOnTrackerGui(InventoryClickEvent e){
		if(!e.getInventory().getTitle().equalsIgnoreCase("§cTracker")){
			return;
		}
		if(e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR){
			return;
		}
		e.setCancelled(true);
		Player p = (Player) e.getWhoClicked();
		ItemStack stack = e.getCurrentItem();
		if(stack.isSimilar(this.grayPane) || stack.isSimilar(this.redPane) || stack.isSimilar(this.whitePane)){
			return;
		}
		if(!stack.hasItemMeta()){
			return;
		}
		if(!stack.getItemMeta().hasDisplayName()){
			return;
		}
		if(stack.getItemMeta().getDisplayName().equalsIgnoreCase("§cAbandonner cette victime")){
			p.performCommand("tracker abandon");
			p.closeInventory();
			return;
		}
		if(stack.getItemMeta().getDisplayName().equalsIgnoreCase("§eRechercher une victime")){
			p.performCommand("tracker search");
			p.closeInventory();
			return;
		}
	}
	
	@EventHandler
	public void die(PlayerDeathEvent e){
		if(e.getEntity().getKiller() == null){
			return;
		}
		Player t = e.getEntity();
		Player p = e.getEntity().getKiller();
		FactionUser user = HCF.getPlugin().getUserManager().getUser(p.getUniqueId());
		if(user.getTrackingUser() != null && user.getTrackingUser() == t){
			int pKills = user.getKills();
			int tKills = HCF.getPlugin().getUserManager().getUser(t.getUniqueId()).getKills();
			int dif;
			int keys;
			if(tKills >= pKills){
				dif = pKills - tKills;
			} else {
				dif = tKills - pKills;
			}
			if(dif <= 0){
				keys = 1;
			} else if (dif <= 15){
				keys = 2;
			} else if (dif <= 25){
				keys = 3;
			} else if (dif <= 50){
				keys = 4;
			} else {
				keys = 5;
			}
			p.sendMessage("§eTu as tué le joueur que tu traquais: §6" + t.getName() + "§e!");
			p.sendMessage("§eIl avais " + Math.abs(dif) + " kill(s) " + (dif > 0 ? "de plus" : "de moins") + " que toi, tu gagnes donc §6" + keys + " Killer Key");
			if(!this.isInventoryFull(p)){
				p.getInventory().addItem(HCF.getPlugin().getKeyManager().getKillerKey().getItemStack());
			} else {
				p.getWorld().dropItem(p.getLocation(), HCF.getPlugin().getKeyManager().getKillerKey().getItemStack());
				p.sendMessage("§6Ton inventaire est plein, ta §eKiller key §6a été drop par terre.");
			}
			Bukkit.broadcastMessage("§6" + p.getName() + "§e a gagné sa traque sur §6" + t.getName() + "§e ! §6/tracker !");
			user.setTrackedUser(null);
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
}
