package fr.taeron.hcf.faction.argument;

import fr.taeron.hcf.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.heavenmc.core.util.command.CommandArgument;
import org.bukkit.*;
import fr.taeron.hcf.faction.struct.*;
import fr.taeron.hcf.faction.type.*;
import fr.taeron.hcf.faction.*;

public class FactionOpenArgument extends CommandArgument{
	
    private final HCF plugin;
    
    public FactionOpenArgument(HCF plugin) {
        super("open", "Opens the faction to the public.");
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName();
    }
    
    @SuppressWarnings("deprecation")
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        final Player player = (Player)sender;
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "Tu n'as pas de faction.");
            return true;
        }
        final FactionMember factionMember = playerFaction.getMember(player.getUniqueId());
        if (factionMember.getRole() != Role.LEADER) {
            sender.sendMessage(ChatColor.RED + "Tu dois être chef de ta faction.");
            return true;
        }
        final boolean newOpen = !playerFaction.isOpen();
        playerFaction.setOpen(newOpen);
        playerFaction.broadcast(ChatColor.YELLOW + sender.getName() + " a " + (newOpen ? (ChatColor.GREEN + "ouvert") : (ChatColor.RED + "fermé")) + ChatColor.YELLOW + " la faction au public.");
        return true;
    }
}
