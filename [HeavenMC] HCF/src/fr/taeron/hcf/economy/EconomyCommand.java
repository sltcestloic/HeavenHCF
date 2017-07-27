package fr.taeron.hcf.economy;

import com.google.common.collect.*;
import fr.taeron.hcf.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.heavenmc.core.BaseConstants;
import org.heavenmc.core.util.BukkitUtils;
import org.heavenmc.core.util.JavaUtils;
import org.bukkit.*;
import java.util.*;

public class EconomyCommand implements CommandExecutor{
	
    static final ImmutableList<String> COMPLETIONS_SECOND;
    private final HCF plugin;
    
    public EconomyCommand(final HCF plugin) {
        this.plugin = plugin;
    }
    
    @SuppressWarnings("deprecation")
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        final boolean hasStaffPermission = sender.hasPermission(command.getPermission() + ".staff");
        OfflinePlayer target;
        if (args.length > 0 && hasStaffPermission) {
            target = BukkitUtils.offlinePlayerWithNameOrUUID(args[0]);
        }
        else {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Utilisation: /" + label + " <joueur>");
                return true;
            }
            target = (OfflinePlayer)sender;
        }
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, args[0]));
            return true;
        }
        final UUID uuid = target.getUniqueId();
        final int balance = this.plugin.getEconomyManager().getBalance(uuid);
        if (args.length < 2 || !hasStaffPermission) {
            sender.sendMessage(ChatColor.YELLOW + (sender.equals(target) ? "Tu as actuellement " : (target.getName()) + "a actuellement ") + ChatColor.GREEN + '$' + balance);
            return true;
        }
        if (args[1].equalsIgnoreCase("give") || args[1].equalsIgnoreCase("add")) {
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Utilisation: /" + label + ' ' + target.getName() + ' ' + args[1] + " <somme>");
                return true;
            }
            final Integer amount = JavaUtils.tryParseInt(args[2]);
            if (amount == null) {
                sender.sendMessage(ChatColor.RED + "'" + args[2] + "' n'est pas un nombre valide.");
                return true;
            }
            final int newBalance = this.plugin.getEconomyManager().addBalance(uuid, amount);
            sender.sendMessage(new String[] { ChatColor.YELLOW + "Don de " + '$' + JavaUtils.format((Number)amount) + " à " + target.getName() + '.', ChatColor.YELLOW + target.getName() + " possède désormais " + '$' + newBalance + '.' });
            return true;
        }
        else if (args[1].equalsIgnoreCase("take") || args[1].equalsIgnoreCase("negate") || args[1].equalsIgnoreCase("minus") || args[1].equalsIgnoreCase("subtract")) {
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Utilisation: /" + label + ' ' + target.getName() + ' ' + args[1] + " <somme>");
                return true;
            }
            final Integer amount = JavaUtils.tryParseInt(args[2]);
            if (amount == null) {
                sender.sendMessage(ChatColor.RED + "'" + args[2] + "' n'est pas un nombre valide.");
                return true;
            }
            final int newBalance = this.plugin.getEconomyManager().subtractBalance(uuid, amount);
            sender.sendMessage(new String[] { ChatColor.YELLOW + "Tu as pris " + '$' + JavaUtils.format((Number)amount) + " à " + target.getName() + '.', ChatColor.YELLOW + target.getName() + " possède désormais " + '$' + newBalance + '.' });
            return true;
        }
        else {
            if (!args[1].equalsIgnoreCase("set")) {
                sender.sendMessage(ChatColor.GOLD + (sender.equals(target) ? "Tu possèdes" : (target.getName() + " possède ")) + ChatColor.WHITE + '$' + balance + ChatColor.GOLD + '.');
                return true;
            }
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Utilisation: /" + label + ' ' + target.getName() + ' ' + args[1] + " <somme>");
                return true;
            }
            final Integer amount = JavaUtils.tryParseInt(args[2]);
            if (amount == null) {
                sender.sendMessage(ChatColor.RED + "'" + args[2] + "' n'est pas un nombre valide.");
                return true;
            }
            final int newBalance = this.plugin.getEconomyManager().setBalance(uuid, amount);
            sender.sendMessage(ChatColor.YELLOW + target.getName() + " possède désormais " + '$' + JavaUtils.format((Number)newBalance) + '.');
            return true;
        }
    }
    
    static {
        COMPLETIONS_SECOND = ImmutableList.of("add", "set", "take");
    }
}
