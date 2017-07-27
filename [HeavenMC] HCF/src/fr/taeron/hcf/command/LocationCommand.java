package fr.taeron.hcf.command;

import fr.taeron.hcf.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import fr.taeron.hcf.faction.type.*;
import java.util.*;

public class LocationCommand implements CommandExecutor, TabCompleter
{
    private final HCF plugin;
    
    public LocationCommand(final HCF plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        Player target;
        if (args.length >= 1 && sender.hasPermission(command.getPermission() + ".others")) {
            target = Bukkit.getPlayer(args[0]);
        }
        else {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Utilisation: /" + label + " [joueur]");
                return true;
            }
            target = (Player)sender;
        }
        if (target == null || (sender instanceof Player && !((Player)sender).canSee(target))) {
            sender.sendMessage(ChatColor.GOLD + "Le joueur " + ChatColor.WHITE + args[0] + ChatColor.GOLD + " n'est pas connecté");
            return true;
        }
        final Location location = target.getLocation();
        final Faction factionAt = this.plugin.getFactionManager().getFactionAt(location);
        sender.sendMessage(ChatColor.YELLOW + target.getName() + " est dans le territoire de " + factionAt.getDisplayName(sender) + ChatColor.YELLOW + '(' + (factionAt.isSafezone() ? (ChatColor.GREEN + "Non-Deathban") : (ChatColor.RED + "Deathban")) + ChatColor.YELLOW + ')');
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return (args.length == 1 && sender.hasPermission(command.getPermission() + ".others")) ? null : Collections.emptyList();
    }
}
