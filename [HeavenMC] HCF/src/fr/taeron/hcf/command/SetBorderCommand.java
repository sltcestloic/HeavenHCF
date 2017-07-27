package fr.taeron.hcf.command;

import org.bukkit.command.*;
import org.heavenmc.core.util.BukkitUtils;
import org.heavenmc.core.util.GuavaCompat;
import org.heavenmc.core.util.JavaUtils;
import org.bukkit.*;
import fr.taeron.hcf.*;
import com.google.common.base.Optional;

import java.util.*;

public class SetBorderCommand implements CommandExecutor, TabCompleter
{
    
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Utilisation: /" + label + " <monde> <taille>");
            return true;
        }
        final Optional<World.Environment> optional = (Optional<World.Environment>)GuavaCompat.getIfPresent(World.Environment.class, args[0]);
        if (!optional.isPresent()) {
            sender.sendMessage(ChatColor.RED + "L'environement '" + args[0] + "' n'existe pas.");
            return true;
        }
        final Integer amount = JavaUtils.tryParseInt(args[1]);
        if (amount == null) {
            sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a valid number.");
            return true;
        }
        if (amount < 3000) {
            sender.sendMessage(ChatColor.RED + "La taille minimum est de " + 3000 + '.');
            return true;
        }
        if (amount > 25000) {
            sender.sendMessage(ChatColor.RED + "La taille maximale est de " + 25000 + '.');
            return true;
        }
        final World.Environment environment = (World.Environment)optional.get();
        ConfigurationService.BORDER_SIZES.put(environment, amount);
        Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Tu as défini la taille de la bordure de " + environment.name() + " à " + amount + '.');
        return true;
    }
    
	public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length != 1) {
            return Collections.emptyList();
        }
        final World.Environment[] values = World.Environment.values();
        final List<String> results = new ArrayList<String>(values.length);
        for (final World.Environment environment : values) {
            results.add(environment.name());
        }
        return (List<String>)BukkitUtils.getCompletions(args, results);
    }
}
