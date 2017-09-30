package fr.taeron.hcf.command;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.heavenmc.core.Core;
import org.heavenmc.core.command.module.staffmode.StaffMode;
import org.heavenmc.core.command.module.staffmode.StaffModeHCF;

public class StaffModeCommand implements CommandExecutor {

	@Override
    public boolean onCommand(CommandSender sender, Command label, String arg2, String[] arg3) {
	if(sender instanceof Player){
	    Player p = (Player)sender;
		if(StaffMode.isInStaffMode(p)){
		    Command.broadcastCommandMessage(sender, "§cStaffMode desactivé.");
		    StaffMode.mods.remove(p);
		    Core.getInstance().getUserManager().getPlayer(p).setVanished(false);
		    p.getInventory().clear();
		    p.setGameMode(GameMode.SURVIVAL);
		    p.teleport(Bukkit.getWorld("world").getSpawnLocation());
		}else{
		    p.getInventory().clear();
		    p.getInventory().setArmorContents(null);
		    StaffMode.mods.add(p);
		    p.setAllowFlight(true);
		    Command.broadcastCommandMessage(sender, "§aStaffMode activé.");
		    StaffModeHCF.giveStaffItems(p, false);
		    Core.getInstance().getUserManager().getPlayer(p).setVanished(true);
		   
		}
	}
	return false;
    }



}
