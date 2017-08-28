package fr.taeron.hcf.events.arguments;

import fr.taeron.hcf.*;
import fr.taeron.hcf.events.factions.*;

import org.bukkit.command.*;
import org.heavenmc.core.util.command.CommandArgument;
import org.bukkit.*;

import fr.taeron.hcf.faction.type.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class EventRenameArgument extends CommandArgument{
	
    private final HCF plugin;
    
    public EventRenameArgument(HCF plugin) {
        super("rename", "Renommer un event");
        this.plugin = plugin;
        this.permission = "command.event.argument." + this.getName();
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <ancien nom> <nouveau nom>";
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Utilisation: " + this.getUsage(label));
            return true;
        }
        Faction faction = this.plugin.getFactionManager().getFaction(args[2]);
        if (faction != null) {
            sender.sendMessage(ChatColor.RED + "Il y a déjà une faction nommée " + args[2] + '.');
            return true;
        }
        faction = this.plugin.getFactionManager().getFaction(args[1]);
        if (!(faction instanceof EventFaction)) {
            sender.sendMessage(ChatColor.RED + "Il n'y a pas de faction nommée '" + args[1] + "'.");
            return true;
        }
        final String oldName = faction.getName();
        faction.setName(args[2], sender);
        sender.sendMessage(ChatColor.YELLOW + "Tu as renommé l'event " + ChatColor.WHITE + oldName + ChatColor.YELLOW + " en " + ChatColor.WHITE + faction.getName() + ChatColor.YELLOW + '.');
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
