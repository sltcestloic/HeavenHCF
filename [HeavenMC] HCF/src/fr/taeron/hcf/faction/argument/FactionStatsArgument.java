package fr.taeron.hcf.faction.argument;

import fr.taeron.hcf.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.heavenmc.core.util.command.CommandArgument;
import org.bukkit.*;
import fr.taeron.hcf.faction.type.*;

public class FactionStatsArgument extends CommandArgument{
	
    private final HCF plugin;
    
    public FactionStatsArgument(final HCF plugin) {
        super("stats", "Get details about a faction.");
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " [playerName|factionName]";
    }
    
    @SuppressWarnings("deprecation")
	public boolean onCommand(final CommandSender cs, final Command cmd, final String s, final String[] args) {
        Faction playerFaction = null;
        Faction namedFaction;
        if (args.length < 2) {
            if (!(cs instanceof Player)) {
                cs.sendMessage(ChatColor.RED + "Utilisation: " + this.getUsage(s));
                return true;
            }
            namedFaction = this.plugin.getFactionManager().getPlayerFaction(((Player)cs).getUniqueId());
            if (namedFaction == null) {
                cs.sendMessage(ChatColor.RED + "Tu n'as pas de faction.");
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
                cs.sendMessage(ChatColor.RED + "La faction (ou le joueur) " + args[1] + " n'existe pas.");
                return true;
            }
        }
        if (namedFaction != null) {
            namedFaction.printDetails(cs);
        }
        if (playerFaction != null && (namedFaction == null || !namedFaction.equals(playerFaction))) {
            playerFaction.printDetails(cs);
        }
        return false;
    }
}
