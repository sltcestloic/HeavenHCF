package fr.taeron.hcf.eventgame.argument;

import fr.taeron.hcf.*;
import org.bukkit.command.*;
import org.heavenmc.core.util.command.CommandArgument;
import org.bukkit.*;
import fr.taeron.hcf.eventgame.faction.*;
import fr.taeron.hcf.faction.type.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class EventStartArgument extends CommandArgument{
	
    private final HCF plugin;
    
    public EventStartArgument(final HCF plugin) {
        super("start", "Démarrer un event");
        this.plugin = plugin;
        this.aliases = new String[] { "begin" };
        this.permission = "command.event.argument." + this.getName();
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <event>";
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Utilisation: " + this.getUsage(label));
            return true;
        }
        final Faction faction = this.plugin.getFactionManager().getFaction(args[1]);
        if (!(faction instanceof EventFaction)) {
            sender.sendMessage(ChatColor.RED + "Il n'y a pas d'event avec le nom '" + args[1] + "'.");
            return true;
        }
        if (this.plugin.getTimerManager().eventTimer.tryContesting((EventFaction)faction, sender)) {
            sender.sendMessage(ChatColor.YELLOW + "Tu as lancé l'event " + faction.getName() + '.');
        }
        return true;
    }
    
    @SuppressWarnings("unchecked")
	public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length != 2) {
            return Collections.emptyList();
        }
        return (List<String>) this.plugin.getFactionManager().getFactions().stream().filter(faction -> faction instanceof EventFaction).map((Function<? super Faction, ?>)Faction::getName).collect(Collectors.toList());
    }
}
