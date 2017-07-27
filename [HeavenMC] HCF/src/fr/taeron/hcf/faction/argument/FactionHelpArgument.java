package fr.taeron.hcf.faction.argument;

import fr.taeron.hcf.faction.*;
import org.bukkit.command.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.heavenmc.core.util.BukkitUtils;
import org.heavenmc.core.util.JavaUtils;
import org.heavenmc.core.util.command.CommandArgument;

import com.google.common.collect.*;

public class FactionHelpArgument extends CommandArgument{
	
    private final FactionExecutor executor;
    private ImmutableMultimap<Integer, String> pages;
    
    public FactionHelpArgument(final FactionExecutor executor) {
        super("help", "View help on how to use factions.");
        this.executor = executor;
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName();
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 2) {
            this.showPage(sender, label, 1);
            return true;
        }
        final Integer page = JavaUtils.tryParseInt(args[1]);
        if (page == null) {
            sender.sendMessage(ChatColor.RED + "'" + args[1] + "' n'est pas un nombre valide.");
            return true;
        }
        this.showPage(sender, label, page);
        return true;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private void showPage(final CommandSender sender, final String label, final int pageNumber) {
        if (this.pages == null) {
            final boolean isPlayer = sender instanceof Player;
            int val = 1;
            int count = 0;
            final Multimap<Integer, String> pages = ArrayListMultimap.create();
            for (final CommandArgument argument : this.executor.getArguments()) {
                if (argument.equals((Object)this)) {
                    continue;
                }
                final String permission = argument.getPermission();
                if (permission != null && !sender.hasPermission(permission)) {
                    continue;
                }
                if (argument.isPlayerOnly() && !isPlayer) {
                    continue;
                }
                ++count;
                pages.get(val).add(ChatColor.YELLOW + "/" + label + ' ' + argument.getName() + ChatColor.AQUA + " - " + ChatColor.GRAY + argument.getDescription());
                if (count % 10 != 0) {
                    continue;
                }
                ++val;
            }
            this.pages = (ImmutableMultimap<Integer, String>)ImmutableMultimap.copyOf((Multimap)pages);
        }
        final int totalPageCount = this.pages.size() / 10 + 1;
        if (pageNumber < 1) {
            sender.sendMessage(ChatColor.RED + "Tu ne peux pas voir une page en dessous de la page 1.");
            return;
        }
        if (pageNumber > totalPageCount) {
            sender.sendMessage(ChatColor.RED + "Il n'y a que " + totalPageCount + " pages.");
            return;
        }
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        sender.sendMessage("                            §6Aide §7(Page " + pageNumber + '/' + totalPageCount + ")");
        if(pageNumber == 1){
        	sender.sendMessage("  §7» §6/f accept §7-> §eAccepter une invitation de faction");
        	sender.sendMessage("  §7» §6/f ally §7-> §eMettre une faction en allié");
        	sender.sendMessage("  §7» §6/f chat §7-> §eChanger de mode de chat");
        	sender.sendMessage("  §7» §6/f focus <faction> §7-> §eFocus une faction");
        	sender.sendMessage("  §7» §6/f claim §7-> §eClaim un territoire pour ta faction (Avec la claiming wand)");
        	sender.sendMessage("  §7» §6/f claimchunk §7-> §eClaim un chunk pour ta faction (16x16)");
        	sender.sendMessage("  §7» §6/f promote §7-> §eGrader un membre de ta faction en capitaine");
        	sender.sendMessage("  §7» §6/f rename §7-> §eRenommer ta faction");
        	sender.sendMessage("  §7» §6/f claims §7-> §eVoir les claims d'une faction");
        	sender.sendMessage("  §7» §6/f create §7-> §eCréer une faction");
        }
        if(pageNumber == 2){
        	sender.sendMessage("  §7» §6/f demote §7-> §eDégrader un officier de ta faction");
        	sender.sendMessage("  §7» §6/f deposit §7-> §eDéposer de l'argent dans la banque de ta faction");
        	sender.sendMessage("  §7» §6/f disband §7-> §eSupprimer ta faction");
        	sender.sendMessage("  §7» §6/f setdtrregen §7-> §eDéfinir le temps de regen de DTR d'une faction §c(Staff)");
        	sender.sendMessage("  §7» §6/f forcejoin §7-> §eRejoindre une faction sans y être invité §c(Staff)");
        	sender.sendMessage("  §7» §6/f forcekick §7-> §eExclure un joueur d'une faction §c(Staff)");
        	sender.sendMessage("  §7» §6/f forceleader §7-> §eMettre un joueur chef d'une faction §c(Staff)");
        	sender.sendMessage("  §7» §6/f forcepromote §7-> §ePromote un joueur dans une faction §c(Staff)");
        	sender.sendMessage("  §7» §6/f home §7-> §eSe téléporter au home de ta faction");
        	sender.sendMessage("  §7» §6/f invite §7-> §eInviter un joueur dans ta faction");
        }
        if(pageNumber == 3){
        	sender.sendMessage("  §7» §6/f invites §7-> §eVoir les invitations que tu as reçu");
        	sender.sendMessage("  §7» §6/f kick §7-> §eExclure un joueur de ta faction");
        	sender.sendMessage("  §7» §6/f leader §7-> §eGrader un membre de ta faction en tant que chef");
        	sender.sendMessage("  §7» §6/f leave §7-> §eQuitter ta faction");
        	sender.sendMessage("  §7» §6/f list §7-> §eVoir une liste des factions");
        	sender.sendMessage("  §7» §6/f map §7-> §eVoir les claims autour de toi");
        	sender.sendMessage("  §7» §6/f open §7-> §eOuvrir ta faction");
        	sender.sendMessage("  §7» §6/f remove §7-> §eSupprimer une faction §c(Staff)");
        	sender.sendMessage("  §7» §6/f rename §7-> §eRenommer ta faction");
        	sender.sendMessage("  §7» §6/f promote §7-> §eGrader un membre de ta faction en capitaine");
        }
        if(pageNumber == 4){
        	sender.sendMessage("  §7» §6/f setdtr §7-> §eDéfinir le DTR d'une faction §c(Staff)");
        	sender.sendMessage("  §7» §6/f setdeathbanmultiplier §7-> §eDéfinir le deathban multiplier d'une faction §c(Staff)");
        	sender.sendMessage("  §7» §6/f leader §7-> §eGrader un membre de ta faction en tant que chef");
        	sender.sendMessage("  §7» §6/f leave §7-> §eQuitter ta faction");
        	sender.sendMessage("  §7» §6/f list §7-> §eVoir une liste des factions");
        	sender.sendMessage("  §7» §6/f map §7-> §eVoir les claims autour de toi");
        	sender.sendMessage("  §7» §6/f open §7-> §eOuvrir ta faction au public");
        	sender.sendMessage("  §7» §6/f remove §7-> §eSupprimer une faction §c(Staff)");
        	sender.sendMessage("  §7» §6/f clearclaims §7-> §eSupprimer les claims d'une faction §c(Staff)");
        	sender.sendMessage("  §7» §6/f claimfor §7-> §eClaim pour une autre faction §c(Staff)");
        	sender.sendMessage("  §7» §6/f chatspy §7-> §eVoir le chat d'une autre faction §c(Staff)");
        }
        
        /*for (final String message : this.pages.get(pageNumber)) {
            sender.sendMessage("  " + message);
        }*/
        sender.sendMessage(" §6Pour voir les autres pages, fait " + ChatColor.YELLOW + '/' + label + ' ' + this.getName() + " <page#>" + ChatColor.GOLD + '.');
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
    }
}
