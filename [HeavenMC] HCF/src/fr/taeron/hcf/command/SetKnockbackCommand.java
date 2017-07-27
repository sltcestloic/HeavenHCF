package fr.taeron.hcf.command;

import java.io.File;
import java.io.IOException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.heavenmc.core.util.JavaUtils;

import net.md_5.bungee.api.ChatColor;

public class SetKnockbackCommand implements CommandExecutor{

	public static double xz;
	public static double y;
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length != 2){
			sender.sendMessage(ChatColor.RED + "Utilisation: /" + label + " <horizontal (x/z)> <vertical (y)>");
			sender.sendMessage(ChatColor.GREEN + "Valeurs actuelles:");
			sender.sendMessage(ChatColor.GRAY + " - " + ChatColor.GREEN + "Horizontal (x/z)" + ChatColor.WHITE + ": " + SetKnockbackCommand.xz);
			sender.sendMessage(ChatColor.GRAY + " - " + ChatColor.GREEN + "Vertical (y)" + ChatColor.WHITE + ": " + SetKnockbackCommand.y);
			return false;
		}
		if(JavaUtils.tryParseDouble(args[0]) == null || JavaUtils.tryParseDouble(args[1]) == null){
			sender.sendMessage(ChatColor.RED + "Valeurs incorectes.");
			return false;
		}
		double xz = JavaUtils.tryParseDouble(args[0]);
		double y = JavaUtils.tryParseDouble(args[1]);
		SetKnockbackCommand.xz = xz;
		SetKnockbackCommand.y = y;
		File f = new File("knockbacks.yml");
		if(!f.exists()){	
			try {
				YamlConfiguration config = new YamlConfiguration();
				config.createSection("xz");
				config.createSection("y");
				config.set("xz", xz);
				config.set("y", y);
				config.save(f);
				sender.sendMessage(ChatColor.GREEN + "Knockback modifié et sauvegardé avec succès:");
				sender.sendMessage(ChatColor.GRAY + " - " + ChatColor.GREEN + "Horizontal (x/z)" + ChatColor.WHITE + ": " + SetKnockbackCommand.xz);
				sender.sendMessage(ChatColor.GRAY + " - " + ChatColor.GREEN + "Vertical (y)" + ChatColor.WHITE + ": " + SetKnockbackCommand.y);
			} catch (IOException e) {
				sender.sendMessage(ChatColor.GREEN + "Knockback modifié, " + ChatColor.RED + "impossible de sauvegarder les valeurs dans le fichier, elles seront donc remises à zéro lors du prochain redémmarage." + ChatColor.GREEN + ":");
				sender.sendMessage(ChatColor.GRAY + " - " + ChatColor.GREEN + "Horizontal (x/z)" + ChatColor.WHITE + ": " + SetKnockbackCommand.xz);
				sender.sendMessage(ChatColor.GRAY + " - " + ChatColor.GREEN + "Vertical (y)" + ChatColor.WHITE + ": " + SetKnockbackCommand.y);
				e.printStackTrace();
			}
		} else {
			try {
				YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
				config.set("xz", xz);
				config.set("y", y);
				config.save(f);
				sender.sendMessage(ChatColor.GREEN + "Knockback modifié et sauvegardé avec succès:");
				sender.sendMessage(ChatColor.GRAY + " - " + ChatColor.GREEN + "Horizontal (x/z)" + ChatColor.WHITE + ": " + SetKnockbackCommand.xz);
				sender.sendMessage(ChatColor.GRAY + " - " + ChatColor.GREEN + "Vertical (y)" + ChatColor.WHITE + ": " + SetKnockbackCommand.y);
			} catch (IOException e) {
				sender.sendMessage(ChatColor.GREEN + "Knockback modifié, " + ChatColor.RED + "impossible de sauvegarder les valeurs dans le fichier, elles seront donc remises à zéro lors du prochain redémmarage." + ChatColor.GREEN + ":");
				sender.sendMessage(ChatColor.GRAY + " - " + ChatColor.GREEN + "Horizontal (x/z)" + ChatColor.WHITE + ": " + SetKnockbackCommand.xz);
				sender.sendMessage(ChatColor.GRAY + " - " + ChatColor.GREEN + "Vertical (y)" + ChatColor.WHITE + ": " + SetKnockbackCommand.y);
				e.printStackTrace();
			}
		}
		return false;
	}
}
