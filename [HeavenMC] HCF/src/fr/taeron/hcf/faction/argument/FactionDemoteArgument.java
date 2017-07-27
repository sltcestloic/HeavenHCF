package fr.taeron.hcf.faction.argument;

import fr.taeron.hcf.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.heavenmc.core.util.command.CommandArgument;

import fr.taeron.hcf.faction.struct.*;
import fr.taeron.hcf.faction.type.*;
import fr.taeron.hcf.faction.*;
import java.util.*;
import org.bukkit.*;

public class FactionDemoteArgument extends CommandArgument{
	
    private final HCF plugin;
    
    public FactionDemoteArgument(final HCF plugin) {
        super("demote", "Enlever le grade d'un joueur", new String[] { "uncaptain", "delcaptain", "delofficer" });
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <joueur>";
    }
    
    @SuppressWarnings("deprecation")
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "La console n'est pas supportée.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Utilisation: " + this.getUsage(label));
            return true;
        }
        final Player player = (Player)sender;
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "Tu n'as pas de faction.");
            return true;
        }
        if (playerFaction.getMember(player.getUniqueId()).getRole() != Role.LEADER) {
            sender.sendMessage(ChatColor.RED + "Seul le chef de la faction peut expulser les membres.");
            return true;
        }
        final FactionMember targetMember = playerFaction.getMember(args[1]);
        if (targetMember == null) {
            sender.sendMessage(ChatColor.RED + "Ce joueur n'est pas dans ta faction.");
            return true;
        }
        if (targetMember.getRole() != Role.CAPTAIN) {
            sender.sendMessage(ChatColor.RED + "Tu ne peux demote que les capitaines.");
            return true;
        }
        targetMember.setRole(Role.MEMBER);
        playerFaction.broadcast(Relation.MEMBER.toChatColour() + targetMember.getName() + ChatColor.YELLOW + " a été demote de son grade capitaine par.");
        return true;
    }
    
    @SuppressWarnings("deprecation")
	public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length != 2 || !(sender instanceof Player)) {
            return Collections.emptyList();
        }
        final Player player = (Player)sender;
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null || playerFaction.getMember(player.getUniqueId()).getRole() != Role.LEADER) {
            return Collections.emptyList();
        }
        final List<String> results = new ArrayList<String>();
        final Collection<UUID> keySet = playerFaction.getMembers().keySet();
        for (final UUID entry : keySet) {
            final OfflinePlayer target = Bukkit.getOfflinePlayer(entry);
            final String targetName = target.getName();
            if (targetName != null && playerFaction.getMember(target.getUniqueId()).getRole() == Role.CAPTAIN) {
                results.add(targetName);
            }
        }
        return results;
    }
}
