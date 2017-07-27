package fr.taeron.hcf.command;

import fr.taeron.hcf.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import fr.taeron.hcf.timer.type.*;
import java.util.*;

public class LogoutCommand implements CommandExecutor, TabCompleter
{
    private final HCF plugin;
    
    public LogoutCommand(final HCF plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        final Player player = (Player)sender;
        final LogoutTimer logoutTimer = this.plugin.getTimerManager().logoutTimer;
        if (!logoutTimer.setCooldown(player, player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Ton cooldown de " + logoutTimer.getDisplayName() + ChatColor.RED + " est déjà actif");
            return true;
        }
        sender.sendMessage(ChatColor.RED + "Ton cooldown de " + logoutTimer.getDisplayName() + ChatColor.RED + " viens de commencer");
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return Collections.emptyList();
    }
}
