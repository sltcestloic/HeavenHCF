package fr.taeron.hcf.deathban.lives.argument;

import fr.taeron.hcf.*;
import org.bukkit.command.*;
import org.heavenmc.core.util.BukkitUtils;
import org.heavenmc.core.util.JavaUtils;
import org.heavenmc.core.util.command.CommandArgument;
import org.bukkit.*;
import java.util.*;

public class LivesSetArgument extends CommandArgument{
	
    private final HCF plugin;
    
    public LivesSetArgument(final HCF plugin) {
        super("set", "Définir le nombre de vies d'un joueur");
        this.plugin = plugin;
        this.permission = "hcf.command.lives.argument." + this.getName();
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <joueur> <nombre>";
    }
    
    @SuppressWarnings("deprecation")
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Utilisation: " + this.getUsage(label));
            return true;
        }
        final Integer amount = JavaUtils.tryParseInt(args[2]);
        if (amount == null) {
            sender.sendMessage(ChatColor.RED + "'" + args[2] + "' n'est pas un nombre.");
            return true;
        }
        final OfflinePlayer target = BukkitUtils.offlinePlayerWithNameOrUUID(args[1]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage("§cCe joueur n'existe pas.");
            return true;
        }
        this.plugin.getDeathbanManager().setLives(target.getUniqueId(), amount);
        sender.sendMessage(ChatColor.YELLOW + target.getName() + " a désormais " + ChatColor.GOLD + amount + ChatColor.YELLOW + " vies.");
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return (args.length == 2) ? null : Collections.emptyList();
    }
}
