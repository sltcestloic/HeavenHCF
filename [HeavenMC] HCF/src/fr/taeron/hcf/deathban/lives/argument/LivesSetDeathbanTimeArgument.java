package fr.taeron.hcf.deathban.lives.argument;

import org.bukkit.command.*;
import org.heavenmc.core.util.JavaUtils;
import org.heavenmc.core.util.command.CommandArgument;
import org.bukkit.*;
import fr.taeron.hcf.*;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;

import java.util.*;

public class LivesSetDeathbanTimeArgument extends CommandArgument{
	
    public LivesSetDeathbanTimeArgument() {
        super("setdeathbantime", "Définir la durée du deathban de base");
        this.permission = "command.lives.argument." + this.getName();
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <durée>";
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Utilisation: " + this.getUsage(label));
            return true;
        }
        final long duration = JavaUtils.parse(args[1]);
        if (duration == -1L) {
            sender.sendMessage(ChatColor.RED + "Durée invalide. Exemple de durée: 10m 1s");
            return true;
        }
        ConfigurationService.DEFAULT_DEATHBAN_DURATION = duration;
        Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Le temps de deathban par défaut est désormais de " + DurationFormatUtils.formatDurationWords(duration, true, true) + " (not including multipliers, etc).");
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return Collections.emptyList();
    }
}
