package fr.taeron.hcf.faction.argument;

import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.heavenmc.core.util.command.CommandArgument;

import fr.taeron.hcf.faction.struct.*;
import fr.taeron.hcf.faction.type.*;
import org.bukkit.*;
import fr.taeron.hcf.*;
import java.util.*;

public class FactionFocusArgument extends CommandArgument{
	
    @SuppressWarnings("unused")
	private static final Relation RELATION;
    private final HCF plugin;
    
    public FactionFocusArgument(final HCF plugin) {
        super("focus", "Focus une faction.", new String[] { "foc" });
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
        if(playerFaction.getFocusedFaction() != null && playerFaction.getFocusedFaction() == targetFaction){
        	sender.sendMessage(ChatColor.RED + "Ta faction focus déjà la faction " + ConfigurationService.FOCUS_COLOUR + targetFaction.getName());
        	return true;
        }
        playerFaction.setFocusedFaction(targetFaction);
        playerFaction.broadcast(targetFaction.getDisplayName(playerFaction) + ChatColor.YELLOW + "Ta faction focus désormais la faction " + ConfigurationService.FOCUS_COLOUR + targetFaction.getName());
        return true;
    }
    
    @SuppressWarnings("deprecation")
	public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length != 2 || !(sender instanceof Player)) {
            return Collections.emptyList();
        }
        final Player player = (Player)sender;
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null || playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
            return Collections.emptyList();
        }
        final List<String> results = new ArrayList<String>();
        for (final Player target : Bukkit.getOnlinePlayers()) {
            if (player.canSee(target) && !results.contains(target.getName())) {
                final Faction targetFaction = this.plugin.getFactionManager().getPlayerFaction(target.getUniqueId());
                if (targetFaction != null && targetFaction.equals(playerFaction)) {
                    continue;
                }
                results.add(target.getName());
            }
        }
        return results;
    }
    
    static {
        RELATION = Relation.ALLY;
    }
}
