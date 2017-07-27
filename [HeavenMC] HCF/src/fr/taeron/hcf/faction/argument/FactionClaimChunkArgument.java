package fr.taeron.hcf.faction.argument;

import fr.taeron.hcf.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.heavenmc.core.util.command.CommandArgument;

import fr.taeron.hcf.faction.struct.*;
import fr.taeron.hcf.faction.claim.*;
import fr.taeron.hcf.faction.type.*;
import org.bukkit.*;

public class FactionClaimChunkArgument extends CommandArgument{
	
    private final HCF plugin;
    
    public FactionClaimChunkArgument(final HCF plugin) {
        super("claimchunk", "Claim un chunk", new String[] { "chunkclaim" });
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
        if (playerFaction.isRaidable()) {
        	sender.sendMessage(ChatColor.RED + "Tu ne peux pas claim pour ta faction car elle est actuellement raidable.");
            return true;
        }
        if (playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
            sender.sendMessage(ChatColor.RED + "Tu doit être au moins officier pour pouvoir claim.");
            return true;
        }
        final Location location = player.getLocation();
        this.plugin.getClaimHandler().tryPurchasing(player, new Claim(playerFaction, location.clone().add(7.0, 0.0, 7.0), location.clone().add(-7.0, 256.0, -7.0)));
        return true;
    }
}
