package fr.taeron.hcf.faction.argument.staff;

import fr.taeron.hcf.*;
import org.bukkit.command.*;
import fr.taeron.hcf.faction.type.*;
import org.bukkit.entity.*;
import org.heavenmc.core.util.JavaUtils;
import org.heavenmc.core.util.command.CommandArgument;

import java.util.*;
import org.bukkit.*;

public class FactionSetDtrArgument extends CommandArgument{
	
    private final HCF plugin;
    
    public FactionSetDtrArgument(final HCF plugin) {
        super("setdtr", "Sets the DTR of a faction.");
        this.plugin = plugin;
        this.permission = "command.faction.argument." + this.getName();
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <joueur|faction> <valeur>";
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Utilisation: " + this.getUsage(label));
            return true;
        }
        Double newDTR = JavaUtils.tryParseDouble(args[2]);
        if (newDTR == null) {
            sender.sendMessage(ChatColor.RED + "'" + args[2] + "' n'est pas un nombre valide.");
            return true;
        }
        if (args[1].equalsIgnoreCase("all")) {
            for (final Faction faction : this.plugin.getFactionManager().getFactions()) {
                if (faction instanceof PlayerFaction) {
                    ((PlayerFaction)faction).setDeathsUntilRaidable(newDTR);
                }
            }
            Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Le DTR de toutes les factions est désormais de " + newDTR + '.');
            return true;
        }
        final Faction faction2 = this.plugin.getFactionManager().getContainingFaction(args[1]);
        if (faction2 == null) {
        	sender.sendMessage(ChatColor.RED + "La faction (ou le joueur) " + args[1] + " n'existe pas.");
            return true;
        }
        if (!(faction2 instanceof PlayerFaction)) {
            sender.sendMessage(ChatColor.RED + "Tu ne peux pas modifier le DTR d'une faction système.");
            return true;
        }
        final PlayerFaction playerFaction = (PlayerFaction)faction2;
        newDTR = playerFaction.setDeathsUntilRaidable(newDTR);
        Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Le DTR de " + faction2.getName() + " est désormais de " + newDTR + '.');
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
