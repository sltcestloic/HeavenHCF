package fr.taeron.hcf.faction.argument.staff;

import com.google.common.collect.*;
import fr.taeron.hcf.*;
import org.bukkit.plugin.*;
import org.heavenmc.core.util.command.CommandArgument;

import fr.taeron.hcf.user.*;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;

import org.bukkit.event.*;
import fr.taeron.hcf.faction.event.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import fr.taeron.hcf.faction.type.*;
import org.bukkit.command.*;
import java.util.*;

public class FactionChatSpyArgument extends CommandArgument implements Listener{
	
    private static final UUID ALL_UUID;
    private static final ImmutableList<String> COMPLETIONS;
    private final HCF plugin;
    
    public FactionChatSpyArgument(final HCF plugin) {
        super("chatspy", "Spy on the chat of a faction.");
        this.plugin = plugin;
        this.aliases = new String[] { "cs" };
        this.permission = "command.faction.argument." + this.getName();
        plugin.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)plugin);
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <" + StringUtils.join(FactionChatSpyArgument.COMPLETIONS, '|') + "> [factionName]";
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFactionRemove(final FactionRemoveEvent event) {
        if (event.getFaction() instanceof PlayerFaction) {
            final UUID factionUUID = event.getFaction().getUniqueID();
            for (final FactionUser user : this.plugin.getUserManager().getUsers().values()) {
                user.getFactionChatSpying().remove(factionUUID);
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFactionChat(final FactionChatEvent event) {
        final Player player = event.getPlayer();
        final Faction faction = event.getFaction();
        final String format = ChatColor.GOLD + "[" + ChatColor.RED + event.getChatChannel().getDisplayName() + ": " + ChatColor.YELLOW + faction.getName() + ChatColor.GOLD + "] " + ChatColor.GRAY + event.getFactionMember().getRole().getAstrix() + player.getName() + ": " + ChatColor.YELLOW + event.getMessage();
        final HashSet<Player> recipients = new HashSet<Player>();
        recipients.removeAll(event.getRecipients());
        for (final CommandSender recipient : recipients) {
            if (!(recipient instanceof Player)) {
                continue;
            }
            final Player target = (Player)recipient;
            final FactionUser user = event.isAsynchronous() ? this.plugin.getUserManager().getUserAsync(target.getUniqueId()) : this.plugin.getUserManager().getUser(player.getUniqueId());
            final Collection<UUID> spying = user.getFactionChatSpying();
            if (!spying.contains(FactionChatSpyArgument.ALL_UUID) && !spying.contains(faction.getUniqueID())) {
                continue;
            }
            recipient.sendMessage(format);
        }
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Utilisation: " + this.getUsage(label));
            return true;
        }
        final Player player = (Player)sender;
        final Set<UUID> currentSpies = this.plugin.getUserManager().getUser(player.getUniqueId()).getFactionChatSpying();
        if (args[1].equalsIgnoreCase("list")) {
            if (currentSpies.isEmpty()) {
                sender.sendMessage(ChatColor.RED + "Tu n'espionne le chat d'aucune faction.");
                return true;
            }
            sender.sendMessage(ChatColor.GRAY + "Tu espionne actuellement le chat de (" + currentSpies.size() + " factions): " + ChatColor.RED + StringUtils.join(currentSpies, ChatColor.GRAY + ", " + ChatColor.RED) + ChatColor.GRAY + '.');
            return true;
        }
        else if (args[1].equalsIgnoreCase("add")) {
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Utilisation: /" + label + ' ' + args[1].toLowerCase() + " <all|faction|joueur>");
                return true;
            }
            final Faction faction = this.plugin.getFactionManager().getFaction(args[2]);
            if (!(faction instanceof PlayerFaction)) {
                sender.sendMessage(ChatColor.RED + "La faction (ou le joueur) " + args[1] + " n'existe pas.");
                return true;
                
            }
            if (currentSpies.contains(FactionChatSpyArgument.ALL_UUID) || currentSpies.contains(faction.getUniqueID())) {
                sender.sendMessage(ChatColor.RED + "Tu espionnes déjà le chat de " + (args[2].equalsIgnoreCase("all") ? "toutes les factions" : args[2]) + '.');
                return true;
            }
            if (args[2].equalsIgnoreCase("all")) {
                currentSpies.clear();
                currentSpies.add(FactionChatSpyArgument.ALL_UUID);
                sender.sendMessage(ChatColor.GREEN + "Tu espionnes désormais le chat de toutes les factions.");
                return true;
            }
            if (currentSpies.add(faction.getUniqueID())) {
                sender.sendMessage(ChatColor.GREEN + "Tu espionnes désormais le chat de " + faction.getDisplayName(sender) + ChatColor.GREEN + '.');
            }
            else {
                sender.sendMessage(ChatColor.RED + "Tu espionnes déjà le chat de " + faction.getDisplayName(sender) + ChatColor.RED + '.');
            }
            return true;
        }
        else if (args[1].equalsIgnoreCase("del") || args[1].equalsIgnoreCase("delete") || args[1].equalsIgnoreCase("remove")) {
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Utilisation: /" + label + ' ' + args[1].toLowerCase() + " <joueur>");
                return true;
            }
            if (args[2].equalsIgnoreCase("all")) {
                currentSpies.remove(FactionChatSpyArgument.ALL_UUID);
                sender.sendMessage(ChatColor.RED + "Tu n'espionnes plus le chat des factions.");
                return true;
            }
            final Faction faction = this.plugin.getFactionManager().getContainingFaction(args[2]);
            if (faction == null) {
                sender.sendMessage(ChatColor.GOLD + "La faction '" + ChatColor.WHITE + args[2] + ChatColor.GOLD + "' n'existe pas.");
                return true;
            }
            if (currentSpies.remove(faction.getUniqueID())) {
                sender.sendMessage(ChatColor.RED + "Tu n'espionnes plus le chat de " + faction.getDisplayName(sender) + ChatColor.RED + '.');
            }
            else {
                sender.sendMessage(ChatColor.RED + "Tu n'espionnes pas le chat de " + faction.getDisplayName(sender) + ChatColor.RED + '.');
            }
            return true;
        }
        else {
            if (args[1].equalsIgnoreCase("clear")) {
                currentSpies.clear();
                sender.sendMessage(ChatColor.YELLOW + "Tu n'espionnes plus le chat d'aucune faction.");
                return true;
            }
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
    }
    
    static {
        ALL_UUID = UUID.fromString("5a3ed6d1-0239-4e24-b4a9-8cd5b3e5fc72");
        COMPLETIONS = ImmutableList.of("list", "add", "del", "clear");
    }
}
