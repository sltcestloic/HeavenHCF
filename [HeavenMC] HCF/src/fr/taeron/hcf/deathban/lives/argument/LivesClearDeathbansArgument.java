package fr.taeron.hcf.deathban.lives.argument;

import fr.taeron.hcf.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.heavenmc.core.util.command.CommandArgument;

import fr.taeron.hcf.user.*;
import org.bukkit.*;

public class LivesClearDeathbansArgument extends CommandArgument{
	
    private final HCF plugin;
    
    public LivesClearDeathbansArgument(final HCF plugin) {
        super("cleardeathbans", "Effacer tous les deathbans");
        this.plugin = plugin;
        this.aliases = new String[] { "resetdeathbans" };
        this.permission = "command.lives.argument." + this.getName();
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName();
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (sender instanceof ConsoleCommandSender || (sender instanceof Player && sender.getName().equalsIgnoreCase("CommandoNanny"))) {
            for (final FactionUser user : this.plugin.getUserManager().getUsers().values()) {
                user.removeDeathban();
            }
            Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Tous les deathbans ont été supprimés.");
            return true;
        }
        sender.sendMessage(ChatColor.RED + "Cette commande ne peut s'executer que depuis la console.");
        return false;
    }
}
