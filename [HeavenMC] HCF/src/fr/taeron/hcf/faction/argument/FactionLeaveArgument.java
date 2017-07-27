package fr.taeron.hcf.faction.argument;

import fr.taeron.hcf.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.heavenmc.core.util.command.CommandArgument;
import org.bukkit.*;
import fr.taeron.hcf.faction.struct.*;
import fr.taeron.hcf.faction.type.*;
import java.util.*;

public class FactionLeaveArgument extends CommandArgument{
	
    private final HCF plugin;
    
    public FactionLeaveArgument(final HCF plugin) {
        super("leave", "Leave your current faction.");
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName();
    }
    
    @SuppressWarnings("deprecation")
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can leave faction.");
            return true;
        }
        final Player player = (Player)sender;
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "Tu n'as pas de faction.");
            return true;
        }
        final UUID uuid = player.getUniqueId();
        if (playerFaction.getMember(uuid).getRole() == Role.LEADER) {
            sender.sendMessage(ChatColor.RED + "Tu es chef de ta faction, tu ne peux pas la quitter. Utilise " + ChatColor.GOLD + '/' + label + " disband" + ChatColor.RED + " ou " + ChatColor.GOLD + '/' + label + " leader" + ChatColor.RED + '.');
            return true;
        }
        if (playerFaction.setMember(player, null)) {
            sender.sendMessage(ChatColor.YELLOW + "Tu as quitté la faction.");
            playerFaction.broadcast(Relation.ENEMY.toChatColour() + sender.getName() + ChatColor.YELLOW + " a quitté la faction.");
        }
        return true;
    }
}
