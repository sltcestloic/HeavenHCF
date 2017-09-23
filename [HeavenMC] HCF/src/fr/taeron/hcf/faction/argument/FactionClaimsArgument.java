package fr.taeron.hcf.faction.argument;

import fr.taeron.hcf.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.heavenmc.core.util.command.CommandArgument;

import fr.taeron.hcf.faction.claim.*;
import fr.taeron.hcf.faction.type.*;
import java.util.*;
import org.bukkit.*;

public class FactionClaimsArgument extends CommandArgument{
	
    private final HCF plugin;
    
    public FactionClaimsArgument(final HCF plugin) {
        super("claims", "View all claims for a faction.");
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " [faction]";
    }
    
    @SuppressWarnings("deprecation")
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        final PlayerFaction selfFaction = (sender instanceof Player) ? this.plugin.getFactionManager().getPlayerFaction((Player)sender) : null;
        ClaimableFaction targetFaction;
        if (args.length < 2) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Utilisation: " + this.getUsage(label));
                return true;
            }
            if (selfFaction == null) {
                sender.sendMessage(ChatColor.RED + "Tu n'as pas de faction.");
                return true;
            }
            targetFaction = selfFaction;
        }
        else {
            final Faction faction = this.plugin.getFactionManager().getContainingFaction(args[1]);
            if (faction == null) {
            	sender.sendMessage(ChatColor.RED + "Aucune faction ou joueur portant le nom " + args[1] + " n'as été trouvée.");
                return true;
            }
            if (!(faction instanceof ClaimableFaction)) {
                sender.sendMessage(ChatColor.RED + "Tu ne peux pas voir les claims de cette faction car elle n'est pas claimable.");
                return true;
            }
            targetFaction = (ClaimableFaction)faction;
        }
        final Collection<Claim> claims = targetFaction.getClaims();
        if (claims.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "La faction " + targetFaction.getDisplayName(sender) + ChatColor.RED + " n'as aucun claim.");
            return true;
        }
        if (sender instanceof Player && !sender.isOp() && targetFaction instanceof PlayerFaction && ((PlayerFaction)targetFaction).getHome() == null && (selfFaction == null || !selfFaction.equals(targetFaction))) {
            sender.sendMessage(ChatColor.RED + "Tu ne peux pas voir les claims de " + targetFaction.getDisplayName(sender) + ChatColor.RED + " car leur home n'est pas défini.");
            return true;
        }
        sender.sendMessage(ChatColor.YELLOW + "Claims de " + targetFaction.getDisplayName(sender));
        for (final Claim claim : claims) {
            sender.sendMessage(ChatColor.GRAY + " " + claim.getFormattedName());
        }
        return true;
    }
    
	public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length != 2 || !(sender instanceof Player)) {
            return Collections.emptyList();
        }
        if (args[1].isEmpty()) {
            return null;
        }
        final Player player = (Player)sender;
        final List<String> results = new ArrayList<String>(this.plugin.getFactionManager().getFactionNameMap().keySet());
        for (final Player target : Bukkit.getOnlinePlayers()) {
            if (player.canSee(target) && !results.contains(target.getName())) {
                results.add(target.getName());
            }
        }
        return results;
    }
}
