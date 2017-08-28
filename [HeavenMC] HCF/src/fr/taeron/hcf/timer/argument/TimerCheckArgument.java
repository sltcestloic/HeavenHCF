package fr.taeron.hcf.timer.argument;

import org.bukkit.command.*;
import org.bukkit.*;
import fr.taeron.hcf.timer.*;
import fr.taeron.hcf.timer.Timer;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;

import org.bukkit.scheduler.*;
import org.heavenmc.core.util.UUIDFetcher;
import org.heavenmc.core.util.command.CommandArgument;

import fr.taeron.hcf.*;
import org.bukkit.plugin.*;
import java.util.*;

public class TimerCheckArgument extends CommandArgument{
	
    private final HCF plugin;
    
    public TimerCheckArgument(final HCF plugin) {
        super("check", "Check remaining timer time");
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <timer> <joueur>";
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Utilisation: " + this.getUsage(label));
            return true;
        }
        PlayerTimer temporaryTimer = null;
        for (final Timer timer : this.plugin.getTimerManager().getTimers()) {
            if (timer instanceof PlayerTimer && timer.getName().equalsIgnoreCase(args[1])) {
                temporaryTimer = (PlayerTimer)timer;
                break;
            }
        }
        if (temporaryTimer == null) {
            sender.sendMessage(ChatColor.RED + "Le timer '" + args[1] + "' est introuvable.");
            return true;
        }
        final PlayerTimer playerTimer = temporaryTimer;
        new BukkitRunnable() {
            public void run() {
                UUID uuid;
                try {
                    uuid = UUIDFetcher.getUUID(args[2]);
                }
                catch (Exception ex) {
                    sender.sendMessage(ChatColor.GOLD + "Le joueur '" + ChatColor.WHITE + args[2] + ChatColor.GOLD + "' est introuvable.");
                    return;
                }
                final long remaining = playerTimer.getRemaining(uuid);
                sender.sendMessage(ChatColor.YELLOW + args[2] + " a le timer " + playerTimer.getName() + " pendant " + DurationFormatUtils.formatDurationWords(remaining, true, true));
            }
        }.runTaskAsynchronously((Plugin)this.plugin);
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return (args.length == 2) ? null : Collections.emptyList();
    }
}
