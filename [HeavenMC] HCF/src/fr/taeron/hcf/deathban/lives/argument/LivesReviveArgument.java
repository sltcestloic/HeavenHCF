package fr.taeron.hcf.deathban.lives.argument;

import org.bukkit.plugin.*;
import org.heavenmc.core.util.command.CommandArgument;
import org.bukkit.command.*;
import fr.taeron.hcf.faction.struct.*;
import org.bukkit.entity.*;
import fr.taeron.hcf.*;
import org.bukkit.*;
import fr.taeron.hcf.user.*;
import fr.taeron.hcf.deathban.*;
import fr.taeron.hcf.faction.type.*;
import java.util.*;

public class LivesReviveArgument extends CommandArgument {
	
    private final HCF plugin;
    
    public LivesReviveArgument(final HCF plugin) {
        super("revive", "Revive un joueur");
        this.plugin = plugin;
        this.permission = "command.lives.argument." + this.getName();
        plugin.getServer().getMessenger().registerOutgoingPluginChannel((Plugin)plugin, "BungeeCord");
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <joueur>";
    }
    
    @SuppressWarnings("deprecation")
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Utilisation: " + this.getUsage(label));
            return true;
        }
        final OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(ChatColor.GOLD + "Le joueur '" + ChatColor.WHITE + args[1] + ChatColor.GOLD + "' ne s'est jamais connecté au serveur.");
            return true;
        }
        final UUID targetUUID = target.getUniqueId();
        final FactionUser factionTarget = this.plugin.getUserManager().getUser(targetUUID);
        final Deathban deathban = factionTarget.getDeathban();
        if (deathban == null || !deathban.isActive()) {
            sender.sendMessage(ChatColor.RED + target.getName() + " n'est pas deathban.");
            return true;
        }
        Relation relation = Relation.ENEMY;
        if (sender instanceof Player) {
            if (!sender.hasPermission("hcf.revive.bypass") && this.plugin.getEotwHandler().isEndOfTheWorld()) {
                sender.sendMessage(ChatColor.RED + "Impossible de revive un joueur durant l'EOTW.");
                return true;
            }
            if (!sender.hasPermission("hcf.revive.bypass")) {
                final Player player = (Player)sender;
                final UUID playerUUID = player.getUniqueId();
                final int selfLives = this.plugin.getDeathbanManager().getLives(playerUUID);
                if (selfLives <= 0 && !sender.hasPermission("heaven.staff")) {
                    sender.sendMessage(ChatColor.RED + "Tu n'as pas de vies.");
                    return true;
                }
                if(!sender.hasPermission("heaven.staff")){
                	this.plugin.getDeathbanManager().setLives(playerUUID, selfLives - 1);
                }
                final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
                relation = ((playerFaction == null) ? Relation.ENEMY : playerFaction.getFactionRelation(this.plugin.getFactionManager().getPlayerFaction(targetUUID)));
                sender.sendMessage(ChatColor.YELLOW + "Tu as revive " + relation.toChatColour() + target.getName() + ChatColor.YELLOW + '.');
            }
            else {
                sender.sendMessage(ChatColor.YELLOW + "Tu as revive " + relation.toChatColour() + target.getName() + ChatColor.YELLOW + '.');
            }
        }
        else {
            sender.sendMessage(ChatColor.YELLOW + "Tu as revive " + ConfigurationService.ENEMY_COLOUR + target.getName() + ChatColor.YELLOW + '.');
        }
        factionTarget.removeDeathban();
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length != 2) {
            return Collections.emptyList();
        }
        final List<String> results = new ArrayList<String>();
        final Collection<FactionUser> factionUsers = this.plugin.getUserManager().getUsers().values();
        for (final FactionUser factionUser : factionUsers) {
            final Deathban deathban = factionUser.getDeathban();
            if (deathban != null) {
                if (!deathban.isActive()) {
                    continue;
                }
                final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(factionUser.getUserUUID());
                final String offlineName = offlinePlayer.getName();
                if (offlineName == null) {
                    continue;
                }
                results.add(offlinePlayer.getName());
            }
        }
        return results;
    }
}
