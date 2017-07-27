package fr.taeron.hcf.listener.fixes;


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

public class PortalTrapFixListener implements Listener
{
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPortalPlace(BlockPlaceEvent e){
		if(e.getBlock().isLiquid() || e.getBlock().getTypeId() == 90){
			return;
		}
		if(this.isNearToPortal(e.getBlock().getLocation())){
			e.setCancelled(true);
			e.getPlayer().sendMessage("§cTu ne peux pas poser de blocks près d'un portail.");
			e.getPlayer().damage(2);
		}
	}	
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onLavaPlace(PlayerBucketEmptyEvent e){
		if(isNearToPortal(e.getBlockClicked().getLocation())){
			e.setCancelled(true);
			e.getPlayer().sendMessage("§cTu ne peux pas poser de lave près d'un portail.");
			e.getPlayer().damage(2);
		}
	}
	
	
	public boolean isNearToPortal(Location loc) {
	    for (int x = (loc.getBlockX()-2); x <= (loc.getBlockX()+2); x++) {
	        for (int y = (loc.getBlockY()-2); y <= (loc.getBlockY()+2); y++) {
	            for (int z = (loc.getBlockZ()-2); z <= (loc.getBlockZ()+2); z++) {
	                Location l = new Location(loc.getWorld(), x, y, z);
	                if (l.distance(loc) <= 2) {
	                    if(l.getBlock().getType().equals(Material.PORTAL)){
	                    	return true;
	                    }
	                }
	            }
	        }
	    }
		return false;  
	}
}
