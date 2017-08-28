package fr.taeron.hcf.events.koth.arguments;

import org.bukkit.command.*;
import org.heavenmc.core.util.BukkitUtils;
import org.heavenmc.core.util.command.CommandArgument;

import fr.taeron.hcf.*;
import net.minecraft.util.org.apache.commons.lang3.text.WordUtils;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;

import java.time.*;
import java.time.format.*;
import org.bukkit.*;
import java.time.temporal.*;
import java.util.*;

public class KothScheduleArgument extends CommandArgument{
	
    private static final DateTimeFormatter HHMMA;
    private final HCF plugin;
    
    public KothScheduleArgument(final HCF plugin) {
        super("schedule", "Voir les KoTH à venir");
        this.plugin = plugin;
        this.aliases = new String[] { "info", "i", "time" };
        this.permission = "command.koth.argument." + this.getName();
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName();
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        final LocalDateTime now = LocalDateTime.now(DateTimeFormats.SERVER_ZONE_ID);
        final int currentDay = now.getDayOfYear();
        final Map<LocalDateTime, String> scheduleMap = this.plugin.eventScheduler.getScheduleMap();
        final List<String> shownEvents = new ArrayList<String>();
        for (final Map.Entry<LocalDateTime, String> entry : scheduleMap.entrySet()) {
            final LocalDateTime scheduleDateTime = entry.getKey();
            if (scheduleDateTime.isAfter(now)) {
                final int dayDifference = scheduleDateTime.getDayOfYear() - currentDay;
                if (dayDifference > 1) {
                    continue;
                }
                final String eventName = entry.getValue();
                final String monthName = scheduleDateTime.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
                final String weekName = scheduleDateTime.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
                final ChatColor colour = (dayDifference == 0) ? ChatColor.GREEN : ChatColor.AQUA;
                shownEvents.add("  " + colour + WordUtils.capitalizeFully(eventName) + ": " + ChatColor.YELLOW + weekName + ' ' + scheduleDateTime.getDayOfMonth() + ' ' + monthName + ChatColor.RED + " (" + KothScheduleArgument.HHMMA.format(scheduleDateTime) + ')' + ChatColor.GRAY + " - " + ChatColor.GOLD + DurationFormatUtils.formatDuration(now.until(scheduleDateTime, ChronoUnit.MILLIS), "d'd' H'h' mm'm'"));
            }
        }
        if (shownEvents.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Les KoTH ont lieu tous les 5 jours.");
            return true;
        }
        final String monthName2 = WordUtils.capitalizeFully(now.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
        final String weekName2 = WordUtils.capitalizeFully(now.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        sender.sendMessage(ChatColor.GRAY + "Server time is currently " + ChatColor.WHITE + weekName2 + ' ' + now.getDayOfMonth() + ' ' + monthName2 + ' ' + KothScheduleArgument.HHMMA.format(now) + ChatColor.GRAY + '.');
        sender.sendMessage((String[])shownEvents.toArray(new String[shownEvents.size()]));
        sender.sendMessage(ChatColor.GRAY + "For more info about King of the Hill, use " + ChatColor.WHITE + '/' + label + " help" + ChatColor.GRAY + '.');
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        return true;
    }
    
    static {
        HHMMA = DateTimeFormatter.ofPattern("h:mma");
    }
}
