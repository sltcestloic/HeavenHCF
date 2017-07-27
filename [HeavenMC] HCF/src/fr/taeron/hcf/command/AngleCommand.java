package fr.taeron.hcf.command;

import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.heavenmc.core.util.JavaUtils;
import org.bukkit.*;
import java.util.*;

public class AngleCommand implements CommandExecutor, TabCompleter
{
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        final Location location = ((Player)sender).getLocation();
        sender.sendMessage(ChatColor.GOLD + JavaUtils.format((Number)location.getYaw()) + " yaw" + ChatColor.WHITE + ", " + ChatColor.GOLD + JavaUtils.format((Number)location.getPitch()) + " pitch");
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return Collections.emptyList();
    }
}
 