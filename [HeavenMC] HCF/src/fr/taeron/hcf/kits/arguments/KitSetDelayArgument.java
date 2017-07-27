package fr.taeron.hcf.kits.arguments;

import fr.taeron.hcf.HCF;
import fr.taeron.hcf.kits.*;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;

import org.bukkit.command.*;
import org.heavenmc.core.util.JavaUtils;
import org.heavenmc.core.util.command.CommandArgument;
import org.bukkit.*;

import java.util.*;

public class KitSetDelayArgument extends CommandArgument
{
    private final HCF plugin;
    
    public KitSetDelayArgument(final HCF plugin) {
        super("setdelay", "Définir le délai d'un kit");
        this.plugin = plugin;
        this.aliases = new String[] { "delay", "setcooldown", "cooldown" };
        this.permission = "command.kit.argument." + this.getName();
    }
    
    @Override
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <kit> <délai>";
    }
    
    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Utilisation: " + this.getUsage(label));
            return true;
        }
        final Kit kit = this.plugin.getKitManager().getKit(args[1]);
        if (kit == null) {
            sender.sendMessage(ChatColor.RED + "Ce kit n'existe pas");
            return true;
        }
        final long duration = JavaUtils.parse(args[2]);
        if (duration == -1L) {
            sender.sendMessage(ChatColor.RED + "Durée incorrecte, exemple: 10m 1s");
            return true;
        }
        kit.setDelayMillis(duration);
        sender.sendMessage(ChatColor.GREEN + "Tu as défini le délai du kit " + kit.getName() + " à " + DurationFormatUtils.formatDurationWords(duration, true, true) + '.');
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
