package fr.taeron.hcf.kits.arguments;

import fr.taeron.hcf.HCF;
import fr.taeron.hcf.kits.*;

import org.bukkit.command.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.heavenmc.core.util.command.CommandArgument;

import java.util.*;

public class KitApplyArgument extends CommandArgument{
	
    private final HCF plugin;
    
    public KitApplyArgument(final HCF plugin) {
        super("apply", "Applies a kit to player");
        this.plugin = plugin;
        this.aliases = new String[] { "give" };
        this.permission = "command.kit.argument." + this.getName();
    }
    
    @Override
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <kit> <joueur>";
    }
    
    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Utilisation: " + this.getUsage(label));
            return true;
        }
        final Kit kit = this.plugin.getKitManager().getKit(args[1]);
        if (kit == null) {
            sender.sendMessage(ChatColor.RED + "Ce kit n'existe pas.");
            return true;
        }
        final Player target = Bukkit.getPlayer(args[2]);
        if (target == null || (sender instanceof Player && !((Player)sender).canSee(target))) {
            sender.sendMessage(ChatColor.RED + "Joueur introuvable");
            return true;
        }
        if (kit.applyTo(target, true, true)) {
            sender.sendMessage(ChatColor.GRAY + "Tu as donné le kit '" + kit.getDisplayName() + "' à '" + target.getName() + "'.");
            return true;
        }
        sender.sendMessage(ChatColor.RED + "Impossible de donner le kit " + kit.getDisplayName() + " à " + target.getName() + '.');
        return true;
    }
    
    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length == 2) {
            final Collection<Kit> kits = this.plugin.getKitManager().getKits();
            final List<String> results = new ArrayList<String>(kits.size());
            for (final Kit kit : kits) {
                results.add(kit.getName());
            }
            return results;
        }
        if (args.length == 3) {
            return null;
        }
        return Collections.emptyList();
    }
}
