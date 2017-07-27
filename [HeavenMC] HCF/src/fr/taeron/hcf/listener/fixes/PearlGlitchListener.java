package fr.taeron.hcf.listener.fixes;

import org.bukkit.event.block.*;
import org.bukkit.inventory.*;

import fr.taeron.hcf.HCF;
import fr.taeron.hcf.utils.UtilDirection;

import org.bukkit.block.*;

import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;


import org.bukkit.*;

public class PearlGlitchListener implements Listener
{
    
	@EventHandler
	public void test(PlayerMoveEvent e){
		Damageable d = (Damageable)e.getPlayer();
		if (Double.isNaN(d.getHealth())){
			d.setHealth(20l);
		}
	}
	
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().getType() == Material.ENDER_PEARL) {
            final Block block = event.getClickedBlock();
            if (block.getType().isSolid() && !(block.getState() instanceof InventoryHolder)) {
                event.setCancelled(true);
                final Player player = event.getPlayer();
                player.setItemInHand(event.getItem());
            }
        }
    }
    
	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPearlTP(final PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            final Location to = event.getTo();
            //east = x-
            //west = x+
            //north = z- 
            //south = z+
	        to.setX(to.getBlockX() + 0.5);
            to.setZ(to.getBlockZ() + 0.5);
            BlockFace face = UtilDirection.yawToFace(to.getYaw());
            if(face == BlockFace.EAST){
            	to.setX(to.getBlockX() - 0.5);
            }
            if(face == BlockFace.WEST){
            	to.setX(to.getBlockX() + 0.5);
            }
            if(to.getBlock().getRelative(0, 1, 0).isEmpty() && to.getBlock().getRelative(0, -1, 0).getType() != Material.FENCE && to.getBlock().getRelative(0, -1, 0).getType() != Material.FENCE_GATE && !to.getBlock().getRelative(0, -1, 0).isLiquid()){
            	to.setY(to.getBlockY() + 1);
            } 
            if(!to.getBlock().getRelative(0, -1, 0).isEmpty()){
            //	to.setY(to.getbl);
            }
            if(!to.getBlock().getRelative(0, 1, 0).isEmpty()){
            	if(face == BlockFace.SOUTH || face == BlockFace.NORTH){
	            	if(this.getBestHorizontal(face, to) == 0){
	            		event.setCancelled(true);
	                	event.getPlayer().sendMessage("§cGlitch détecté, impossible de te téléporter.");
	                	event.getPlayer().getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 1));
	                	HCF.getPlugin().getTimerManager().enderPearlTimer.clearCooldown(event.getPlayer());
	            	} else {
	            		to.setZ(this.getBestHorizontal(face, to));
	            	}
            	} else {
            		if(this.getBestHorizontal(face, to) == 0){
	            		event.setCancelled(true);
	                	event.getPlayer().sendMessage("§cGlitch détecté, impossible de te téléporter.");
	                	event.getPlayer().getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 1));
	                	HCF.getPlugin().getTimerManager().enderPearlTimer.clearCooldown(event.getPlayer());
	            	} else {
	            		to.setX(this.getBestHorizontal(face, to));
	            	}
            	}
            }
            event.setTo(to);
        }
    }
	
	public int getBestHorizontal(BlockFace face, Location to){
		if(face == BlockFace.SOUTH){
    		int z = to.getBlockZ();
    		int prevZ = to.getBlockZ();
    		for(int i = 0; i < 5; i++){
    			z += i;
    			to.setZ(z);
    			if(!to.getBlock().getRelative(0, 1, 0).isEmpty()){
    				to.setZ(prevZ);
    			} else {
    				return z;
    			}
    		}
    	}
    	if(face == BlockFace.NORTH){
    		int z = to.getBlockZ();
    		int prevZ = to.getBlockZ();
    		for(int i = 0; i < 5; i++){
    			z -= i;
    			to.setZ(z);
    			if(!to.getBlock().getRelative(0, 1, 0).isEmpty()){
    				to.setZ(prevZ);
    			} else {
    				return z;
    			}
    		}
    	}
    	if(face == BlockFace.EAST){
    		int x = to.getBlockX();
    		int prevX = to.getBlockX();
    		for(int i = 0; i < 5; i++){
    			x += i;
    			to.setX(x);
    			if(!to.getBlock().getRelative(0, 1, 0).isEmpty()){
    				to.setX(prevX);
    			} else {
    				return x;
    			}
    		}
    	}
    	if(face == BlockFace.WEST){
    		int x = to.getBlockX();
    		int prevX = to.getBlockX();
    		for(int i = 0; i < 5; i++){
    			x -= i;
    			to.setX(x);
    			if(!to.getBlock().getRelative(0, 1, 0).isEmpty()){
    				to.setX(prevX);
    			} else {
    				return x;
    			}
    		}
    	}
    	return 0;
	}
}
