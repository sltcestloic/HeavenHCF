package fr.taeron.hcf.faction.argument;

import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.heavenmc.core.util.command.CommandArgument;

import fr.taeron.hcf.faction.struct.*;
import fr.taeron.hcf.faction.claim.*;
import fr.taeron.hcf.*;
import fr.taeron.hcf.faction.type.*;
import fr.taeron.hcf.faction.*;
import org.bukkit.*;

public class FactionSetHomeArgument extends CommandArgument{
	
    private final HCF plugin;
    
    public FactionSetHomeArgument(final HCF plugin) {
        super("sethome", "Sets the faction home location.");
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
        final FactionMember factionMember = playerFaction.getMember(player);
        if (factionMember.getRole() == Role.MEMBER) {
            sender.sendMessage(ChatColor.RED + "Tu dois être officier de faction pour définir le home.");
            return true;
        }
        final Location location = player.getLocation();
        boolean insideTerritory = false;
        for (final Claim claim : playerFaction.getClaims()) {
            if (claim.contains(location)) {
                insideTerritory = true;
                break;
            }
        }
        if (!insideTerritory) {
            player.sendMessage(ChatColor.RED + "Tu n'es pas dans ton territoire.");
            return true;
        }
        playerFaction.setHome(location);
        playerFaction.broadcast(ConfigurationService.TEAMMATE_COLOUR + factionMember.getRole().getAstrix() + sender.getName() + ChatColor.YELLOW + " a défini le home de faction.");
        return true;
    }
}
