package fr.taeron.hcf.faction.argument.staff;

import fr.taeron.hcf.*;
import org.bukkit.command.*;
import org.heavenmc.core.util.JavaUtils;
import org.heavenmc.core.util.command.CommandArgument;
import org.bukkit.*;
import fr.taeron.hcf.faction.type.*;

public class FactionSetDeathbanMultiplierArgument extends CommandArgument{
	
    private final HCF plugin;
    
    public FactionSetDeathbanMultiplierArgument(final HCF plugin) {
        super("setdeathbanmultiplier", "Sets the deathban multiplier of a faction.");
        this.plugin = plugin;
        this.permission = "command.faction.argument." + this.getName();
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <joueur|faction> <multiplier>";
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Utilisation: " + this.getUsage(label));
            return true;
        }
        final Faction faction = this.plugin.getFactionManager().getContainingFaction(args[1]);
        if (faction == null) {
        	sender.sendMessage(ChatColor.RED + "La faction (ou le joueur) " + args[1] + " n'existe pas.");
            return true;
        }
        final Double multiplier = JavaUtils.tryParseDouble(args[2]);
        if (multiplier == null) {
            sender.sendMessage(ChatColor.RED + "'" + args[2] + "' n'est pas un nombre valide.");
            return true;
        }
        if (multiplier < 0.0) {
            sender.sendMessage(ChatColor.RED + "Multiplier minimum: " + 0.0 + '.');
            return true;
        }
        if (multiplier > 5.0) {
            sender.sendMessage(ChatColor.RED + "Multiplier maximimum: " + 5.0 + '.');
            return true;
        }
        //final double previousMultiplier = faction.getDeathbanMultiplier();
        faction.setDeathbanMultiplier(multiplier);
        Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Le deathban multiplier de " + faction.getName() + " est désormais de " + multiplier + '.');
        return true;
    }
}
