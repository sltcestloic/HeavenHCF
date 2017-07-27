package fr.taeron.hcf.command;

import fr.taeron.hcf.*;
import net.minecraft.util.org.apache.commons.lang3.time.FastDateFormat;

import org.bukkit.command.*;
import org.bukkit.*;
import java.util.*;

public class ServerTimeCommand implements CommandExecutor, TabCompleter
{
    private static final FastDateFormat FORMAT;
    
    static {
        FORMAT = FastDateFormat.getInstance("h:mm:ssa z yyyy", ConfigurationService.SERVER_TIME_ZONE);
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        sender.sendMessage(ChatColor.YELLOW + "Il est actuellement " + ChatColor.AQUA + ServerTimeCommand.FORMAT.format(System.currentTimeMillis()) + ChatColor.AQUA + '.');
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return Collections.emptyList();
    }
}
