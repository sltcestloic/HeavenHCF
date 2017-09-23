package fr.taeron.hcf.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CoordsCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		sender.sendMessage("§7§l§m-------------------------------------------");
		sender.sendMessage("§6* §eSpawn§7: §f0, 0");
		sender.sendMessage("§6* §eNether Spawn§7: §f0, 0");
		sender.sendMessage(" ");
		sender.sendMessage("§eEvent Coords:");
		sender.sendMessage("§6* §eCitadel§7: §f500, 500");
		sender.sendMessage("§6* §eVillage§7: §f-500, 500");
		sender.sendMessage("§6* §eNemo§7: §f500, -500");
		sender.sendMessage("§6* §eMoutain§7: §f-500, -500");
		sender.sendMessage(" ");
		sender.sendMessage("§eOther Coords:");
		sender.sendMessage("§6* §eEndPortal§7: §f1000, 1000 (tous les cadrants)");
		sender.sendMessage("§6* §eEndExit: §f: 0, 75, 300 (South Road)");
		sender.sendMessage("§6* §eGlowstone Mountain: §f: 0, 60, 290 (Nether, sur la gauche de la route)");
		sender.sendMessage("§7§l§m-------------------------------------------");
		return false;
	}
}
