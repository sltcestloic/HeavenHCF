package fr.taeron.hcf.kits.arguments;

import fr.taeron.hcf.HCF;
import fr.taeron.hcf.kits.*;
import fr.taeron.hcf.kits.events.*;

import org.bukkit.command.*;
import org.bukkit.entity.*;

import org.bukkit.*;
import org.bukkit.event.*;
import org.heavenmc.core.util.JavaUtils;
import org.heavenmc.core.util.command.CommandArgument;

public class KitCreateArgument extends CommandArgument{
	
    private final HCF plugin;
    
    public KitCreateArgument(HCF plugin) {
        super("create", "Creates a kit");
        this.plugin = plugin;
        this.aliases = new String[] { "make", "build" };
        this.permission = "command.kit.argument." + this.getName();
    }
    
    @Override
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <kit> [description]";
    }
    
    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "tg");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Utilisation: " + this.getUsage(label));
            return true;
        }
        if (!JavaUtils.isAlphanumeric(args[1])) {
            sender.sendMessage(ChatColor.RED + "Nom incorrect");
            return true;
        }
        Kit kit = this.plugin.getKitManager().getKit(args[1]);
        if (kit != null) {
            sender.sendMessage(ChatColor.RED + "Ce kit existe déjà");
            return true;
        }
        final Player player = (Player)sender;
        kit = new Kit(args[1], (args.length >= 3) ? args[2] : null, player.getInventory(), player.getActivePotionEffects());
        final KitCreateEvent event = new KitCreateEvent(kit);
        Bukkit.getPluginManager().callEvent((Event)event);
        if (event.isCancelled()) {
            return true;
        }
        this.plugin.getKitManager().createKit(kit);
        sender.sendMessage(ChatColor.GREEN + "Tu as créé le kit '" + kit.getDisplayName() + "'.");
        return true;
    }
}
