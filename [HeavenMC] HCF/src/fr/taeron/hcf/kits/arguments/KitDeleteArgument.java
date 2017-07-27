package fr.taeron.hcf.kits.arguments;

import fr.taeron.hcf.HCF;
import fr.taeron.hcf.kits.*;
import fr.taeron.hcf.kits.events.*;

import org.bukkit.command.*;
import org.bukkit.*;
import org.bukkit.event.*;
import org.heavenmc.core.util.command.CommandArgument;

import java.util.*;

public class KitDeleteArgument extends CommandArgument{
	
    private final HCF plugin;
    
    public KitDeleteArgument(final HCF plugin) {
        super("delete", "Supprimer un kit");
        this.plugin = plugin;
        this.aliases = new String[] { "del", "remove" };
        this.permission = "command.kit.argument." + this.getName();
    }
    
    @Override
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <kitName>";
    }
    
    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        final Kit kit = this.plugin.getKitManager().getKit(args[1]);
        if (kit == null) {
            sender.sendMessage(ChatColor.RED + "There is not a kit named " + args[1] + '.');
            return true;
        }
        final KitRemoveEvent event = new KitRemoveEvent(kit);
        Bukkit.getPluginManager().callEvent((Event)event);
        if (event.isCancelled()) {
            return true;
        }
        this.plugin.getKitManager().removeKit(kit);
        sender.sendMessage(ChatColor.GRAY + "Removed kit '" + args[1] + "'.");
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
