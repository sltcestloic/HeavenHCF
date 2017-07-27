package fr.taeron.hcf.deathban.lives;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class LivesListener implements Listener {

	
	@EventHandler
	public void onRightClickLife(PlayerInteractEvent e){
		if(e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK){
			return;
		}
		Player p = e.getPlayer();
		if(p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR){
			return;
		}
		ItemStack stack = p.getItemInHand();
		if(!stack.hasItemMeta()){
			return;
		}
		if(!stack.getItemMeta().hasDisplayName()){
			return;
		}
		if(stack.getItemMeta().getDisplayName().startsWith("§fVie")){
			this.decrementHand(p);
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lives give " + p.getName() + " 1");
		}
	}
	
    private void decrementHand(final Player player) {
        final ItemStack stack = player.getItemInHand();
        if (stack.getAmount() <= 1) {
            player.setItemInHand(new ItemStack(Material.AIR, 1));
        }
        else {
            stack.setAmount(stack.getAmount() - 1);
        }
    }
}
