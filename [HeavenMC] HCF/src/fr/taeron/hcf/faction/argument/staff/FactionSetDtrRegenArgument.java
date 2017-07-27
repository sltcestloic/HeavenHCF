package fr.taeron.hcf.faction.argument.staff;

import fr.taeron.hcf.*;
import org.bukkit.command.*;
import org.heavenmc.core.util.JavaUtils;
import org.heavenmc.core.util.command.CommandArgument;
import org.bukkit.*;
import fr.taeron.hcf.faction.*;
import fr.taeron.hcf.faction.type.*;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;

public class FactionSetDtrRegenArgument extends CommandArgument{
	
    private final HCF plugin;
    
    public FactionSetDtrRegenArgument(final HCF plugin) {
        super("setdtrregen", "Sets the DTR cooldown of a faction.", new String[] { "setdtrregeneration" });
        this.plugin = plugin;
        this.permission = "command.faction.argument." + this.getName();
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <joueur|faction> <valeur>";
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Utilisation: " + this.getUsage(label));
            return true;
        }
        final long newRegen = JavaUtils.parse(args[2]);
        if (newRegen == -1L) {
            sender.sendMessage(ChatColor.RED + "Durée invalide, exemple de durée: 10m 1s");
            return true;
        }
        if (newRegen > FactionManager.MAX_DTR_REGEN_MILLIS) {
            sender.sendMessage(ChatColor.RED + "Valeur maximum:" + FactionManager.MAX_DTR_REGEN_WORDS + ".");
            return true;
        }
        final Faction faction = this.plugin.getFactionManager().getContainingFaction(args[1]);
        if (faction == null) {
        	sender.sendMessage(ChatColor.RED + "La faction (ou le joueur) " + args[1] + " n'existe pas.");
            return true;
        }
        if (!(faction instanceof PlayerFaction)) {
            sender.sendMessage(ChatColor.RED + "Cette faction est une faction système.");
            return true;
        }
        final PlayerFaction playerFaction = (PlayerFaction)faction;
        playerFaction.setRemainingRegenerationTime(newRegen);
        Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Tu as défini le DTR regen de " + faction.getName() + " à " + DurationFormatUtils.formatDurationWords(newRegen, true, true) + '.');
        return true;
    }
}
