package fr.taeron.hcf.faction.argument;

import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.heavenmc.core.util.command.CommandArgument;

import fr.taeron.hcf.faction.struct.*;
import fr.taeron.hcf.*;
import fr.taeron.hcf.faction.type.*;
import fr.taeron.hcf.faction.*;
import java.util.*;
import org.bukkit.*;

public class FactionLeaderArgument extends CommandArgument{
	
    private final HCF plugin;
    
    public FactionLeaderArgument(final HCF plugin) {
        super("leader", "Sets the new leader for your faction.");
        this.plugin = plugin;
        this.aliases = new String[] { "setleader", "newleader" };
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <playerName>";
    }
    
    @SuppressWarnings("deprecation")
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can set faction leaders.");
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
        final UUID uuid = player.getUniqueId();
        final FactionMember selfMember = playerFaction.getMember(uuid);
        final Role selfRole = selfMember.getRole();
        if (selfRole != Role.LEADER) {
            sender.sendMessage(ChatColor.RED + "Tu n'es pas chef de la faction.");
            return true;
        }
        final FactionMember targetMember = playerFaction.getMember(args[1]);
        if (targetMember == null) {
            sender.sendMessage(ChatColor.RED + "Ce joueur n'est pas dans ta faction.");
            return true;
        }
        if (targetMember.getUniqueId().equals(uuid)) {
            sender.sendMessage(ChatColor.RED + "Tu es déjà chef de ta faction.");
            return true;
        }
        targetMember.setRole(Role.LEADER);
        selfMember.setRole(Role.CAPTAIN);
        playerFaction.broadcast(ConfigurationService.TEAMMATE_COLOUR + selfMember.getRole().getAstrix() + selfMember.getName() + ChatColor.YELLOW + " a donné le rôle de chef de faction à " + ConfigurationService.TEAMMATE_COLOUR + targetMember.getRole().getAstrix() + targetMember.getName() + ChatColor.YELLOW + '.');
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
        final Map<UUID, FactionMember> members = playerFaction.getMembers();
        for (final Map.Entry<UUID, FactionMember> entry : members.entrySet()) {
            if (entry.getValue().getRole() != Role.LEADER) {
                final OfflinePlayer target = Bukkit.getOfflinePlayer((UUID)entry.getKey());
                final String targetName = target.getName();
                if (targetName == null) {
                    continue;
                }
                if (results.contains(targetName)) {
                    continue;
                }
                results.add(targetName);
            }
        }
        return results;
    }
}
