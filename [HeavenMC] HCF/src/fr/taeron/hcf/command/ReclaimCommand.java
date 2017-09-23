package fr.taeron.hcf.command;

import java.io.File;
import java.io.IOException;
			
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import fr.taeron.hcf.ConfigurationService;

public class ReclaimCommand implements CommandExecutor{


	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage("§cCette commande n'ext pas executable depuis la console.");
			return false;
		}
		if(!sender.hasPermission("premium")){
			sender.sendMessage("§cCette commande est réservée au donateurs, elle permet de recevoir des crate keys et des vies à chaque map.");
			sender.sendMessage("§eDeviens donateur sur §6§n" + ConfigurationService.DONATE_URL);
			return false;
		}
		Player p = (Player) sender;
		File f = new File("plugins/HeavenHCF/reclaim/" + p.getUniqueId() + ".yml");
		if(f.exists()){
			sender.sendMessage("§cTu as déjà réclamé des items de donateur.");
			return false;
		}
		if(sender.hasPermission("heaven")){
			try {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "key give " + p.getName() + " Heaven 20");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "key give " + p.getName() + " Master 15");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lives give " + p.getName() + " 30");
				Bukkit.broadcastMessage("§7[§6§lHeavenMC§7] §6" + p.getName() + " §ea récuperé ses avantages de donateur grâce à la commande §6/reclaim");
				sender.sendMessage("§aTu as reçu tes clés.");
				YamlConfiguration config = new YamlConfiguration();
				config.save(f);
			} catch (IOException e) {
				sender.sendMessage("§cUne erreur est survenue, merci de contacter un administrateur.");
				e.printStackTrace();
			}
			return true;
		}
		else if(sender.hasPermission("master")){
			try {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "key give " + p.getName() + " Heaven 15");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "key give " + p.getName() + " Master 10");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lives give " + p.getName() + " 15");
				Bukkit.broadcastMessage("§7[§6§lHeavenMC§7] §6" + p.getName() + " §ea récuperé ses avantages de donateur grâce à la commande §6/reclaim");
				sender.sendMessage("§aTu as reçu tes clés.");
				YamlConfiguration config = new YamlConfiguration();
				config.save(f);
			} catch (IOException e) {
				sender.sendMessage("§cUne erreur est survenue, merci de contacter un administrateur.");
				e.printStackTrace();
			}
			return true;
		}
		else if(sender.hasPermission("hero")){
			try {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "key give " + p.getName() + " Heaven 10");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "key give " + p.getName() + " Master 5");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lives give " + p.getName() + " 10");
				Bukkit.broadcastMessage("§7[§6§lHeavenMC§7] §6" + p.getName() + " §ea récuperé ses avantages de donateur grâce à la commande §6/reclaim");
				sender.sendMessage("§aTu as reçu tes clés.");
				YamlConfiguration config = new YamlConfiguration();
				config.save(f);
			} catch (IOException e) {
				sender.sendMessage("§cUne erreur est survenue, merci de contacter un administrateur.");
				e.printStackTrace();
			}
			return true;
		}
		else if(sender.hasPermission("elite")){
			try {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "key give " + p.getName() + " Heaven 5");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "key give " + p.getName() + " Master 3");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lives give " + p.getName() + " 8");
				Bukkit.broadcastMessage("§7[§6§lHeavenMC§7] §6" + p.getName() + " §ea récuperé ses avantages de donateur grâce à la commande §6/reclaim");
				sender.sendMessage("§aTu as reçu tes clés.");
				YamlConfiguration config = new YamlConfiguration();
				config.save(f);
			} catch (IOException e) {
				sender.sendMessage("§cUne erreur est survenue, merci de contacter un administrateur.");
				e.printStackTrace();
			}
			return true;
		}
		else if(sender.hasPermission("premium")){
			try {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "key give " + p.getName() + " Heaven 1");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "key give " + p.getName() + " Master 1");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lives give " + p.getName() + " 5");
				Bukkit.broadcastMessage("§7[§6§lHeavenMC§7] §6" + p.getName() + " §ea récuperé ses avantages de donateur grâce à la commande §6/reclaim");
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
