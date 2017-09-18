package fr.taeron.hcf.faction.argument;

import org.bukkit.command.*;
import org.bukkit.*;
import org.heavenmc.core.util.BukkitUtils;
import org.heavenmc.core.util.command.CommandArgument;


public class FactionHelpArgument extends CommandArgument{
	
    
    public FactionHelpArgument() {
        super("help", "View help on how to use factions.");
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName();
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        this.showPage(sender);
        return true;
    }
    
	private void showPage(final CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        sender.sendMessage("§6§lfaction §7- §eAide");
        sender.sendMessage(ChatColor.GOLD + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        sender.sendMessage("§dGeneral");
        sender.sendMessage(" §e/f create <faction> §6» §fCrée une faction");
        sender.sendMessage(" §e/f accept <faction> §6» §fAccepter l'invitation é rejoindre une faction");
        sender.sendMessage(" §e/f leave §6» §fQuitter sa faction");
        sender.sendMessage(" §e/f home §6» §fSe téléporter au home de sa faction");
        sender.sendMessage(" §e/f deposit §6» §fDeposer de l'argent dans sa faction");
        sender.sendMessage("§dInformation");
        sender.sendMessage(" §e/f show <faction> §6» §fMontrer les informations d'une faction");
        sender.sendMessage(" §e/f map §6» §fAfficher les pilliers de délimitation des claims");
        sender.sendMessage(" §e/f list §6» §fVoir la liste des factions possédant le plus de joueurs connéctés");
        sender.sendMessage(" §e/f top §6» §fVoir la liste des factions possédant le plus de kills");
        sender.sendMessage("§dCapitaine");
        sender.sendMessage(" §e/f kick <joueur> §6» §fKick un joueur de la faction");
        sender.sendMessage(" §e/f withdraw <joueur> §6» §fKick un joueur de la faction");
        sender.sendMessage(" §e/f invite <joueur> §6» §fInviter un joueur dans la faction");
        sender.sendMessage("§dChef");
        sender.sendMessage(" §e/f disband §6» §fDissoudre la faction");
        sender.sendMessage(" §e/f rename §6» §fChanger le nom de la faction");
        sender.sendMessage(ChatColor.GOLD + BukkitUtils.STRAIGHT_LINE_DEFAULT);    }
}
