package fr.taeron.hcf.crate.argument;

import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.heavenmc.core.util.command.CommandArgument;

import fr.taeron.hcf.HCF;

public class KeyBankArgument extends CommandArgument {
	
    private HCF plugin;
    
    public KeyBankArgument(HCF plugin) {
        super("bank", "Voir les keys que tu as dans ta banque");
        this.plugin = plugin;
        this.permission = "command.loot.argument." + this.getName();
    }
    
    public String getUsage(String label) {
        return '/' + label + ' ' + this.getName();
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "La console n'est pas supportée.");
            return true;
        }
        Player player = (Player)sender;
        UUID uuid = player.getUniqueId();
        Map<String, Integer> crateKeyMap = this.plugin.getKeyManager().getDepositedCrateMap(uuid);
        if (crateKeyMap.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Il n'y a pas de clés dans ta banque.");
            return true;
        }
        for (Map.Entry<String, Integer> entry : crateKeyMap.entrySet()) {
            sender.sendMessage(ChatColor.YELLOW + entry.getKey() + ": " + ChatColor.GOLD + entry.getValue());
        }
        return true;
    }
}
