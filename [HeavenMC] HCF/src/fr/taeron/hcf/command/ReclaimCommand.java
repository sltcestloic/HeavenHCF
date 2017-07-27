package fr.taeron.hcf.command;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import fr.taeron.hcf.ConfigurationService;

public class ReclaimCommand implements CommandExecutor, TabCompleter{

	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage("§cCette commande n'ext pas executable depuis la console.");
			return false;
		}
		if(!sender.hasPermission("heaven.vip") && !sender.hasPermission("heaven.pro") && !sender.hasPermission("heaven.elite")){
			sender.sendMessage("§cCette commande est réservée au donateurs, elle permet de recevoir des crate keys à chaque map.");
			sender.sendMessage("§eDeviens donateur sur §6§n" + ConfigurationService.DONATE_URL);
			return false;
		}
		Player p = (Player) sender;
		File f = new File("plugins/HeavenHCF/reclaim/" + p.getUniqueId() + ".yml");
		if(f.exists()){
			sender.sendMessage("§cTu as déjà réclamé des items de donateur.");
			return false;
		}
		if(sender.hasPermission("heaven.elite")){
			try {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "key give " + p.getName() + " Heaven 3");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "key give " + p.getName() + " Master 2");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lives give " + p.getName() + " 20");
				Bukkit.broadcastMessage("§7[§6HeavenMC§7] §6" + p.getName() + " §ea récuperé ses avantages de donateur grâce à la commande §6/reclaim");
				sender.sendMessage("§aTu as reçu tes clés.");
				YamlConfiguration config = new YamlConfiguration();
				config.save(f);
			} catch (IOException e) {
				sender.sendMessage("§cUne erreur est survenue, merci de contacter un administrateur.");
				e.printStackTrace();
			}
			return true;
		}
		if(sender.hasPermission("heaven.pro")){
			try {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "key give " + p.getName() + " Heaven 2");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "key give " + p.getName() + " Master 1");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lives give " + p.getName() + " 10");
				Bukkit.broadcastMessage("§7[HeavenMC] §6" + p.getName() + " §ea récuperé ses avantages de donateur grâce à la commande §6/reclaim");
				sender.sendMessage("§aTu as reçu tes clés.");
				YamlConfiguration config = new YamlConfiguration();
				config.save(f);
			} catch (IOException e) {
				sender.sendMessage("§cUne erreur est survenue, merci de contacter un administrateur.");
				e.printStackTrace();
			}
			return true;
		}
		if(sender.hasPermission("heaven.vip")){
			try {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "key give " + p.getName() + " Heaven 2");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lives give " + p.getName() + " 4");
				Bukkit.broadcastMessage("§7[HeavenMC] §6" + p.getName() + " §ea récuperé ses avantages de donateur grâce à la commande §6/reclaim");
				sender.sendMessage("§aTu as reçu tes clés.");
				YamlConfiguration config = new YamlConfiguration();
				config.save(f);
			} catch (IOException e) {
				sender.sendMessage("§cUne erreur est survenue, merci de contacter un administrateur.");
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}
}
