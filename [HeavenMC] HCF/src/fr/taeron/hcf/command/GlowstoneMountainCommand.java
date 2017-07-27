package fr.taeron.hcf.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import fr.taeron.hcf.GlowstoneMountain;

public class GlowstoneMountainCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender.hasPermission("build"))){
			return false;
		}
		if(args.length != 1){
			sender.sendMessage("§c/" + label + " <regen>");
			return false;
		}
		if(args[0].equalsIgnoreCase("regen")){
			GlowstoneMountain.resetMountain();
		} else {
			sender.sendMessage("§c/" + label + " <regen>");
			return false;
		}
		return false;
	}
}
