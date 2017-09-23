package fr.taeron.hcf.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.heavenmc.core.Core;
import org.heavenmc.core.command.module.staffmode.StaffMode;

public class StaffModeCommand implements CommandExecutor {

	@Override
    public boolean onCommand(CommandSender sender, Command label, String arg2, String[] arg3) {
	if(sender instanceof Player){
	    Player p = (Player)sender;
		if(StaffMode.isInStaffMode(p)){
		    Command.broadcastCommandMessage(sender, "§cStaffMode desactivé.");
		    StaffMode.mods.remove(p);
		    Core.getInstance().getUserManager().getPlayer(p).setVanished(false);
		}else{
		    p.getInventory().clear();
		    p.getInventory().setArmorContents(null);
		    StaffMode.mods.add(p);
		    p.setAllowFlight(true);
		    Command.broadcastCommandMessage(sender, "§aStaffMode activé.");
		    StaffMode.giveStaffItems(p, false);
		    Core.getInstance().getUserManager().getPlayer(p).setVanished(true);
		   
		}
	}
	return false;
    }



}
