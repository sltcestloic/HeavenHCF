package fr.taeron.hcf.economy;

import fr.taeron.hcf.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.heavenmc.core.util.JavaUtils;
import org.bukkit.*;
import java.util.*;

public class PayCommand implements CommandExecutor, TabCompleter
{
    private final HCF plugin;
    
    public PayCommand(final HCF plugin) {
        this.plugin = plugin;
    }
    
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Utilisation: /" + label + " <joueur> <somme>");
            return true;
        }
        final Integer amount = JavaUtils.tryParseInt(args[1]);
        if (amount == null) {
            sender.sendMessage(ChatColor.RED + "'" + args[1] + "' n'est pas un nombre valide.");
            return true;
        }
        if (amount <= 0) {
            sender.sendMessage(ChatColor.RED + "La quantité doit être positive.");
            return true;
        }
        final Player senderPlayer = (Player)sender;
        final int senderBalance = (senderPlayer != null) ? this.plugin.getEconomyManager().getBalance(senderPlayer.getUniqueId()) : 1024;
        if (senderBalance < amount) {
            sender.sendMessage(ChatColor.RED + "Tu ne peux pas payer " + '$' + amount + ", tu as seulement " + '$' + senderBalance + ".");
            return true;
        }
        final OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (sender.equals(target)) {
            sender.sendMessage(ChatColor.RED + "Tu ne peux pas envoyer de l'argent à toi même.");
            return true;
        }
        final Player targetPlayer = target.getPlayer();
        if (!target.hasPlayedBefore() && targetPlayer == null) {
            sender.sendMessage(ChatColor.GOLD + "Le joueur '" + ChatColor.WHITE + args[0] + ChatColor.GOLD + "' n'est pas connecté.");
            return true;
        }
        if (targetPlayer == null) {
            return false;
        }
        if (senderPlayer != null) {
            this.plugin.getEconomyManager().subtractBalance(senderPlayer.getUniqueId(), amount);
        }
        this.plugin.getEconomyManager().addBalance(targetPlayer.getUniqueId(), amount);
        targetPlayer.sendMessage(ChatColor.YELLOW + sender.getName() + " t'as envoyé " + ChatColor.GOLD + '$' + amount + ChatColor.YELLOW + '.');
        sender.sendMessage(ChatColor.YELLOW + "Tu as envoyé " + ChatColor.GREEN + '$' + amount + ChatColor.YELLOW + " à " + target.getName() + '.');
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return (args.length == 1) ? null : Collections.emptyList();
    }
}
