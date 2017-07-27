package fr.taeron.hcf.eventgame.conquest;

import fr.taeron.hcf.*;
import org.bukkit.command.*;
import org.heavenmc.core.util.JavaUtils;
import org.heavenmc.core.util.command.CommandArgument;
import org.bukkit.*;
import fr.taeron.hcf.eventgame.tracker.*;
import fr.taeron.hcf.eventgame.*;
import fr.taeron.hcf.faction.type.*;

public class ConquestSetpointsArgument extends CommandArgument{
	
    private final HCF plugin;
    
    public ConquestSetpointsArgument(final HCF plugin) {
        super("setpoints", "Sets the points of a faction in the Conquest event", "command.conquest.argument.setpoints");
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <faction> <nombre>";
    }
    
    @SuppressWarnings("deprecation")
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Utilisation: " + this.getUsage(label));
            return true;
        }
        final Faction faction = this.plugin.getFactionManager().getFaction(args[1]);
        if (!(faction instanceof PlayerFaction)) {
            sender.sendMessage(ChatColor.RED + "La faction " + args[1] + " n'existe pas ou n'est pas une faction de joueur.");
            return true;
        }
        final Integer amount = JavaUtils.tryParseInt(args[2]);
        if (amount == null) {
            sender.sendMessage(ChatColor.RED + "'" + args[2] + "' n'est pas un nombre.");
            return true;
        }
        if (amount > 300) {
            sender.sendMessage(ChatColor.RED + "Le nombre de points maximums pour les conquests est de " + 300 + '.');
            return true;
        }
        final PlayerFaction playerFaction = (PlayerFaction)faction;
        ((ConquestTracker)EventType.CONQUEST.getEventTracker()).setPoints(playerFaction, amount);
        Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Tu as défini les points de conquest de " + playerFaction.getName() + " en " + amount + '.');
        return true;
    }
}
