package fr.taeron.hcf.faction.argument;

import fr.taeron.hcf.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.heavenmc.core.util.command.CommandArgument;
import org.bukkit.*;
import fr.taeron.hcf.faction.struct.*;
import fr.taeron.hcf.faction.type.*;

public class FactionDisbandArgument extends CommandArgument{
	
    private final HCF plugin;
    
    public FactionDisbandArgument(final HCF plugin) {
        super("disband", "Disband your faction.");
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName();
    }
    
    @SuppressWarnings("deprecation")
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "La console n'est pas supportée.");
            return true;
        }
        final Player player = (Player)sender;
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "Tu n'as pas de faction.");
            return true;
        }
        if (playerFaction.isRaidable() && !this.plugin.getEotwHandler().isEndOfTheWorld()) {
            sender.sendMessage(ChatColor.RED + "Tu ne peux pas disband ta faction car elle est raidable.");
            return true;
        }
        if (playerFaction.getMember(player.getUniqueId()).getRole() != Role.LEADER) {
            sender.sendMessage(ChatColor.RED + "Tu doit être chef de faction pour faire ça.");
            return true;
        }
        if(this.plugin.getEotwHandler().isEndOfTheWorld()){
        	sender.sendMessage("§cTu ne peux pas disbant ta faction durant l'EOTW.");
        	return false;
        }
        this.plugin.getFactionManager().removeFaction(playerFaction, sender);
        return true;
    }
}
