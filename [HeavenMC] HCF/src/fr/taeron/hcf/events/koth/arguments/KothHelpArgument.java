package fr.taeron.hcf.events.koth.arguments;

import org.bukkit.command.*;
import org.heavenmc.core.util.command.CommandArgument;

import fr.taeron.hcf.events.koth.*;

import org.bukkit.*;

public class KothHelpArgument extends CommandArgument{
	
    private final KothExecutor kothExecutor;
    
    public KothHelpArgument(final KothExecutor kothExecutor) {
        super("help", "View help about how KOTH's work");
        this.kothExecutor = kothExecutor;
        this.permission = "command.koth.argument." + this.getName();
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName();
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        sender.sendMessage(ChatColor.AQUA + "*** KotH Help ***");
        for (final CommandArgument argument : this.kothExecutor.getArguments()) {
            if (!argument.equals((Object)this)) {
                final String permission = argument.getPermission();
                if (permission != null && !sender.hasPermission(permission)) {
                    continue;
                }
                sender.sendMessage(ChatColor.GRAY + argument.getUsage(label) + " - " + argument.getDescription() + '.');
            }
        }
        sender.sendMessage(ChatColor.GRAY + "/fac show <kothName> - View information about a KOTH.");
        return true;
    }
}
