package fr.taeron.hcf.faction.argument;

import fr.taeron.hcf.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.heavenmc.core.util.command.CommandArgument;
import org.bukkit.*;
import fr.taeron.hcf.faction.*;
import fr.taeron.hcf.faction.struct.*;
import fr.taeron.hcf.faction.type.*;

public class FactionAcceptArgument extends CommandArgument{
	
    private final HCF plugin;
    
    
    public FactionAcceptArgument(final HCF plugin) {
        super("accept", "Accepter une invitation dans une faction", new String[] { "join", "a" });
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <faction>";
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
        if (this.plugin.getFactionManager().getPlayerFaction(player) != null) {
            sender.sendMessage(ChatColor.RED + "Tu es déjà dans une faction.");
            return true;
        }
        final Faction faction = this.plugin.getFactionManager().getContainingFaction(args[1]);
        if (faction == null) {
            sender.sendMessage(ChatColor.RED + "Aucune faction ou joueur portant le nom " + args[1] + " n'as été trouvée.");
            return true;
        }
        if (!(faction instanceof PlayerFaction)) {
            sender.sendMessage(ChatColor.RED + "Tu ne peux pas rejoindre une faction système.");
            return true;
        }
        final PlayerFaction targetFaction = (PlayerFaction)faction;
        if (targetFaction.getMembers().size() >= ConfigurationService.FACTION_PLAYER_LIMIT) {
            sender.sendMessage(faction.getDisplayName(sender) + ChatColor.RED + " est au complet. La limite de joueurs par faction est de " + ConfigurationService.FACTION_PLAYER_LIMIT + '.');
            return true;
        }
        if (!targetFaction.isOpen() && !targetFaction.getInvitedPlayerNames().contains(player.getName())) {
            sender.sendMessage(ChatColor.RED + faction.getDisplayName(sender) + ChatColor.RED + " ne t'as pas invité.");
            return true;
        }
        if (targetFaction.setMember(player, new FactionMember(player, ChatChannel.PUBLIC, Role.MEMBER))) {
            targetFaction.broadcast(Relation.MEMBER.toChatColour() + sender.getName() + ChatColor.YELLOW + " a rejoint la faction.");
        }
        return true;
    }
}
