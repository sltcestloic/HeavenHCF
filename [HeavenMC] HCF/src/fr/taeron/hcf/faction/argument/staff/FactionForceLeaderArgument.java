package fr.taeron.hcf.faction.argument.staff;

import fr.taeron.hcf.*;
import org.bukkit.command.*;
import org.heavenmc.core.util.command.CommandArgument;
import org.bukkit.*;
import fr.taeron.hcf.faction.struct.*;
import fr.taeron.hcf.faction.type.*;
import fr.taeron.hcf.faction.*;
import java.util.*;

public class FactionForceLeaderArgument extends CommandArgument{
	
    private final HCF plugin;
    
    public FactionForceLeaderArgument(final HCF plugin) {
        super("forceleader", "Forces the leader of a faction.");
        this.plugin = plugin;
        this.permission = "command.faction.argument." + this.getName();
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <joueur>";
    }
    
    @SuppressWarnings("deprecation")
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Utilisation: " + this.getUsage(label));
            return true;
        }
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getContainingPlayerFaction(args[1]);
        if (playerFaction == null) {
        	sender.sendMessage(ChatColor.RED + "La faction (ou le joueur) " + args[1] + " n'existe pas.");
        	return true;
        }
        final FactionMember factionMember = playerFaction.getMember(args[1]);
        if (factionMember == null) {
            sender.sendMessage(ChatColor.RED + "La faction (ou le joueur) " + args[1] + " n'existe pas.");
            return true;
        }
        if (factionMember.getRole() == Role.LEADER) {
            sender.sendMessage(ChatColor.RED + factionMember.getName() + " est déjà chef de " + playerFaction.getDisplayName(sender) + ChatColor.RED + '.');
            return true;
        }
        final FactionMember leader = playerFaction.getLeader();
        final String oldLeaderName = (leader == null) ? "none" : leader.getName();
        final String newLeaderName = factionMember.getName();
        if (leader != null) {
            leader.setRole(Role.CAPTAIN);
        }
        factionMember.setRole(Role.LEADER);
        playerFaction.broadcast(ChatColor.YELLOW + sender.getName() + " a donné le rôle de chef de la faction à " + newLeaderName + '.');
        sender.sendMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "Le chef de " + playerFaction.getName() + " n'est désormais plus " + oldLeaderName + " mais " + newLeaderName + '.');
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return (args.length == 2) ? null : Collections.emptyList();
    }
}
