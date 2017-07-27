package fr.taeron.hcf.listener;

import java.util.Objects;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;


public class ElevatorListener implements Listener{
	
	

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onKitSign(final PlayerInteractEvent event) {
	    if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
	        final Block block = event.getClickedBlock();
	        final BlockState state = block.getState();
	        if (!(state instanceof Sign)) {
	            return;
	        }
	        final Sign sign = (Sign)state;
	        final String[] lines = sign.getLines();
	        Player p = event.getPlayer();
	        if(lines.length >= 2){
		        if(lines[0].toLowerCase().contains("elevator")){
		        	if(lines[1].toLowerCase().contains("up")){
		        		final Location origin = sign.getLocation().clone();
		                final Location highestLocation = this.teleportSpotUpTest(origin);
		                final Block originBlock;
		                if (highestLocation == null || Objects.equals(highestLocation, origin) || (highestLocation.getBlockY() - (originBlock = origin.getBlock()).getY() == 1 && originBlock.getType() == Material.WATER) || originBlock.getType() == Material.STATIONARY_WATER) {
		                    p.sendMessage(ChatColor.RED + "Aucune position n'a été trouvée.");
		                    return;
		                }
		                p.teleport(highestLocation.add(0.5, 1.0, 0.5), PlayerTeleportEvent.TeleportCause.PLUGIN);
		        	}
		        	if(lines[1].toLowerCase().contains("down")){
		        		final Location origin = sign.getLocation().clone();
		                final Location lowestLocation = this.teleportSpotDownTest(origin);
		                if (lowestLocation == null) {
		                    p.sendMessage(ChatColor.RED + "Aucune position n'a été trouvée.");
		                    return;
		                }
		                p.teleport(lowestLocation.add(0.5, 0, 0.5));
		        	}
		        }
	        }
	    }
	}
	
   @EventHandler
    public void onMinecart(final VehicleEnterEvent event) {
        if (!(event.getVehicle() instanceof Minecart) || !(event.getEntered() instanceof Player)) {
            return;
        }
        final Player p = (Player)event.getEntered();
        final Location l = event.getVehicle().getLocation();
        final Location loc = new Location(p.getWorld(), (double)l.getBlockX(), (double)l.getBlockY(), (double)l.getBlockZ());
        final Material m = loc.getBlock().getType();
        if (m.equals((Object)Material.FENCE_GATE) || m.equals((Object)Material.SIGN_POST)) {
            event.setCancelled(true);
            if (p.isSneaking()) {
                p.teleport(this.teleportSpotUp(loc, loc.getBlockY(), 254));
            }
        }
    }
	
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onSignCreate(final SignChangeEvent e) {
        final String[] lines = e.getLines();
        if(lines[0].toLowerCase().contains("[elevator]")){
        	if(lines[1].toLowerCase().contains("down")){
        		e.setLine(0, ChatColor.GOLD + "[Elevator]");
            	e.setLine(1, "Down");
           		return;
            }
            if(lines[1].toLowerCase().contains("up")){
            	e.setLine(0, ChatColor.GOLD + "[Elevator]");
            	e.setLine(1, "Up");
            	return;
            }
        }
    }
	public Location teleportSpotDownTest(final Location loc) {
		for(int k = loc.getBlockY() - 1; k > 0; k--){
			Block b = new Location(loc.getWorld(), loc.getBlockX(), k, loc.getBlockZ()).getBlock();
			Block b2 = new Location(loc.getWorld(), loc.getBlockX(), k + 1, loc.getBlockZ()).getBlock();
			if(b.getType() == Material.AIR && b2.getType() == Material.AIR && !b.getRelative(0, -1, 0).isEmpty()){
				return new Location(loc.getWorld(), loc.getBlockX(), k, loc.getBlockZ());
			}
		}
		return null;
	}
	
	public Location teleportSpotUpTest(final Location loc) {
		for(int k = loc.getBlockY() + 2; k < 255; k++){
			Block b = new Location(loc.getWorld(), loc.getBlockX(), k, loc.getBlockZ()).getBlock();
			Block b2 = new Location(loc.getWorld(), loc.getBlockX(), k + 1, loc.getBlockZ()).getBlock();
			if(b.getType() == Material.AIR && b2.getType() == Material.AIR && b.getRelative(0, -1, 0).getType() != Material.AIR){
				return new Location(loc.getWorld(), loc.getBlockX(), k + 2, loc.getBlockZ());
			}
		}
		return null;
	}
	
	public Location teleportSpotDown(final Location loc, final int min, final int max) {
        for (int k = max; k > min; --k) {	
            final Material m1 = new Location(loc.getWorld(), (double)loc.getBlockX(), (double)k, (double)loc.getBlockZ()).getBlock().getType();
            final Material m2 = new Location(loc.getWorld(), (double)loc.getBlockX(), (double)(k + 1), (double)loc.getBlockZ()).getBlock().getType();
            if (m1.equals((Object)Material.AIR) && m2.equals((Object)Material.AIR)) {
                return new Location(loc.getWorld(), (double)loc.getBlockX(), (double)k, (double)loc.getBlockZ());
            }
        }
        return null;
    }

	public Location teleportSpotUp(final Location loc, final int min, final int max) {
        for (int k = min; k < max; ++k) {
            final Material m1 = new Location(loc.getWorld(), (double)loc.getBlockX(), (double)k, (double)loc.getBlockZ()).getBlock().getType();
            final Material m2 = new Location(loc.getWorld(), (double)loc.getBlockX(), (double)(k + 1), (double)loc.getBlockZ()).getBlock().getType();
            if (m1.equals((Object)Material.AIR) && m2.equals((Object)Material.AIR)) {
                return new Location(loc.getWorld(), (double)loc.getBlockX(), (double)k, (double)loc.getBlockZ());
            }
        }
        return null;
    }
}

