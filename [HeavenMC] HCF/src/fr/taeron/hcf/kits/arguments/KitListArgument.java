package fr.taeron.hcf.kits.arguments;

import fr.taeron.hcf.HCF;
import fr.taeron.hcf.kits.*;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;

import org.bukkit.command.*;
import org.heavenmc.core.util.command.CommandArgument;
import org.bukkit.*;

import java.util.*;

public class KitListArgument extends CommandArgument{
	
    private final HCF plugin;
    
    public KitListArgument(final HCF plugin) {
        super("list", "Voir la liste des kits");
        this.plugin = plugin;
        this.permission = "command.kit.argument." + this.getName();
    }
    
    @Override
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName();
    }
    
    @SuppressWarnings("rawtypes")
	@Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        final List<Kit> kits = this.plugin.getKitManager().getKits();
        if (kits.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Aucun kit n'as été créé pour le moment.");
            return true;
        }
        final List<String> kitNames = new ArrayList<String>();
        for (final Kit kit : kits) {
            final String permission = kit.getPermissionNode();
            if (permission == null || sender.hasPermission(permission)) {
                kitNames.add(ChatColor.GREEN + kit.getDisplayName());
            }
        }
        final String kitList = StringUtils.join((Iterable)kitNames, ChatColor.GRAY + ", ");
        sender.sendMessage(ChatColor.GRAY + "Kits (" + kitNames.size() + '/' + kits.size() + ")");
        sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.WHITE + kitList + ChatColor.GRAY + ']');
        return true;
    }
    
    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return Collections.emptyList();
    }
}
