package fr.taeron.hcf.kits.arguments;

import fr.taeron.hcf.HCF;
import fr.taeron.hcf.kits.*;
import fr.taeron.hcf.kits.events.*;

import org.bukkit.command.*;
import org.bukkit.*;
import org.bukkit.event.*;
import org.heavenmc.core.util.command.CommandArgument;

import java.util.*;

public class KitRenameArgument extends CommandArgument{
	
    private final HCF plugin;
    
    public KitRenameArgument(final HCF plugin) {
        super("rename", "Renommer");
        this.plugin = plugin;
        this.permission = "command.kit.argument." + this.getName();
    }
    
    @Override
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <kit> <nom>";
    }
    
    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Utilisation: " + this.getUsage(label));
            return true;
        }
        Kit kit = this.plugin.getKitManager().getKit(args[2]);
        if (kit != null) {
            sender.sendMessage(ChatColor.RED + "Il y a déjà un kit nommé " + kit.getName() + '.');
            return true;
        }
        kit = this.plugin.getKitManager().getKit(args[1]);
        if (kit == null) {
            sender.sendMessage(ChatColor.RED + "Ce kit n'existe pas");
            return true;
        }
        final KitRenameEvent event = new KitRenameEvent(kit, kit.getName(), args[2]);
        Bukkit.getPluginManager().callEvent((Event)event);
        if (event.isCancelled()) {
            return true;
        }
        if (event.getOldName().equals(event.getNewName())) {
            sender.sendMessage(ChatColor.RED + "Ce kit s'appele déjà " + event.getNewName() + '.');
            return true;
        }
        kit.setName(event.getNewName());
        sender.sendMessage(ChatColor.GREEN + "Tu as changé le nom du kit " + event.getOldName() + " en " + event.getNewName() + '.');
        return true;
    }
    
    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length != 2) {
            return Collections.emptyList();
        }
        final Collection<Kit> kits = this.plugin.getKitManager().getKits();
        final List<String> results = new ArrayList<String>(kits.size());
        for (final Kit kit : kits) {
            results.add(kit.getName());
        }
        return results;
    }
}
