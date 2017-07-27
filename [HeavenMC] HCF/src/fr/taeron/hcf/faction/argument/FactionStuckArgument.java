package fr.taeron.hcf.faction.argument;

import fr.taeron.hcf.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.heavenmc.core.util.command.CommandArgument;
import org.bukkit.*;
import fr.taeron.hcf.timer.type.*;

public class FactionStuckArgument extends CommandArgument{
	
    private final HCF plugin;
    
    public FactionStuckArgument(final HCF plugin) {
        super("stuck", "Teleport to a safe position.", new String[] { "trap", "trapped" });
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName();
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        final Player player = (Player)sender;
        if (player.getWorld().getEnvironment() != World.Environment.NORMAL) {
            sender.sendMessage(ChatColor.RED + "Cette commande ne fonctionne que dans l'overworld.");
            return true;
        }
        final StuckTimer stuckTimer = this.plugin.getTimerManager().stuckTimer;
        if (!stuckTimer.setCooldown(player, player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Tu es déjà en stuck timer.");
            return true;
        }
        sender.sendMessage(ChatColor.YELLOW + "Ton timer de "+ stuckTimer.getDisplayName() + ChatColor.YELLOW + " a commencé. " + "Tu seras téléporté dans " + ChatColor.LIGHT_PURPLE + HCF.getRemaining(stuckTimer.getRemaining(player), true, false) + ChatColor.YELLOW + ". " + "La téléportation sera anullée si tu bouge de plus de " + 5 + " blocks.");
        return true;
    }
}
