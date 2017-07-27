package fr.taeron.hcf.kits.arguments;

import fr.taeron.hcf.HCF;
import fr.taeron.hcf.kits.*;

import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import org.bukkit.inventory.*;
import org.heavenmc.core.util.command.CommandArgument;

import java.util.*;

public class KitSetItemsArgument extends CommandArgument{
	
    private final HCF plugin;
    
    public KitSetItemsArgument(final HCF plugin) {
        super("setitems", "Définir les items d'un kit");
        this.plugin = plugin;
        this.permission = "command.kit.argument." + this.getName();
    }
    
    @Override
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <kit>";
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
        final Kit kit = this.plugin.getKitManager().getKit(args[1]);
        if (kit == null) {
            sender.sendMessage(ChatColor.RED + "Ce kit n'existe pas");
            return true;
        }
        final Player player = (Player)sender;
        final PlayerInventory inventory = player.getInventory();
        kit.setItems(inventory.getContents());
        kit.setArmour(inventory.getArmorContents());
        kit.setEffects(player.getActivePotionEffects());
        sender.sendMessage(ChatColor.GREEN + "Tu as défini le contenu du kit " + kit.getDisplayName() + " en ton inventaire actuel.");
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
