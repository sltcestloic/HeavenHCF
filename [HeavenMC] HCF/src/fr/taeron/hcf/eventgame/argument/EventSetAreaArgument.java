package fr.taeron.hcf.eventgame.argument;

import fr.taeron.hcf.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.heavenmc.core.util.command.CommandArgument;
import org.heavenmc.core.util.cuboid.Cuboid;
import org.bukkit.*;
import fr.taeron.hcf.eventgame.faction.*;
import com.sk89q.worldedit.bukkit.*;
import com.sk89q.worldedit.bukkit.selections.*;
import fr.taeron.hcf.faction.type.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class EventSetAreaArgument extends CommandArgument{
	
    private final HCF plugin;
    
    public EventSetAreaArgument(final HCF plugin) {
        super("setarea", "Définir la zone d'un event");
        this.plugin = plugin;
        this.aliases = new String[] { "setclaim", "setclaimarea", "setland" };
        this.permission = "command.event.argument." + this.getName();
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <nom>";
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can set event claim areas");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Utilisation: " + this.getUsage(label));
            return true;
        }
        final WorldEditPlugin worldEditPlugin = this.plugin.getWorldEdit();
        if (worldEditPlugin == null) {
            sender.sendMessage(ChatColor.RED + "WorldEdit n'est pas installé.");
            return true;
        }
        final Player player = (Player)sender;
        final Selection selection = worldEditPlugin.getSelection(player);
        if (selection == null) {
            sender.sendMessage(ChatColor.RED + "Tu dois avoir une sélection WorldEdit pour faire ça.");
            return true;
        }
        if (selection.getWidth() < 8 || selection.getLength() < 8) {
            sender.sendMessage(ChatColor.RED + "Les claims d'event doivent faire au moins une taille de " + 8 + 'x' + 8 + '.');
            return true;
        }
        final Faction faction = this.plugin.getFactionManager().getFaction(args[1]);
        if (!(faction instanceof EventFaction)) {
            sender.sendMessage(ChatColor.RED + "Il n'y a pas de faction nommée '" + args[1] + "'.");
            return true;
        }
        ((EventFaction)faction).setClaim(new Cuboid(selection.getMinimumPoint(), selection.getMaximumPoint()), (CommandSender)player);
        sender.sendMessage(ChatColor.YELLOW + "Tu as défini les claims de l'event " + faction.getName() + ChatColor.YELLOW + '.');
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
