package fr.taeron.hcf.faction.argument.staff;

import fr.taeron.hcf.*;
import org.bukkit.command.*;
import org.heavenmc.core.util.command.CommandArgument;
import org.bukkit.*;
import fr.taeron.hcf.faction.struct.*;
import fr.taeron.hcf.faction.*;
import fr.taeron.hcf.faction.type.*;
import java.util.*;

public class FactionForceKickArgument extends CommandArgument{
	
    private final HCF plugin;
    
    public FactionForceKickArgument(final HCF plugin) {
        super("forcekick", "Forcefully kick a player from their faction.");
        this.plugin = plugin;
        this.permission = "hcf.command.faction.argument." + this.getName();
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
            sender.sendMessage(ChatColor.RED + "Le joueur " + args[1] + " n'existe pas.");
            return true;
        }
        final FactionMember factionMember = playerFaction.getMember(args[1]);
        if (factionMember == null) {
            sender.sendMessage(ChatColor.RED + "Le joueur " + args[1] + " n'existe pas.");
            return true;
        }
        if (factionMember.getRole() == Role.LEADER) {
            sender.sendMessage(ChatColor.RED + "Tu ne peux pas exclure un chef de faction.");
            return true;
        }
        if (playerFaction.setMember(factionMember.getUniqueId(), null, true)) {
            playerFaction.broadcast(ChatColor.GOLD.toString() + ChatColor.BOLD + factionMember.getName() + " a été expulsé de force de la faction par " + sender.getName() + '.');
        }
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return (args.length == 2) ? null : Collections.emptyList();
    }
}
