package fr.taeron.hcf.command;

import fr.taeron.hcf.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import fr.taeron.hcf.user.*;
import java.util.*;

public class ToggleCapzoneCommand implements CommandExecutor, TabExecutor
{
    private final HCF plugin;
    
    public ToggleCapzoneCommand(final HCF plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        final FactionUser factionUser = this.plugin.getUserManager().getUser(((Player)sender).getUniqueId());
        final boolean newStatus = !factionUser.isCapzoneEntryAlerts();
        factionUser.setCapzoneEntryAlerts(newStatus);
        sender.sendMessage(ChatColor.AQUA + "You will now " + (newStatus ? ChatColor.GREEN.toString() : (ChatColor.RED + "un")) + "able" + ChatColor.AQUA + " to see capture zone entry messages.");
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return Collections.emptyList();
    }
}
