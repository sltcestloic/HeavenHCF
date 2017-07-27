package fr.taeron.hcf.faction.argument;

import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.heavenmc.core.util.JavaUtils;
import org.heavenmc.core.util.command.CommandArgument;
import org.bukkit.*;
import fr.taeron.hcf.faction.struct.*;
import fr.taeron.hcf.*;
import fr.taeron.hcf.faction.type.*;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;

import java.util.concurrent.*;

public class FactionRenameArgument extends CommandArgument{
	
    private static final long FACTION_RENAME_DELAY_MILLIS;
    static final String FACTION_RENAME_DELAY_WORDS;
    private final HCF plugin;
    
    public FactionRenameArgument(final HCF plugin) {
        super("rename", "Change the name of your faction.");
        this.plugin = plugin;
        this.aliases = new String[] { "changename", "setname" };
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <newFactionName>";
    }
    
    @SuppressWarnings("deprecation")
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can create faction.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Utilisation: " + this.getUsage(label));
            return true;
        }
        final Player player = (Player)sender;
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "Tu n'as pas de faction.");
            return true;
        }
        if (playerFaction.getMember(player.getUniqueId()).getRole() != Role.LEADER) {
            sender.sendMessage(ChatColor.RED + "Tu n'es pas chef de ta faction.");
            return true;
        }
        final String newName = args[1];
        if (ConfigurationService.DISALLOWED_FACTION_NAMES.contains(newName.toLowerCase()) && !player.isOp()) {
            sender.sendMessage(ChatColor.RED + "Le nom '" + newName + "' n'est pas autorisé.");
            return true;
        }
        if (newName.length() < 3) {
            sender.sendMessage(ChatColor.RED + "Le nom de faction doit faire au moins " + 3 + " charactères.");
            return true;
        }
        if (newName.length() > 16) {
            sender.sendMessage(ChatColor.RED + "Le nom de faction ne doit pas faire  " + 16 + " charactères.");
            return true;
        }
        if (!JavaUtils.isAlphanumeric(newName)) {
            sender.sendMessage(ChatColor.RED + "Les noms de factions doivent être alphanumériques.");
            return true;
        }
        if (this.plugin.getFactionManager().getFaction(newName) != null) {
            sender.sendMessage(ChatColor.RED + "La faction " + newName + ChatColor.RED + " existe déjà.");
            return true;
        }
        final long difference = playerFaction.lastRenameMillis - System.currentTimeMillis() + FactionRenameArgument.FACTION_RENAME_DELAY_MILLIS;
        if (!player.isOp() && difference > 0L) {
            player.sendMessage(ChatColor.RED + "Merci d'attendre " + DurationFormatUtils.formatDurationWords(difference, true, true) + " avant de rename ta faction.");
            return true;
        }
        playerFaction.setName(args[1], sender);
        return true;
    }
    
    static {
        FACTION_RENAME_DELAY_MILLIS = TimeUnit.SECONDS.toMillis(15L);
        FACTION_RENAME_DELAY_WORDS = DurationFormatUtils.formatDurationWords(FactionRenameArgument.FACTION_RENAME_DELAY_MILLIS, true, true);
    }
}
