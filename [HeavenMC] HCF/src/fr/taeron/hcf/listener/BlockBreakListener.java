package fr.taeron.hcf.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import fr.taeron.hcf.HCF;
import fr.taeron.hcf.command.ToggleCobblestoneCommand;
import fr.taeron.hcf.faction.type.Faction;

public class BlockBreakListener implements Listener{
	
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void listenBreak(BlockBreakEvent e){
		final Faction factionAt = HCF.getPlugin().getFactionManager().getFactionAt(e.getBlock().getLocation());
		if(factionAt.isSafezone() && !factionAt.getName().contains("Road") && !factionAt.getName().contains("Warzone") && !!factionAt.getName().contains("Wilderness")){
			return;
		}
		if(!e.getBlock().getType().equals(Material.COBBLESTONE) && !e.getBlock().getType().equals(Material.STONE)){
			return;
		}
		Player p = e.getPlayer();
		if(HCF.getPlugin().getPvpClassManager().getEquippedClass(p) == null){
			return;
		}
		if(!HCF.getPlugin().getPvpClassManager().getEquippedClass(p).equals(HCF.getPlugin().getPvpClassManager().getPvpClass("Mineur"))){
			return;
		}
		if(ToggleCobblestoneCommand.toggled.get(p) == null){
			return;
		} else {
			if(ToggleCobblestoneCommand.toggled.get(p) == false){
				return;
			}
			if(ToggleCobblestoneCommand.toggled.get(p) == true){
				e.setCancelled(true);
				e.getBlock().setType(Material.AIR);
			}
		}
	}

}
