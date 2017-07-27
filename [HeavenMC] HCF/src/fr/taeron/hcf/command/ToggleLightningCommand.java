package fr.taeron.hcf.command;

import fr.taeron.hcf.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import fr.taeron.hcf.user.*;
import java.util.*;

public class ToggleLightningCommand implements CommandExecutor, TabExecutor
{
    private final HCF plugin;
    
    public ToggleLightningCommand(final HCF plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        final FactionUser factionUser = this.plugin.getUserManager().getUser(((Player)sender).getUniqueId());
        final boolean newShowLightning = !factionUser.isShowLightning();
        factionUser.setShowLightning(newShowLightning);
        sender.sendMessage(ChatColor.AQUA + "Éclairs de mort " + (newShowLightning ? (ChatColor.GREEN + "activés") : (ChatColor.RED + "désactivés")));
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return Collections.emptyList();
    }
}
