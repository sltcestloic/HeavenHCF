package fr.taeron.hcf.faction.argument.staff;

import fr.taeron.hcf.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import fr.taeron.hcf.faction.claim.*;
import org.bukkit.inventory.*;
import org.heavenmc.core.util.command.CommandArgument;

import fr.taeron.hcf.faction.type.*;
import org.bukkit.*;
import java.util.*;

public class FactionClaimForArgument extends CommandArgument{
	
    private final HCF plugin;
    
    public FactionClaimForArgument(final HCF plugin) {
        super("claimfor", "Claims land for another faction.");
        this.plugin = plugin;
        this.permission = "command.faction.argument." + this.getName();
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <faction>";
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
        final Faction targetFaction = this.plugin.getFactionManager().getFaction(args[1]);
        if (!(targetFaction instanceof ClaimableFaction)) {
            sender.sendMessage(ChatColor.RED + "La faction " + args[1] + " n'existe pas.");
            return true;
        }
        final Player player = (Player)sender;
        final UUID uuid = player.getUniqueId();
        final ClaimSelection claimSelection = (ClaimSelection) this.plugin.getClaimHandler().claimSelectionMap.get(uuid);
        if (claimSelection == null || !claimSelection.hasBothPositionsSet()) {
            player.sendMessage(ChatColor.RED + "Tu n'as pas défini les deux positions du claim.");
            return true;
        }
        if (this.plugin.getClaimHandler().tryPurchasing(player, claimSelection.toClaim(targetFaction))) {
            this.plugin.getClaimHandler().clearClaimSelection(player);
            player.setItemInHand(new ItemStack(Material.AIR, 1));
            sender.sendMessage(ChatColor.YELLOW + "Tu as claim ce territoire pour " + ChatColor.RED + targetFaction.getName() + ChatColor.YELLOW + '.');
        }
        return true;
    }
    
    @SuppressWarnings("deprecation")
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
