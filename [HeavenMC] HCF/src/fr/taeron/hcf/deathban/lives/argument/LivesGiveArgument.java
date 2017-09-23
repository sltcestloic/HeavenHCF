package fr.taeron.hcf.deathban.lives.argument;

import fr.taeron.hcf.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.heavenmc.core.util.JavaUtils;
import org.heavenmc.core.util.command.CommandArgument;
import org.bukkit.*;
import java.util.*;

public class LivesGiveArgument extends CommandArgument{
	
    private final HCF plugin;
    
    public LivesGiveArgument(final HCF plugin) {
        super("give", "Donner une vie à un joueur");
        this.plugin = plugin;
        this.aliases = new String[] { "transfer", "send", "pay", "add" };
        this.permission = "command.lives.argument." + this.getName();
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <joueur> <nombre>";
    }
    
	@SuppressWarnings("deprecation")
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Utilisation: " + this.getUsage(label));
            return true;
        }
        final Integer amount = JavaUtils.tryParseInt(args[2]);
        if (amount == null) {
            sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a number.");
            return true;
        }
        if (amount <= 0) {
            sender.sendMessage(ChatColor.RED + "Le nombre de vies doit être positif.");
            return true;
        }
        final OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(ChatColor.GOLD + "Le joueur '" + ChatColor.WHITE + args[1] + ChatColor.GOLD + "' ne s'est jamais connecté au serveur.");
            return true;
        }
        final Player onlineTarget = target.getPlayer();
        if (sender instanceof Player) {
            final Player player = (Player)sender;
            final int ownedLives = this.plugin.getDeathbanManager().getLives(player.getUniqueId());
            if (amount > ownedLives) {
                sender.sendMessage(ChatColor.RED + "Tu as essayé de donner " + amount + " vies à " + target.getName() + ", mais tu en as seulement " + ownedLives + '.');
                return true;
            }
            this.plugin.getDeathbanManager().takeLives(player.getUniqueId(), amount);
        }
        this.plugin.getDeathbanManager().addLives(target.getUniqueId(), amount);
        sender.sendMessage(ChatColor.YELLOW + "Tu as envoyé " + ChatColor.LIGHT_PURPLE + amount + ChatColor.YELLOW + ((amount > 1) ? " vie" : " vies") + " à " + target.getName());
        if (onlineTarget != null) {
            onlineTarget.sendMessage(ChatColor.GOLD + sender.getName() + ChatColor.YELLOW + " t'as envoyé " + ChatColor.LIGHT_PURPLE + amount + ' ' + ChatColor.YELLOW + ((amount > 1) ? "vie" : "vies") + '.');
        }
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return (args.length == 2) ? null : Collections.emptyList();
    }
}
