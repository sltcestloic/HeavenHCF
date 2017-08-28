package fr.taeron.hcf.deathban.lives.argument;

import fr.taeron.hcf.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.heavenmc.core.util.command.CommandArgument;
import org.bukkit.*;
import java.util.*;

public class LivesCheckArgument extends CommandArgument
{
    private final HCF plugin;
    
    public LivesCheckArgument(final HCF plugin) {
        super("check", "Voir le nombre de vies qu'un joueur possède.");
        this.plugin = plugin;
        this.permission = "command.lives.argument." + this.getName();
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <joueur>";
    }
    
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        OfflinePlayer target;
        if (args.length > 1) {
            target = Bukkit.getOfflinePlayer(args[1]);
        }
        else {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Utilisation: " + this.getUsage(label));
                return true;
            }
            target = (OfflinePlayer)sender;
        }
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(ChatColor.RED + args[1]  + "n'est pas connecté.");
            return true;
        }
        final int targetLives = this.plugin.getDeathbanManager().getLives(target.getUniqueId());
        sender.sendMessage(ChatColor.YELLOW + target.getName() + ChatColor.YELLOW + " a " + ChatColor.LIGHT_PURPLE + targetLives + ChatColor.YELLOW + ' ' + ((targetLives == 1) ? "vie" : "vies") + '.');
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return (args.length == 2) ? null : Collections.emptyList();
    }
}
