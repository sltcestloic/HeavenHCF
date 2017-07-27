package fr.taeron.hcf.command;

import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

//import com.exodon.hcf.ConfigurationService;

public class ToggleCobblestoneCommand implements CommandExecutor{
	
	public static HashMap<Player, Boolean> toggled = new HashMap<Player, Boolean>();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)){
			return false;
		}
		Player p = (Player) sender;
		/*if(!p.hasPermission("heaven.bronze") && !p.hasPermission("heaven.silver") && !p.hasPermission("heaven.gold") && !p.hasPermission("heaven.diamond") && !p.hasPermission("heaven.emerald")){
			sender.sendMessage("§cTu n'as pas la permission d'utiliser le " + label + ".");
			sender.sendMessage("§cDeviens donateur sur §6§n" + ConfigurationService.DONATE_URL);
			return false;
		}*/
		if(!toggled.containsKey(p)){
			p.sendMessage("§7Drop de cobblestone §cdésactivé");
			toggled.put(p, true);
		} else {
			if(toggled.get(p) == false){
				p.sendMessage("§7Drop de cobblestone §cdésactivé");
				toggled.put(p, true);
				return true;
			}
			if(toggled.get(p) == true){
				p.sendMessage("§7Drop de cobblestone §aactivé");
				toggled.put(p, false);
				return true;
			}
		}
		return false;
	}
}
