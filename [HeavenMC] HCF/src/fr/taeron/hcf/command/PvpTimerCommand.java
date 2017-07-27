package fr.taeron.hcf.command;

import com.google.common.collect.*;
import fr.taeron.hcf.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.heavenmc.core.util.BukkitUtils;
import org.bukkit.*;
import fr.taeron.hcf.timer.type.*;

import java.util.*;

public class PvpTimerCommand implements CommandExecutor, TabCompleter
{
    private static final ImmutableList<String> COMPLETIONS;
    private final HCF plugin;
    
    public PvpTimerCommand(final HCF plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        final Player player = (Player)sender;
        final PvpProtectionTimer pvpTimer = this.plugin.getTimerManager().pvpProtectionTimer;
        if (args.length < 1) {
            this.printUsage(sender, label, pvpTimer);
            return true;
        }
        if (args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("off")) {
        	if(args.length < 2){
	            if (pvpTimer.getRemaining(player) > 0L) {
	                sender.sendMessage(ChatColor.RED + "Ton " + pvpTimer.getDisplayName() + ChatColor.RED + " est désormais désactivé");
	                pvpTimer.clearCooldown(player);
	                return true;
	            }
	            if (pvpTimer.getLegible().remove(player.getUniqueId())) {
	                player.sendMessage(ChatColor.YELLOW + "Tu n'auras plus ton " + pvpTimer.getDisplayName() + ChatColor.YELLOW + " quand tu quitteras le spawn");
	                return true;
	            }
	            sender.sendMessage(ChatColor.RED + "Ton " + pvpTimer.getDisplayName() + ChatColor.RED + " n'est pas actif");
	            return true;
        	} else {
        		if(!sender.hasPermission("heaven.staff")){
        			sender.sendMessage("§cTu n'as pas la permission de retirer le PvP Timer d'un joueur.");
        			return false;
        		}
        		Player p = Bukkit.getPlayer(args[1]);
        		if(p == null){
        			sender.sendMessage("§c" + args[1] + " n'est pas connecté.");
        			return false;
        		}
        		if(pvpTimer.getRemaining(p) > 0L){
        			pvpTimer.clearCooldown(p);
        			Command.broadcastCommandMessage(sender, "§aTu as retiré le PvP Timer de " + args[1]);
        		} else {
        			sender.sendMessage("§c" + args[1] + " n'as pas de PvP Timer actif");
        		}
        		return true;
        	}
        }
        else {
            if (!args[0].equalsIgnoreCase("remaining") && !args[0].equalsIgnoreCase("time") && !args[0].equalsIgnoreCase("left") && !args[0].equalsIgnoreCase("check")) {
                this.printUsage(sender, label, pvpTimer);
                return true;
            }
            final long remaining = pvpTimer.getRemaining(player);
            if (remaining <= 0L) {
            	sender.sendMessage(ChatColor.RED + "Ton " + pvpTimer.getDisplayName() + ChatColor.RED + " n'est pas actif");
                return true;
            }
            sender.sendMessage(ChatColor.YELLOW + "Ton " + pvpTimer.getDisplayName() + ChatColor.YELLOW + " est actif pendant encore " + ChatColor.BOLD + HCF.getRemaining(remaining, true, false) + ChatColor.YELLOW + (pvpTimer.isPaused(player) ? " et est actuellement désactivé" : "") + '.');
            return true;
        }
    }
     
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(args.length == 1){
        	return (args.length == 1) ? BukkitUtils.getCompletions(args, (List)PvpTimerCommand.COMPLETIONS) : Collections.emptyList();
        } else {
        	return null;
        }
    }
    
    private void printUsage(final CommandSender sender, final String label, final PvpProtectionTimer pvpTimer) {
        sender.sendMessage("§6§l§m---*-----------------------------------*---");
        sender.sendMessage("§6§6Aide §7(PvPTimer)");
        sender.sendMessage(" §6* " + ChatColor.YELLOW + "/" + label + " enable " + ChatColor.GRAY + " -> " + "Enleve ton PvPTimer");
        sender.sendMessage(" §6* " + ChatColor.YELLOW + "/" + label + " time " + ChatColor.GRAY + " -> " + "Voir le temps restant de ton PvPTimer");
        sender.sendMessage(" §6* " + ChatColor.YELLOW + "/lives " + ChatColor.GRAY + " -> " + "Voir les commandes pour utiliser les vies et se servir du deathban");
        sender.sendMessage("§6§l§m---*-----------------------------------*---");
    }
    
    static {
        COMPLETIONS = ImmutableList.of("enable", "time");
    }
}
