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

public class FactionPromoteArgument extends CommandArgument{
	
    private final HCF plugin;
    
    public FactionPromoteArgument(final HCF plugin) {
        super("promote", "Promotes a player to a captain.");
        this.plugin = plugin;
        this.aliases = new String[] { "captain", "officer", "mod", "moderator", "promote" };
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <joueur>";
    }
    
    @SuppressWarnings("deprecation")
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can set faction captains.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Utilisation: " + this.getUsage(label));
            return true;
        }
        final Player player = (Player)sender;
        final UUID uuid = player.getUniqueId();
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(uuid);
        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "Tu n'as pas de faction.");
            return true;
        }
        if (playerFaction.getMember(uuid).getRole() != Role.LEADER) {
            sender.sendMessage(ChatColor.RED + "Seul le chef de la faction peut faire ça.");
            return true;
        }
        final FactionMember targetMember = playerFaction.getMember(args[1]);
        if (targetMember == null) {
            sender.sendMessage(ChatColor.RED + "Ce joueur n'est pas dans ta faction.");
            return true;
        }
        if (targetMember.getRole() != Role.MEMBER) {
            sender.sendMessage(targetMember.getName() + " est déjà " + targetMember.getRole().getName() + '.');
            return true;
        }
        final Role role = Role.CAPTAIN;
        targetMember.setRole(role);
        playerFaction.broadcast(Relation.MEMBER.toChatColour() + role.getAstrix() + targetMember.getName() + ChatColor.YELLOW + " a été promu officier de faction.");
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
        for (final Map.Entry<UUID, FactionMember> entry : playerFaction.getMembers().entrySet()) {
            if (entry.getValue().getRole() == Role.MEMBER) {
                final OfflinePlayer target = Bukkit.getOfflinePlayer((UUID)entry.getKey());
                final String targetName = target.getName();
                if (targetName == null) {
                    continue;
                }
                results.add(targetName);
            }
        }
        return results;
    }
}
