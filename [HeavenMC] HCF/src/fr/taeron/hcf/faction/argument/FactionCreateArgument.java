package fr.taeron.hcf.faction.argument;

import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.heavenmc.core.util.JavaUtils;
import org.heavenmc.core.util.command.CommandArgument;
import org.bukkit.*;
import fr.taeron.hcf.*;
import fr.taeron.hcf.faction.type.*;

public class FactionCreateArgument extends CommandArgument{
	
    private final HCF plugin;
    
    public FactionCreateArgument(final HCF plugin) {
        super("create", "Créer une faction", new String[] { "make", "define" });
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <faction>";
    }
    
    @SuppressWarnings("deprecation")
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "La console n'est pas supportée.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Utilisation: " + this.getUsage(label));
            return true;
        }
        final String name = args[1];
        if (ConfigurationService.DISALLOWED_FACTION_NAMES.contains(name.toLowerCase()) && !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "'" + name + "' fait partie de la liste des noms de faction bloqués, merci de choisir un autre nom.");
            return true;
        }
        if (name.length() < 3) {
            sender.sendMessage(ChatColor.RED + "Le nom de la faction doit contenir au moins " + 3 + " caractères.");
            return true;
        }
        if (name.length() > 16) {
            sender.sendMessage(ChatColor.RED + "Le nom de la faction ne peut pas contenir plus de " + 16 + " caractères.");
            return true;
        }
        if (!JavaUtils.isAlphanumeric(name)) {
            sender.sendMessage(ChatColor.RED + "Le nom de la faction doit être alphanumérique.");
            return true;
        }
        if (this.plugin.getFactionManager().getFaction(name) != null) {
            sender.sendMessage(ChatColor.RED + "La faction '" + name + "' existe déjà.");
            return true;
        }
        if (this.plugin.getFactionManager().getPlayerFaction((Player)sender) != null) {
            sender.sendMessage(ChatColor.RED + "Tu as déjà une faction...");
            return true;
        }
        this.plugin.getFactionManager().createFaction(new PlayerFaction(name), sender);
        return true;
    }
}
