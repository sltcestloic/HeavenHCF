package fr.taeron.hcf.events.koth.arguments;

import org.bukkit.command.*;
import org.heavenmc.core.util.command.CommandArgument;
import org.bukkit.*;
import fr.taeron.hcf.*;
import net.minecraft.util.org.apache.commons.lang3.text.WordUtils;

import java.time.*;
import java.time.format.*;
import java.util.concurrent.*;
import java.util.*;

public class KothNextArgument extends CommandArgument{
	
    private final HCF plugin;
    
    public KothNextArgument(final HCF plugin) {
        super("next", "Voir le prochain KoTH prévu");
        this.plugin = plugin;
        this.permission = "command.koth.argument." + this.getName();
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName();
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        final long millis = System.currentTimeMillis();
        sender.sendMessage(ChatColor.GOLD + "The server time is currently " + ChatColor.YELLOW + DateTimeFormats.DAY_MTH_HR_MIN_AMPM.format(millis) + ChatColor.GOLD + '.');
        final Map<LocalDateTime, String> scheduleMap = this.plugin.eventScheduler.getScheduleMap();
        if (scheduleMap.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "There is not an event schedule for after now.");
            return true;
        }
        final LocalDateTime now = LocalDateTime.now(DateTimeFormats.SERVER_ZONE_ID);
        for (final Map.Entry<LocalDateTime, String> entry : scheduleMap.entrySet()) {
            final LocalDateTime scheduleDateTime = entry.getKey();
            if (now.isAfter(scheduleDateTime)) {
                continue;
            }
            final String monthName = scheduleDateTime.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            final String weekName = scheduleDateTime.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            sender.sendMessage(ChatColor.DARK_AQUA + WordUtils.capitalizeFully((String)entry.getValue()) + ChatColor.GRAY + " is the next event: " + ChatColor.AQUA + weekName + ' ' + scheduleDateTime.getDayOfMonth() + ' ' + monthName + ChatColor.DARK_AQUA + " (" + DateTimeFormats.HR_MIN_AMPM.format(TimeUnit.HOURS.toMillis(scheduleDateTime.getHour()) + TimeUnit.MINUTES.toMillis(scheduleDateTime.getMinute())) + ')');
            return true;
        }
        sender.sendMessage(ChatColor.RED + "There is not an event scheduled after now.");
        return true;
    }
}
