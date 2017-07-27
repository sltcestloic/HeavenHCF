package fr.taeron.hcf.faction.argument;

import fr.taeron.hcf.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.heavenmc.core.util.command.CommandArgument;
import org.bukkit.*;
import fr.taeron.hcf.faction.type.*;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;

import java.util.*;

public class FactionInvitesArgument extends CommandArgument{
	
    private final HCF plugin;
    
    public FactionInvitesArgument(final HCF plugin) {
        super("invites", "View faction invitations.");
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName();
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can have faction invites.");
            return true;
        }
        final List<String> receivedInvites = new ArrayList<String>();
        for (final Faction faction : this.plugin.getFactionManager().getFactions()) {
            if (faction instanceof PlayerFaction) {
                final PlayerFaction targetPlayerFaction = (PlayerFaction)faction;
                if (!targetPlayerFaction.getInvitedPlayerNames().contains(sender.getName())) {
                    continue;
                }
                receivedInvites.add(targetPlayerFaction.getDisplayName(sender));
            }
        }
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(((Player)sender).getUniqueId());
        final String delimiter = ChatColor.WHITE + ", " + ChatColor.GRAY;
        if (playerFaction != null) {
            final Set<String> sentInvites = playerFaction.getInvitedPlayerNames();
            sender.sendMessage(ChatColor.YELLOW + "Envoyé par " + playerFaction.getDisplayName(sender) + ChatColor.YELLOW + " (" + sentInvites.size() + ')' + ChatColor.YELLOW + ": " + ChatColor.GRAY + (sentInvites.isEmpty() ? "Ta faction n'a invité personne." : (StringUtils.join(sentInvites, delimiter) + '.')));
        }
        sender.sendMessage(ChatColor.YELLOW + "Invitations (" + receivedInvites.size() + ')' + ChatColor.YELLOW + ": " + ChatColor.GRAY + (receivedInvites.isEmpty() ? "Tu n'as pas d'invitations." : (StringUtils.join(receivedInvites, ChatColor.WHITE + delimiter) + '.')));
        return true;
    }
}
