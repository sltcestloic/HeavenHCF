package fr.taeron.hcf.deathban.lives.argument;

import org.bukkit.command.*;
import org.heavenmc.core.util.command.CommandArgument;

import fr.taeron.hcf.*;
import com.google.common.base.*;
import fr.taeron.hcf.deathban.*;
import org.bukkit.*;
import fr.taeron.hcf.user.*;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;

import java.util.*;

public class LivesCheckDeathbanArgument extends CommandArgument{
	
    private final HCF plugin;
    
    public LivesCheckDeathbanArgument(final HCF plugin) {
        super("checkdeathban", "Voir la cause du deathban d'un joueur");
        this.plugin = plugin;
        this.permission = "command.lives.argument." + this.getName();
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <joueur>";
    }
    
    @SuppressWarnings("deprecation")
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Utilisation: " + this.getUsage(label));
            return true;
        }
        final OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(ChatColor.RED + args[1] + "ne s'est jamais connecté.");
            return true;
        }
        final Deathban deathban = this.plugin.getUserManager().getUser(target.getUniqueId()).getDeathban();
        if (deathban == null || !deathban.isActive()) {
            sender.sendMessage(ChatColor.RED + target.getName() + " n'est pas deathban.");
            return true;
        }
        sender.sendMessage(ChatColor.YELLOW + "Cause du deathban de " + target.getName() + '.');
        sender.sendMessage(ChatColor.AQUA + " Heure: " + DateTimeFormats.HR_MIN.format(deathban.getCreationMillis()));
        sender.sendMessage(ChatColor.AQUA + " Durée: " + DurationFormatUtils.formatDurationWords(deathban.getExpiryMillis() - deathban.getCreationMillis(), true, true));
        final Location location = deathban.getDeathPoint();
        if (location != null) {
            sender.sendMessage(ChatColor.AQUA + " Position: (" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ") - " + location.getWorld().getName());
        }
        sender.sendMessage(ChatColor.AQUA + " Raison: " + Strings.nullToEmpty(deathban.getReason()));
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length != 2) {
            return Collections.emptyList();
        }
        final List<String> results = new ArrayList<String>();
        for (final FactionUser factionUser : this.plugin.getUserManager().getUsers().values()) {
            final Deathban deathban = factionUser.getDeathban();
            if (deathban != null && deathban.isActive()) {
                final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(factionUser.getUserUUID());
                final String name = offlinePlayer.getName();
                if (name == null) {
                    continue;
                }
                results.add(name);
            }
        }
        return results;
    }
}
