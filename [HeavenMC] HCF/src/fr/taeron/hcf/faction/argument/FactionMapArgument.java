package fr.taeron.hcf.faction.argument;

import fr.taeron.hcf.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.heavenmc.core.util.command.CommandArgument;
import org.bukkit.*;
import fr.taeron.hcf.faction.*;
import fr.taeron.hcf.visualise.*;
import fr.taeron.hcf.user.*;
import java.util.*;
import org.heavenmc.core.util.*;

public class FactionMapArgument extends CommandArgument{
	
    private final HCF plugin;
    
    public FactionMapArgument(final HCF plugin) {
        super("map", "View all claims around your chunk.");
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " [factionName]";
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        final Player player = (Player)sender;
        final UUID uuid = player.getUniqueId();
        final FactionUser factionUser = this.plugin.getUserManager().getUser(uuid);
        VisualType visualType;
        if (args.length <= 1) { 
            visualType = VisualType.CLAIM_MAP;
        }
        else if ((visualType = GuavaCompat.getIfPresent(VisualType.class, args[1]).orNull()) == null) {
            player.sendMessage(ChatColor.RED + "Le Visual Type " + args[1] + " n'existe pas.");
            return true;
        }
        final boolean newShowingMap = !factionUser.isShowClaimMap();
        if (newShowingMap) {
            if (!LandMap.updateMap(player, this.plugin, visualType, true)) {
                return true;
            }
        }
        else {
            this.plugin.getVisualiseHandler().clearVisualBlocks(player, visualType, null);
            sender.sendMessage(ChatColor.RED + "Les pilliers ont été retirés.");
        }
        factionUser.setShowClaimMap(newShowingMap);
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length != 2 || !(sender instanceof Player)) {
            return Collections.emptyList();
        }
        final VisualType[] values = VisualType.values();
        final List<String> results = new ArrayList<String>(values.length);
        for (final VisualType visualType : values) {
            results.add(visualType.name());
        }
        return results;
    }
}
