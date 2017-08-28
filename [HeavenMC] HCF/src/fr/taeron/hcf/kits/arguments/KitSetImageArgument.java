package fr.taeron.hcf.kits.arguments;

import fr.taeron.hcf.HCF;
import fr.taeron.hcf.kits.*;

import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import org.bukkit.inventory.*;
import org.heavenmc.core.util.command.CommandArgument;

import java.util.*;

public class KitSetImageArgument extends CommandArgument{
	
    private final HCF plugin;
    
    public KitSetImageArgument(final HCF plugin) {
        super("setimage", "Définir l'image d'un kit dans le GUI");
        this.plugin = plugin;
        this.aliases = new String[] { "setitem", "setpic", "setpicture" };
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
        final Player player = (Player)sender;
        final ItemStack stack = player.getItemInHand();
        if (stack == null || stack.getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "Tu dois avoir un item dans la main.");
            return true;
        }
        final Kit kit = this.plugin.getKitManager().getKit(args[1]);
        if (kit == null) {
            sender.sendMessage(ChatColor.RED + "Ce kit n'existe pas.");
            return true;
        }
        kit.setImage(stack.clone());
        sender.sendMessage(ChatColor.GREEN + "Tu as défini l'image du kit " + ChatColor.YELLOW + kit.getDisplayName() + ChatColor.GREEN + " en " + ChatColor.YELLOW + stack.getType() + ChatColor.GREEN + '.');
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
