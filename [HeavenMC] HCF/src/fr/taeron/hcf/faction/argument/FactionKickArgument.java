package fr.taeron.hcf.faction.argument;

import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.heavenmc.core.util.command.CommandArgument;

import fr.taeron.hcf.faction.struct.*;
import fr.taeron.hcf.faction.*;
import fr.taeron.hcf.*;
import fr.taeron.hcf.faction.type.*;
import java.util.*;
import org.bukkit.*;

public class FactionKickArgument extends CommandArgument{
	
    private final HCF plugin;
    
    public FactionKickArgument(final HCF plugin) {
        super("kick", "Kick a player from the faction.");
        this.plugin = plugin;
        this.aliases = new String[] { "kickmember", "kickplayer" };
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <playerName>";
    }
    
    @SuppressWarnings("deprecation")
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can kick from a faction.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Utilisation: " + this.getUsage(label));
            return true;
        }
        final Player player = (Player)sender;
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "Tu n'as pas de factions.");
            return true;
        }
        if (playerFaction.isRaidable() && !this.plugin.getEotwHandler().isEndOfTheWorld()) {
            sender.sendMessage(ChatColor.RED + "Tu ne peux pas exclure de joueur car ta faction est raidable.");
            return true;
        }
        final FactionMember targetMember = playerFaction.getMember(args[1]);
        if (targetMember == null) {
            sender.sendMessage(ChatColor.RED + "Ce joueur n'est pas dans ta faction.");
            return true;
        }
        final Role selfRole = playerFaction.getMember(player.getUniqueId()).getRole();
        if (selfRole == Role.MEMBER) {
            sender.sendMessage(ChatColor.RED + "Seul les officiers de faction peuvent expulser les autres membres.");
            return true;
        }
        final Role targetRole = targetMember.getRole();
        if (targetRole == Role.LEADER) {
            sender.sendMessage(ChatColor.RED + "Tu ne peux pas expulser le chef de la faction.");
            return true;
        }
        if (targetRole == Role.CAPTAIN && selfRole == Role.CAPTAIN) {
            sender.sendMessage(ChatColor.RED + "Seul le chef de faction peut expulser les officiers.");
            return true;
        }
        if (playerFaction.setMember(targetMember.getUniqueId(), null, true)) {
            final Player onlineTarget = targetMember.toOnlinePlayer();
            if (onlineTarget != null) {
                onlineTarget.sendMessage(ChatColor.RED.toString() + "Tu as été expulsé de la faction " + playerFaction.getName() + '.');
            }
            playerFaction.broadcast(ConfigurationService.ENEMY_COLOUR + targetMember.getName() + ChatColor.YELLOW + " a été expulsé de la faction par " + ConfigurationService.TEAMMATE_COLOUR + playerFaction.getMember(player).getRole().getAstrix() + sender.getName() + ChatColor.YELLOW + '.');
        }
        return true;
    }
    
    @SuppressWarnings("deprecation")
	public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length != 2 || !(sender instanceof Player)) {
            return Collections.emptyList();
        }
        final Player player = (Player)sender;
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            return Collections.emptyList();
        }
        final Role memberRole = playerFaction.getMember(player.getUniqueId()).getRole();
        if (memberRole == Role.MEMBER) {
            return Collections.emptyList();
        }
        final List<String> results = new ArrayList<String>();
        for (final UUID entry : playerFaction.getMembers().keySet()) {
            final Role targetRole = playerFaction.getMember(entry).getRole();
            if (targetRole != Role.LEADER) {
                if (targetRole == Role.CAPTAIN && memberRole != Role.LEADER) {
                    continue;
                }
                final OfflinePlayer target = Bukkit.getOfflinePlayer(entry);
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
