package fr.taeron.hcf.kits.arguments;

import fr.taeron.hcf.HCF;
import fr.taeron.hcf.kits.*;

import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.heavenmc.core.util.command.CommandArgument;
import org.bukkit.*;

import java.util.*;

public class KitGuiArgument extends CommandArgument{
	
    private final HCF plugin;
    
    public KitGuiArgument(HCF plugin) {
        super("gui", "Ouvrir le menu des kits");
        this.plugin = plugin;
        this.aliases = new String[] { "menu" };
        this.permission = "command.kit.argument." + this.getName();
    }
    
    @Override
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName();
    }
    
    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "tg.");
            return true;
        }
        final Collection<Kit> kits = this.plugin.getKitManager().getKits();
        if (kits.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Aucun kit n'as été créé pour le moment.");
            return true;
        }
        final Player player = (Player)sender;
        player.openInventory(this.plugin.getKitManager().getGui(player));
        return true;
    }
    
    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return Collections.emptyList();
    }
}
