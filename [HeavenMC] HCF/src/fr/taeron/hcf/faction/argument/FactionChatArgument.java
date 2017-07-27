package fr.taeron.hcf.faction.argument;

import fr.taeron.hcf.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.heavenmc.core.util.command.CommandArgument;
import org.bukkit.*;
import fr.taeron.hcf.faction.struct.*;
import fr.taeron.hcf.faction.type.*;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;

import fr.taeron.hcf.faction.*;
import java.util.*;

public class FactionChatArgument extends CommandArgument{
	
    private final HCF plugin;
    
    public FactionChatArgument(final HCF plugin) {
        super("chat", "Changer de mode de chat.", new String[] { "c" });
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " [fac|public|ally] [message]";
    }
    
    @SuppressWarnings({ "deprecation", "unchecked" })
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "La console n'est pas supportée.");
            return true;
        }
        final Player player = (Player)sender;
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "Tu n'as pas de faction.");
            return true;
        }
        final FactionMember member = playerFaction.getMember(player.getUniqueId());
        final ChatChannel currentChannel = member.getChatChannel();
        final ChatChannel parsed = (args.length >= 2) ? ChatChannel.parse(args[1], null) : currentChannel.getRotation();
        if (parsed == null && currentChannel != ChatChannel.PUBLIC) {
            final Collection<Player> recipients = (Collection<Player>)playerFaction.getOnlinePlayers();
            if (currentChannel == ChatChannel.ALLIANCE) {
                for (final PlayerFaction ally : playerFaction.getAlliedFactions()) {
                    recipients.addAll(ally.getOnlinePlayers());
                }
            }
            final String format = String.format(currentChannel.getRawFormat(player), "", StringUtils.join((Object[])args, ' ', 1, args.length));
            for (final Player recipient : recipients) {
                recipient.sendMessage(format);
            }
            return true;
        }
        final ChatChannel newChannel = (parsed == null) ? currentChannel.getRotation() : parsed;
        member.setChatChannel(newChannel);
        sender.sendMessage(ChatColor.YELLOW + "Tu parles désormais dans le chat " + ChatColor.AQUA + newChannel.getDisplayName().toLowerCase());
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length != 2 || !(sender instanceof Player)) {
            return Collections.emptyList();
        }
        final ChatChannel[] values = ChatChannel.values();
        final List<String> results = new ArrayList<String>(values.length);
        for (final ChatChannel type : values) {
            results.add(type.getName());
        }
        return results;
    }
}
