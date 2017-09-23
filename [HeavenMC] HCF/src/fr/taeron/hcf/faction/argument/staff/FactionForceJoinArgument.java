package fr.taeron.hcf.faction.argument.staff;

import fr.taeron.hcf.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.heavenmc.core.util.command.CommandArgument;

import fr.taeron.hcf.faction.struct.*;
import fr.taeron.hcf.faction.*;
import fr.taeron.hcf.faction.type.*;
import org.bukkit.*;
import java.util.*;

public class FactionForceJoinArgument extends CommandArgument{
	
    private final HCF plugin;
    
    public FactionForceJoinArgument(final HCF plugin) {
        super("forcejoin", "Forcefully join a faction.");
        this.plugin = plugin;
        this.permission = "command.faction.argument." + this.getName();
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <factionName>";
    }
    
    @SuppressWarnings("deprecation")
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can join factions.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Utilisation: " + this.getUsage(label));
            return true;
        }
        final Player player = (Player)sender;
        PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction != null) {
            sender.sendMessage(ChatColor.RED + "Tu es déjà dans une faction.");
            return true;
        }
        final Faction faction = this.plugin.getFactionManager().getContainingFaction(args[1]);
        if (faction == null) {
        	sender.sendMessage(ChatColor.RED + "La faction (ou le joueur) " + args[1] + " n'existe pas.");
            return true;
        }
        if (!(faction instanceof PlayerFaction)) {
            sender.sendMessage(ChatColor.RED + "Tu ne peux rejoindre que des factions de joueurs.");
            return true;
        }
        playerFaction = (PlayerFaction)faction;
        if (playerFaction.setMember(player, new FactionMember(player, ChatChannel.PUBLIC, Role.MEMBER), true)) {
            playerFaction.broadcast("§c(Staff) " + ChatColor.GOLD.toString() + ChatColor.BOLD + sender.getName() + " a rejoint la faction.");
        }
        return true;
    }
    
	public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length != 2 || !(sender instanceof Player)) {
            return Collections.emptyList();
        }
        if (args[1].isEmpty()) {
            return null;
        }
        final Player player = (Player)sender;
        final List<String> results = new ArrayList<String>(this.plugin.getFactionManager().getFactionNameMap().keySet());
        for (final Player target : Bukkit.getOnlinePlayers()) {
            if (player.canSee(target) && !results.contains(target.getName())) {
                results.add(target.getName());
            }
        }
        return results;
    }
}
