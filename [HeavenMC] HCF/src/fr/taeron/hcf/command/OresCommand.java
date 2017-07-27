package fr.taeron.hcf.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.taeron.hcf.HCF;
import fr.taeron.hcf.user.FactionUser;


public class OresCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)){
			return false;
		}
		Player p = (Player) sender;
		if(args.length == 0){			
			FactionUser user = HCF.getPlugin().getUserManager().getUser(p.getUniqueId());
			p.sendMessage("§7Stats de minage de §a" + p.getName() + "§7:");
			p.sendMessage("§bDiamond: §f" + user.diamonds);
			p.sendMessage("§6Gold: §f" + user.gold);
			p.sendMessage("§0Coal: §f" + user.coal);
			p.sendMessage("§aEmerald: §f" + user.emerald);
			p.sendMessage("§7Iron: §f" + user.iron);
			p.sendMessage("§9Lapis: §f" + user.lapis);
			p.sendMessage("§cRedstone: §f" + user.redstone);
			return true;
		}
		if(args.length == 1){
			Player t = Bukkit.getPlayer(args[0]);
			if(t == null){
				p.sendMessage("§c" + args[0] + " n'est pas connecté.");
				return false;
			}
			FactionUser user = HCF.getPlugin().getUserManager().getUser(t.getUniqueId());
			p.sendMessage("§7Stats de minage de §a" + t.getName() + "§7:");
			p.sendMessage("§bDiamond: §f" + user.diamonds);
			p.sendMessage("§6Gold: §f" + user.gold);
			p.sendMessage("§0Coal: §f" + user.coal);
			p.sendMessage("§aEmerald: §f" + user.emerald);
			p.sendMessage("§7Iron: §f" + user.iron);
			p.sendMessage("§9Lapis: §f" + user.lapis);
			p.sendMessage("§cRedstone: §f" + user.redstone);
			return true;
		}
		p.sendMessage("§cUtilisation: /" + label + " (<joueur>)");
		return false;
	}
}
