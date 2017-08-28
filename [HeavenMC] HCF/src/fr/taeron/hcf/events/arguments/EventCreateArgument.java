package fr.taeron.hcf.events.arguments;

import fr.taeron.hcf.*;
import org.bukkit.command.*;
import org.heavenmc.core.util.command.CommandArgument;
import org.bukkit.*;

import fr.taeron.hcf.faction.type.*;
import net.minecraft.util.org.apache.commons.lang3.text.WordUtils;
import fr.taeron.hcf.events.*;
import fr.taeron.hcf.events.factions.*;

import java.util.*;

public class EventCreateArgument extends CommandArgument{
	
    private final HCF plugin;
    
    public EventCreateArgument(HCF plugin) {
        super("create", "Créer un event", new String[] { "make", "define" });
        this.plugin = plugin;
        this.permission = "command.event.argument." + this.getName();
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <event> <Conquest|KOTH|Siege>";
    }
    
    @SuppressWarnings("unused")
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Utilisation: " + this.getUsage(label));
            return true;
        }
        Faction faction = this.plugin.getFactionManager().getFaction(args[1]);
        if (faction != null) {
            sender.sendMessage(ChatColor.RED + "Il y a déjà une faction nommée " + args[1] + '.');
            return true;
        }
        final String upperCase2;
        final String upperCase = upperCase2 = args[2].toUpperCase();
        switch (upperCase2) {
            case "CONQUEST": {
                faction = new ConquestFaction(args[1]);
                break;
            }
            case "KOTH": {
                faction = new KothFaction(args[1]);
                break;
            }
            default: {
                sender.sendMessage(ChatColor.RED + "Utilisation: " + this.getUsage(label));
                return true;
            }
        }
        this.plugin.getFactionManager().createFaction(faction, sender);
        sender.sendMessage(ChatColor.YELLOW + "La faction event " + ChatColor.WHITE + faction.getDisplayName(sender) + ChatColor.YELLOW + " a été créée avec le type " + WordUtils.capitalizeFully(args[2]) + '.');
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length != 3) {
            return Collections.emptyList();
        }
        final EventType[] eventTypes = EventType.values();
        final List<String> results = new ArrayList<String>(eventTypes.length);
        for (final EventType eventType : eventTypes) {
            results.add(eventType.name());
        }
        return results;
    }
}
