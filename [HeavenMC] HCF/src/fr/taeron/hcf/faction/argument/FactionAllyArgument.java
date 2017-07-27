package fr.taeron.hcf.faction.argument;

import org.bukkit.command.*;
import org.bukkit.entity.*;
import fr.taeron.hcf.faction.struct.*;
import fr.taeron.hcf.faction.type.*;
import fr.taeron.hcf.faction.event.*;
import org.bukkit.*;
import org.bukkit.event.*;
import org.heavenmc.core.util.command.CommandArgument;

import fr.taeron.hcf.*;
import java.util.*;

public class FactionAllyArgument extends CommandArgument{
	
    private static final Relation RELATION;
    
    private final HCF plugin;
    
    public FactionAllyArgument(final HCF plugin) {
        super("ally", "Faire un pacte d'alliance avec une autre faction.", new String[] { "alliance" });
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
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "Tu n'as pas de faction.");
            return true;
        }
        if (playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
            sender.sendMessage(ChatColor.RED + "Tu doit être officier de faction pour faire cela.");
            return true;
        }
        final Faction containingFaction = this.plugin.getFactionManager().getContainingFaction(args[1]);
        if (!(containingFaction instanceof PlayerFaction)) {
        	sender.sendMessage(ChatColor.RED + "Aucune faction ou joueur portant le nom " + args[1] + " n'as été trouvée.");
            return true;
        }
        final PlayerFaction targetFaction = (PlayerFaction)containingFaction;
        if (playerFaction.equals(targetFaction)) {
            sender.sendMessage(ChatColor.RED + "Tu ne peux pas faire de demande d'" + FactionAllyArgument.RELATION.getDisplayName() + ChatColor.RED + "a ta propre faction.");
            return true;
        }
        final Collection<UUID> allied = playerFaction.getAllied();
        if (allied.size() >= 1) {
            sender.sendMessage(ChatColor.RED + "Ta faction a déjà atteint le nombre maximum d'alliés  (" + 1 + ')');
            return true;
        }
        if (targetFaction.getAllied().size() >= 1) {
            sender.sendMessage(targetFaction.getDisplayName(sender) + ChatColor.RED + " a déjà atteint le nombre maximum d'alliés  (" + 1 + ')');
            return true;
        }
        if (allied.contains(targetFaction.getUniqueID())) {
            sender.sendMessage(ChatColor.RED + "Ta faction est déjà en " + FactionAllyArgument.RELATION.getDisplayName() + ChatColor.RED + " avec " + targetFaction.getDisplayName(playerFaction) + ChatColor.RED + '.');
            return true;
        }
        if (targetFaction.getRequestedRelations().remove(playerFaction.getUniqueID()) != null) {
            final FactionRelationCreateEvent event = new FactionRelationCreateEvent(playerFaction, targetFaction, FactionAllyArgument.RELATION);
            Bukkit.getPluginManager().callEvent((Event)event);
            targetFaction.getRelations().put(playerFaction.getUniqueID(), FactionAllyArgument.RELATION);
            targetFaction.broadcast(ChatColor.YELLOW + "Ta faction est désormais en " + FactionAllyArgument.RELATION.getDisplayName() + ChatColor.YELLOW + " avec " + playerFaction.getDisplayName(targetFaction) + ChatColor.YELLOW + '.');
            playerFaction.getRelations().put(targetFaction.getUniqueID(), FactionAllyArgument.RELATION);
            playerFaction.broadcast(ChatColor.YELLOW + "Ta faction est désormais en " + FactionAllyArgument.RELATION.getDisplayName() + ChatColor.YELLOW + " avec " + targetFaction.getDisplayName(playerFaction) + ChatColor.YELLOW + '.');
            return true;
        }
        if (playerFaction.getRequestedRelations().putIfAbsent(targetFaction.getUniqueID(), FactionAllyArgument.RELATION) != null) {
            sender.sendMessage(ChatColor.YELLOW + "Ta faction a déjà fait une demande d'" + FactionAllyArgument.RELATION.getDisplayName() + ChatColor.YELLOW + " à " + targetFaction.getDisplayName(playerFaction) + ChatColor.YELLOW + '.');
            return true;
        }
        playerFaction.broadcast(targetFaction.getDisplayName(playerFaction) + ChatColor.YELLOW + " a été informé de votre demande d'" + FactionAllyArgument.RELATION.getDisplayName() + ChatColor.YELLOW + '.');
        targetFaction.broadcast(playerFaction.getDisplayName(targetFaction) + ChatColor.YELLOW + " a envoyé une demande d'" + FactionAllyArgument.RELATION.getDisplayName() + ChatColor.YELLOW + " a ta faction. Fait " + ConfigurationService.ALLY_COLOUR + "/f " + this.getName() + ' ' + playerFaction.getName() + ChatColor.YELLOW + " pour accepter.");
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
        final List<String> results = new ArrayList<String>();
        return results;
    }
    
    static {
        RELATION = Relation.ALLY;
    }
}
