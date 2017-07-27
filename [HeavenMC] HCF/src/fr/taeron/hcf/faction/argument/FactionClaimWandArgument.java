package fr.taeron.hcf.faction.argument;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.heavenmc.core.util.command.CommandArgument;

import fr.taeron.hcf.HCF;
import fr.taeron.hcf.faction.claim.ClaimHandler;
import fr.taeron.hcf.faction.type.PlayerFaction;

public class FactionClaimWandArgument extends CommandArgument{
	
    private final HCF plugin;
    
    public FactionClaimWandArgument(final HCF plugin) {
        super("claimwand", "Claim une partie de la map", new String[] { "claimland" });
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName();
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "La console n'est pas supportée.");
            return true;
        }
        final Player player = (Player)sender;
        final UUID uuid = player.getUniqueId();
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(uuid);
        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "Tu n'as pas de faction.");
            return true;
        }
        if (playerFaction.isRaidable()) {
            sender.sendMessage(ChatColor.RED + "Tu ne peux pas claim pour ta faction car elle est actuellement raidable.");
            return true;
        }
        final PlayerInventory inventory = player.getInventory();
        if (inventory.contains(ClaimHandler.CLAIM_WAND)) {
            sender.sendMessage(ChatColor.RED + "Tu as déjà une claiming wand dans ton inventaire.");
            return true;
        }
        if (!inventory.addItem(new ItemStack[] { ClaimHandler.CLAIM_WAND }).isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Ton inventaire est plein, merci de faire une place pour recevoir ta claiming wand.");
            return true;
        }
        sender.sendMessage(ChatColor.YELLOW + "Une claiming wand a été ajoutée à ton inventaire, passe ton curseur dessus dans ton inventaire pour voir comment claim. Tu peux aussi" + ChatColor.YELLOW + " utiliser " + ChatColor.AQUA + '/' + label + " claimchunk" + ChatColor.YELLOW + '.');
        return true;
    }

	
	
}
