package fr.taeron.hcf.faction.argument.staff;

import fr.taeron.hcf.*;
import org.bukkit.command.*;
import org.heavenmc.core.util.command.CommandArgument;
import org.bukkit.*;
import fr.taeron.hcf.faction.struct.*;
import fr.taeron.hcf.faction.type.*;
import fr.taeron.hcf.faction.*;
import java.util.*;

public class FactionForcePromoteArgument extends CommandArgument{
    private final HCF plugin;
    
    public FactionForcePromoteArgument(final HCF plugin) {
        super("forcepromote", "Forces the promotion status of a player.");
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
        if (factionMember.getRole() != Role.MEMBER) {
            sender.sendMessage(ChatColor.RED + factionMember.getName() + " est déjà " + factionMember.getRole().getName() + '.');
            return true;
        }
        factionMember.setRole(Role.CAPTAIN);
        playerFaction.broadcast(ChatColor.GOLD.toString() + ChatColor.BOLD + sender.getName() + " a été promu de force officier de la faction.");
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return (args.length == 2) ? null : Collections.emptyList();
    }
}
