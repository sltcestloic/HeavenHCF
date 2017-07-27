package fr.taeron.hcf.faction.argument;

import fr.taeron.hcf.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import fr.taeron.hcf.faction.struct.*;
import fr.taeron.hcf.faction.type.*;
import fr.taeron.hcf.faction.event.*;
import org.bukkit.*;
import org.bukkit.event.*;
import org.heavenmc.core.util.command.CommandArgument;

import java.util.*;
import java.util.stream.*;
import com.google.common.collect.*;

public class FactionUnallyArgument extends CommandArgument{
	
    private static ImmutableList<String> COMPLETIONS;
    private final HCF plugin;
    
    public FactionUnallyArgument(final HCF plugin) {
        super("unally", "Remove an ally pact with other factions.");
        this.plugin = plugin;
        this.aliases = new String[] { "unalliance", "neutral" };
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <all|factionName>";
    }
    
    @SuppressWarnings("deprecation")
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
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "Tu n'as pas de faction.");
            return true;
        }
        if (playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
            sender.sendMessage(ChatColor.RED + "Tu dois être officier de faction pour faire ça.");
            return true;
        }
        final Relation relation = Relation.ALLY;
        final Collection<PlayerFaction> targetFactions = new HashSet<PlayerFaction>();
        if (args[1].equalsIgnoreCase("all")) {
            final Collection<PlayerFaction> allies = playerFaction.getAlliedFactions();
            if (allies.isEmpty()) {
                sender.sendMessage(ChatColor.RED + "Ta faction n'as pas d'alliés.");
                return true;
            }
            targetFactions.addAll(allies);
        }
        else {
            final Faction searchedFaction = this.plugin.getFactionManager().getContainingFaction(args[1]);
            if (!(searchedFaction instanceof PlayerFaction)) {
            	sender.sendMessage(ChatColor.RED + "La faction (ou le joueur) " + args[1] + " n'existe pas.");
                return true;
            }
            targetFactions.add((PlayerFaction)searchedFaction);
        }
        for (final PlayerFaction targetFaction : targetFactions) {
            if (playerFaction.getRelations().remove(targetFaction.getUniqueID()) == null || targetFaction.getRelations().remove(playerFaction.getUniqueID()) == null) {
                sender.sendMessage(ChatColor.RED + "Ta faction n'est pas " + relation.getDisplayName() + ChatColor.RED + " avec " + targetFaction.getDisplayName(playerFaction) + ChatColor.RED + '.');
                return true;
            }
            final FactionRelationRemoveEvent event = new FactionRelationRemoveEvent(playerFaction, targetFaction, Relation.ALLY);
            Bukkit.getPluginManager().callEvent((Event)event);
            if (event.isCancelled()) {
                sender.sendMessage(ChatColor.RED + "Could not drop " + relation.getDisplayName() + " with " + targetFaction.getDisplayName(playerFaction) + ChatColor.RED + ".");
                return true;
            }
            playerFaction.broadcast(ChatColor.YELLOW + "Ta faction n'est plus " + relation.getDisplayName() + ChatColor.YELLOW + " avec " + targetFaction.getDisplayName(playerFaction) + ChatColor.YELLOW + '.');
            targetFaction.broadcast(ChatColor.YELLOW + playerFaction.getDisplayName(targetFaction) + ChatColor.YELLOW + " n'est plus " + relation.getDisplayName() + ChatColor.YELLOW + " avec ta faction.");
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
        return (List<String>)Lists.newArrayList(Iterables.concat(FactionUnallyArgument.COMPLETIONS, playerFaction.getAlliedFactions().stream().map(Faction::getName).collect(Collectors.toList())));
    }
}
