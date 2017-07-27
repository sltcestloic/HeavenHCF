package fr.taeron.hcf.command;

import fr.taeron.hcf.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import fr.taeron.hcf.timer.*;
import java.util.*;

public class GoppleCommand implements CommandExecutor, TabCompleter
{
    private final HCF plugin;
    
    public GoppleCommand(final HCF plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        final Player player = (Player)sender;
        final PlayerTimer timer = this.plugin.getTimerManager().notchAppleTimer;
        final long remaining = timer.getRemaining(player);
        if (remaining <= 0L) {
            sender.sendMessage(ChatColor.RED + "Ton cooldown de " + timer.getDisplayName() + ChatColor.RED + " n'est pas actif");
            return true;
        }
        sender.sendMessage(ChatColor.YELLOW + "Ton cooldown de " + timer.getDisplayName() + ChatColor.YELLOW + " est actif pendant encore " + ChatColor.BOLD + HCF.getRemaining(remaining, true, false) + ChatColor.YELLOW + '.');
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return Collections.emptyList();
    }
}
