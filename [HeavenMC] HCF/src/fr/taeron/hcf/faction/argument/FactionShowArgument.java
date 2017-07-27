package fr.taeron.hcf.faction.argument;

import fr.taeron.hcf.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.heavenmc.core.util.command.CommandArgument;
import org.bukkit.*;
import fr.taeron.hcf.faction.type.*;
import java.util.*;

public class FactionShowArgument extends CommandArgument{	
	
    private final HCF plugin;    
    
    public FactionShowArgument(final HCF plugin) {
        super("show", "Get details about a faction.", new String[] { "i", "info", "who" });
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " [joueur|faction]";
    }
    
    @SuppressWarnings("deprecation")
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        Faction playerFaction = null;
        Faction namedFaction;
        if (args.length < 2) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Utilisation: " + this.getUsage(label));
                return true;
            }
            namedFaction = this.plugin.getFactionManager().getPlayerFaction(((Player)sender).getUniqueId());
            if (namedFaction == null) {
                sender.sendMessage(ChatColor.RED + "Tu n'as pas de faction.");
                return true;
            }
        }
        else {
            namedFaction = this.plugin.getFactionManager().getFaction(args[1]);
            playerFaction = this.plugin.getFactionManager().getFaction(args[1]);
            if (Bukkit.getPlayer(args[1]) != null) {
                playerFaction = this.plugin.getFactionManager().getPlayerFaction(Bukkit.getPlayer(args[1]));
            }
            else if (Bukkit.getOfflinePlayer(args[1]).hasPlayedBefore()) {
                playerFaction = this.plugin.getFactionManager().getPlayerFaction(Bukkit.getOfflinePlayer(args[1]).getUniqueId());
            }
            if (namedFaction == null && playerFaction == null) {
                sender.sendMessage(ChatColor.RED + "Impossible de trouver un joueur ou une faction avec le nom " + args[1]);
                return true;
            }
        }
        if (namedFaction != null) {
            namedFaction.printDetails(sender);
        }
        if (playerFaction != null && (namedFaction == null || !namedFaction.equals(playerFaction))) {
            playerFaction.printDetails(sender);
        }
        return true;
    }
    
    @SuppressWarnings("deprecation")
	public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length != 2 || !(sender instanceof Player)) {
            return Collections.emptyList();
        }
        if (args[1].isEmpty()) {
            return null;
        }
        final Player player = (Player)sender;
        final List<String> results = new ArrayList<String>(this.plugin.getFactionManager().getFactionNameMap().keySet());
        for (final Player target : Bukkit.getOnlinePlayers()) {
            if (player.canSee(target) && !results.contains(target.getName())) {
                results.add(target.getName());
            }
        }
        return results;
    }
}
