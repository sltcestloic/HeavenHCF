package fr.taeron.hcf.command;

import org.bukkit.command.*;
import org.bukkit.*;
import java.util.*;

public class HelpCommand implements CommandExecutor, TabCompleter
{
    ChatColor MAIN_COLOR;
    ChatColor SECONDARY_COLOR;
    ChatColor EXTRA_COLOR;
    ChatColor VALUE_COLOR;
    
    public HelpCommand() {
        this.MAIN_COLOR = ChatColor.GOLD;
        this.SECONDARY_COLOR = ChatColor.AQUA;
        this.EXTRA_COLOR = ChatColor.YELLOW; 
        this.VALUE_COLOR = ChatColor.RED;
    }
    
    /* - "§6§l§m---*-----------------------------------*---"
  - "§6§LHeavenMC Help"
  - ""
  - "§6§lCommandes utiles:"
  - "§6* §e/lives §7- Voir les informations sur les vies."
  - "§6* §e/f help §7- Information sur le plugin faction."
  - "§6* §e/coords §7- Coordonnées utiles."
  - "§6* §e/f who (KOTH) §7- Information sur un KOTH."
  - ""
  - "§6§lMap Information:"
  - "§6* §eMap§7: II"
  - "§6* §eTaille de l''overworld§7: 3000"
  - "§6* §eTaille du Nether§7: 1000"
  - "§6* §eLimites d''enchantement§7: /mapkit"
  - "§6* §eDeathban§7: 2h Normal | 1h30 VIP | 1h Pro | 30m Elite."
  - ""
  - "§6§lAutre information:"
  - "§eTeamspeak§7: §fheavenmc.voice.vg"
  - "§eStore§7: §fstore.heavenmc.org"
  - "§eSite§7: §fheavenmc.org"
  - "§6§l§m---*-----------------------------------*---"*/
    
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        sender.sendMessage("§6§l§m---*-----------------------------------*---");
        sender.sendMessage("§6§lAide");
        sender.sendMessage("");
        sender.sendMessage("§6§lCommandes utiles:");
        sender.sendMessage("§6* §e/lives §7- Voir les informations sur les vies.");
        sender.sendMessage("§6* §e/f help §7- Information sur le plugin faction.");
        sender.sendMessage("§6* §e/coords §7- Coordonnées utiles.");
        sender.sendMessage("§6* §e/f who (KOTH) §7- Information sur un KOTH.");
        sender.sendMessage("");
        sender.sendMessage("§6§lMap Information:");
        sender.sendMessage("§6* §eMap§7: I");
        sender.sendMessage("§6* §eTaille de l'overworld§7: 3000");
        sender.sendMessage("§6* §eTaille du Nether§7: 1000");
        sender.sendMessage("§6* §eLimites d''enchantement§7: /mapkit");
        sender.sendMessage("§6* §eDeathban§7: 2h Normal | 1h30 VIP | 1h Pro | 30m Elite.");
        sender.sendMessage("");
        sender.sendMessage("§6§lAutre information:");
        sender.sendMessage("§eTeamspeak§7: §fheavenmc.voice.vg");
        sender.sendMessage("§eStore§7: §fstore.heavenmc.org");
        sender.sendMessage("§eSite§7: §fheavenmc.org");
        sender.sendMessage("§6§l§m---*-----------------------------------*---");
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return Collections.emptyList();
    }
}
