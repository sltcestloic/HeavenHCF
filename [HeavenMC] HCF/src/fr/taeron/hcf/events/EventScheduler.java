package fr.taeron.hcf.events;

import java.time.*;

import fr.taeron.hcf.*;
import java.util.*;
import java.nio.charset.*;
import org.bukkit.*;
import org.heavenmc.core.util.JavaUtils;

import java.io.*;
import java.util.concurrent.*;

public class EventScheduler
{
    static final String FILE_NAME = "event-schedules.txt";
    private static final long QUERY_DELAY;
    private final Map<LocalDateTime, String> scheduleMap;
    private final HCF plugin;
    private long lastQuery;
    
    public EventScheduler(final HCF plugin) {
        this.scheduleMap = new LinkedHashMap<LocalDateTime, String>();
        this.plugin = plugin;
        this.reloadSchedules();
    }
    
    private static LocalDateTime getFromString(final String input) {
        if (!input.contains(",")) {
            return null;
        }
        final String[] args = input.split(",");
        if (args.length != 5) {
            return null;
        }
        final Integer year = JavaUtils.tryParseInt(args[0]);
        if (year == null) {
            return null;
        }
        final Integer month = JavaUtils.tryParseInt(args[1]);
        if (month == null) {
            return null;
        }
        final Integer day = JavaUtils.tryParseInt(args[2]);
        if (day == null) {
            return null;
        }
        final Integer hour = JavaUtils.tryParseInt(args[3]);
        if (hour == null) {
            return null;
        }
        final Integer minute = JavaUtils.tryParseInt(args[4]);
        if (minute == null) {
            return null;
        }
        return LocalDateTime.of(year, month, day, hour, minute);
    }
    
    private void reloadSchedules() {
        try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(this.plugin.getDataFolder(), "event-schedules.txt")), StandardCharsets.UTF_8))) {
            String currentLine;
            while ((currentLine = bufferedReader.readLine()) != null) {
                if (currentLine.startsWith("#")) {
                    continue;
                }
                currentLine = currentLine.trim();
                final String[] args = currentLine.split(":");
                if (args.length != 2) {
                    continue;
                }
                final LocalDateTime localDateTime = getFromString(args[0]);
                if (localDateTime == null) {
                    continue;
                }
                this.scheduleMap.put(localDateTime, args[1]);
            }
        }
        catch (FileNotFoundException ex2) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Le fichier 'event-schedules.txt' est introuvable, impossible de programmer les prochains KoTH.");
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public Map<LocalDateTime, String> getScheduleMap() {
        final long millis = System.currentTimeMillis();
        if (millis - EventScheduler.QUERY_DELAY > this.lastQuery) {
            this.reloadSchedules();
            this.lastQuery = millis;
        }
        return this.scheduleMap;
    }
    
    static {
        QUERY_DELAY = TimeUnit.MINUTES.toMillis(2);
    }
}
