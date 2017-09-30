package fr.taeron.hcf.listeners.fixes;

import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.block.*;
import org.bukkit.event.*;

public class BlockJumpGlitchFixListener implements Listener
{
    @EventHandler(ignoreCancelled = false, priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockPlaceEvent event) {
        if (event.isCancelled()) {
            final Player player = event.getPlayer();
            if (player.getGameMode() == GameMode.CREATIVE || player.getAllowFlight()) {
                return;
            }
            final Block block = event.getBlockPlaced();
            if (block.getType().isSolid() && !(block.getState() instanceof Sign)) {
            	int playerY = player.getLocation().getBlockY();
            	int blockY = block.getLocation().getBlockY();
            	int playerX = player.getLocation().getBlockX();
                int blockX = block.getLocation().getBlockX();
                int playerZ = player.getLocation().getBlockZ();
                int blockZ = block.getLocation().getBlockZ();
                if (playerY > blockY && blockX == playerX && blockZ == playerZ && player.getLocation().getBlock().getRelative(0, -1, 0).getType().equals(Material.AIR)) {
                    player.teleport(player.getLocation().add(0, -1, 0));
                }
            }
        }
    }
    
    @EventHandler
    public void fixTeleportFall(PlayerTeleportEvent e){
    	if(e.getCause() == TeleportCause.UNKNOWN){
    		e.getPlayer().setFallDistance(0);
    	}
    }
}
