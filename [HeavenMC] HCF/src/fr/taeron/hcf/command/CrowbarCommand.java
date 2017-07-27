package fr.taeron.hcf.command;

import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import org.bukkit.inventory.*;
import org.heavenmc.core.util.BukkitUtils;
import org.heavenmc.core.util.JavaUtils;

import com.google.common.base.Optional;

import fr.taeron.hcf.Crowbar;

import java.util.*;

public class CrowbarCommand implements CommandExecutor, TabCompleter
{
    private final List<String> completions;
    
    public CrowbarCommand() {
        this.completions = Arrays.asList("spawn", "setspawners", "setendframes");
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Utilisation: /" + label + " <spawn|setspawners|setendframes>");
            return true;
        }
        final Player player = (Player)sender;
        if (args[0].equalsIgnoreCase("spawn")) {
            final ItemStack stack = new Crowbar().getItemIfPresent();
            player.getInventory().addItem(new ItemStack[] { stack });
            sender.sendMessage(ChatColor.GRAY + "Tu t'es give une " + stack.getItemMeta().getDisplayName() + ChatColor.YELLOW + '.');
            return true;
        }
        final Optional<Crowbar> crowbarOptional = Crowbar.fromStack(player.getItemInHand());
        if (!crowbarOptional.isPresent()) {
            sender.sendMessage(ChatColor.RED + "Tu n'as pas de Crowbar dans la main.");
            return true;
        }
        if (args[0].equalsIgnoreCase("setspawners")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + ' ' + args[0].toLowerCase() + " <amount>");
                return true;
            }
            final Integer amount = JavaUtils.tryParseInt(args[1]);
            if (amount == null) {
                sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a number.");
                return true;
            }
            if (amount < 0) {
                sender.sendMessage(ChatColor.RED + "You cannot set Spawner uses to an amount less than " + 0 + '.');
                return true;
            }
            if (amount > 1) {
                sender.sendMessage(ChatColor.RED + "Crowbars have maximum Spawner uses of " + 1 + '.');
                return true;
            }
            final Crowbar crowbar = (Crowbar)crowbarOptional.get();
            crowbar.setSpawnerUses(amount);
            player.setItemInHand(crowbar.getItemIfPresent());
            sender.sendMessage(ChatColor.YELLOW + "Set Spawner uses of held Crowbar to " + amount + '.');
            return true;
        }
        else {
            if (!args[0].equalsIgnoreCase("setendframes")) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <spawn|setspawners|setendframes>");
                return true;
            }
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + ' ' + args[0].toLowerCase() + " <amount>");
                return true;
            }
            final Integer amount = JavaUtils.tryParseInt(args[1]);
            if (amount == null) {
                sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a number.");
                return true;
            }
            if (amount < 0) {
                sender.sendMessage(ChatColor.RED + "You cannot set End Frame uses to an amount less than " + 0 + '.');
                return true;
            }
            if (amount > 5) {
                sender.sendMessage(ChatColor.RED + "Crowbars have maximum End Frame uses of " + 1 + '.');
                return true;
            }
            final Crowbar crowbar = (Crowbar)crowbarOptional.get();
            crowbar.setEndFrameUses(amount);
            player.setItemInHand(crowbar.getItemIfPresent());
            sender.sendMessage(ChatColor.YELLOW + "Set End Frame uses of held Crowbar to " + amount + '.');
            return true;
        }
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return (List<String>) ((args.length == 1) ? BukkitUtils.getCompletions(args, (List)this.completions) : Collections.emptyList());
    }
}
