package fr.taeron.hcf.scoreboard.provider;

import java.text.*;
import org.bukkit.entity.*;
import org.heavenmc.core.Core;
import org.heavenmc.core.command.module.staffmode.StaffMode;
import org.heavenmc.core.scoreboard.provider.ScoreboardProvider;
import org.heavenmc.core.util.BukkitUtils;
import fr.taeron.hcf.pvpclass.bard.*;
import fr.taeron.hcf.pvpclass.type.*;
import fr.taeron.hcf.pvpclass.archer.*;
import fr.taeron.hcf.*;
import fr.taeron.hcf.timer.type.*;
import fr.taeron.hcf.user.FactionUser;
import fr.taeron.hcf.timer.*;
import fr.taeron.hcf.timer.Timer;
import fr.taeron.hcf.faction.type.*;
import org.bukkit.command.*;
import org.bukkit.*;
import fr.taeron.hcf.pvpclass.*;
import fr.taeron.hcf.events.*;
import fr.taeron.hcf.events.eotw.*;
import fr.taeron.hcf.events.factions.*;
import fr.taeron.hcf.events.trackers.*;
import java.util.*;

public class TimerSidebarProvider implements ScoreboardProvider {

  public static final ThreadLocal<DecimalFormat> CONQUEST_FORMATTER;
  static final String EMPTY_ENTRY_FILLER;
  private final HCF plugin;
  protected static final String STRAIGHT_LINE;
  private HashMap<Player, Long> lastCoordsUpdate = new HashMap<Player, Long>();
  private HashMap<Player, String> curentCoordsMessage = new HashMap<Player, String>();

  public TimerSidebarProvider(final HCF plugin) {
    this.plugin = plugin;
  }

  public String getApproxCoords(Player searching, Player tracked) {
    if (!this.lastCoordsUpdate.containsKey(searching)) {
      FactionUser user = HCF.getPlugin().getUserManager().getUser(searching.getUniqueId());
      double aproxX = this.randInt(-25, 25);
      double aproxZ = this.randInt(-25, 25);
      double X = aproxX > 25 ? user.getTrackingUser().getLocation().getBlockX() + aproxX
          : tracked.getLocation().getBlockX() - aproxX;
      double Z = aproxZ > 25 ? user.getTrackingUser().getLocation().getBlockZ() + aproxZ
          : tracked.getLocation().getBlockZ() - aproxZ;
      this.lastCoordsUpdate.put(searching, System.currentTimeMillis());
      this.curentCoordsMessage.put(searching, new String("(" + X + ", " + Z + ")"));
      return new String("(" + X + ", " + Z + ")");
    }
    if (System.currentTimeMillis() - this.lastCoordsUpdate.get(searching) < 5000) {
      return this.curentCoordsMessage.get(searching);
    } else {
      FactionUser user = HCF.getPlugin().getUserManager().getUser(searching.getUniqueId());
      double aproxX = this.randInt(-25, 25);
      double aproxZ = this.randInt(-25, 25);
      double X = aproxX > 25 ? user.getTrackingUser().getLocation().getBlockX() + aproxX
          : tracked.getLocation().getBlockX() - aproxX;
      double Z = aproxZ > 25 ? user.getTrackingUser().getLocation().getBlockZ() + aproxZ
          : tracked.getLocation().getBlockZ() - aproxZ;
      this.lastCoordsUpdate.put(searching, System.currentTimeMillis());
      this.curentCoordsMessage.put(searching, new String("(" + X + ", " + Z + ")"));
      return new String("(" + X + ", " + Z + ")");
    }
  }

  private static String handleBardFormat(final long millis, final boolean trailingZero) {
    return (trailingZero ? DateTimeFormats.REMAINING_SECONDS_TRAILING
        : DateTimeFormats.REMAINING_SECONDS).get().format(millis * 0.001);
  }


  @SuppressWarnings("deprecation")
  @Override
  public List<String> getLignes(final Player player) {
    List<String> lines = new ArrayList<String>();
    final EotwHandler.EotwRunnable eotwRunnable = this.plugin.getEotwHandler().getRunnable();
    final PvpClass pvpClass = this.plugin.getPvpClassManager().getEquippedClass(player);
    final EventTimer eventTimer = this.plugin.getTimerManager().eventTimer;
    List<String> conquestLines = null;
    final EventFaction eventFaction = eventTimer.getEventFaction();
    FactionUser user = HCF.getPlugin().getUserManager().getUser(player.getUniqueId());
    lines.add("§eKills: " + "§r" + user.getKills());
    lines.add("§eMorts: " + "§r" + user.getDeaths());
    if (pvpClass != null) {
      if (pvpClass instanceof BardClass || pvpClass instanceof AssassinClass
          || pvpClass instanceof ArcherClass) {
        lines.add(ChatColor.YELLOW.toString() + "Classe" + ChatColor.GOLD + " » " + ChatColor.WHITE
            + pvpClass.getName());
      }
      if (pvpClass instanceof BardClass) {
        final BardClass bardClass = (BardClass) pvpClass;
        lines.add(ChatColor.GOLD + " * " + ChatColor.YELLOW + "Energie" + ChatColor.GOLD + ": "
            + ChatColor.WHITE + handleBardFormat(bardClass.getEnergyMillis(player), true));
        final long remaining2 = bardClass.getRemainingBuffDelay(player);
        if (remaining2 > 0L) {
          lines.add(ChatColor.GOLD + " * " + ChatColor.YELLOW + "Cooldown" + ChatColor.GOLD + ": "
              + ChatColor.WHITE + HCF.getRemaining(remaining2, true));
        }
      } else if (pvpClass instanceof ArcherClass) {
        if (ArcherClass.tagged.containsValue(player.getUniqueId())) {
          for (final UUID uuid : ArcherClass.tagged.keySet()) {
            if (ArcherClass.tagged.get(uuid).equals(player.getUniqueId())
                && Bukkit.getPlayer(uuid) != null) {
              lines.add(ChatColor.GOLD + " * " + ChatColor.YELLOW.toString() + "Tag"
                  + ChatColor.GOLD + ": " + ChatColor.RED + Bukkit.getPlayer(uuid).getName());
            }
          }
        }
      }
    }
    if (user.getTrackingUser() != null) {
      lines.add(
          ChatColor.GOLD.toString() + "* " + ChatColor.YELLOW + "Victime" + ChatColor.GRAY + ": ");
      if (!user.getTrackingUser().isOnline()) {
        lines.add(ChatColor.RED.toString() + user.getTrackingUserName());
        this.curentCoordsMessage.remove(player);
        this.lastCoordsUpdate.remove(player);
      } else {
        lines.add(ChatColor.GREEN.toString() + user.getTrackingUser().getName());
        lines.add(ChatColor.GOLD.toString() + "* " + ChatColor.YELLOW + "Faction" + ChatColor.GRAY
            + ": ");
        if (HCF.getPlugin().getFactionManager().getPlayerFaction(user.getTrackingUser()) == null) {
          lines.add(ChatColor.RED + "Aucune" + ChatColor.GRAY);
        } else {
          lines.add(ChatColor.GRAY.toString() + HCF.getPlugin().getFactionManager()
              .getPlayerFaction(user.getTrackingUser()).getName() + ChatColor.GRAY.toString());
        }
        lines.add(
            ChatColor.GOLD.toString() + "* " + ChatColor.YELLOW + "Coords" + ChatColor.GRAY + ": ");
        lines.add(
            ChatColor.GRAY + this.getApproxCoords(player, user.getTrackingUser()) + ChatColor.GRAY);
        lines.add(ChatColor.GOLD.toString() + "* " + ChatColor.YELLOW + "Territoire"
            + ChatColor.GRAY + ":");
        lines.add(ChatColor.GRAY + HCF.getPlugin().getFactionManager()
            .getFactionAt(user.getTrackingUser().getLocation()).getName() + ChatColor.GRAY);
        lines.add(
            ChatColor.GOLD.toString() + "* " + ChatColor.YELLOW + "Monde" + ChatColor.GRAY + ": ");
        lines.add(ChatColor.GRAY + user.getTrackingUser().getLocation().getWorld().getName()
            + ChatColor.GRAY);

      }
    }
    final Collection<Timer> timers = this.plugin.getTimerManager().getTimers();
    for (final Timer timer : timers) {
      if (timer instanceof PlayerTimer && !(timer instanceof NotchAppleTimer)) {
        final PlayerTimer playerTimer = (PlayerTimer) timer;
        final long remaining2 = playerTimer.getRemaining(player);
        if (remaining2 <= 0L) {
          continue;
        }
        String timerName = playerTimer.getName();
        if (timerName.length() > 14) {
          timerName = timerName.substring(0 + timerName.length());
        }
        lines.add(playerTimer.getScoreboardPrefix() + timerName + ChatColor.GOLD + " » "
            + ChatColor.WHITE + HCF.getRemaining(remaining2, false));
      }
      if (timer instanceof GlobalTimer) {
        final GlobalTimer playerTimer2 = (GlobalTimer) timer;
        final long remaining2 = playerTimer2.getRemaining();
        if (remaining2 <= 0L) {
          continue;
        }
        String timerName = playerTimer2.getName();
        if (timerName.length() > 14) {
          timerName = timerName.substring(0 + timerName.length());
        }
        lines.add(playerTimer2.getScoreboardPrefix() + timerName + ChatColor.GRAY + " » "
            + ChatColor.RED + HCF.getRemaining(remaining2, true));
      }
    }
    if (eotwRunnable != null) {
      long remaining3 = eotwRunnable.getTimeUntilStarting();
      if (remaining3 > 0L) {
        lines.add(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "EOTW" + ChatColor.RED
            + " (Début" + ") " + ChatColor.GRAY + " » " + ChatColor.RED
            + HCF.getRemaining(remaining3, true));
      } else if ((remaining3 = eotwRunnable.getTimeUntilCappable()) > 0L) {
        lines.add(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "EOTW" + ChatColor.RED
            + " (Capture" + ") " + ChatColor.GRAY + " » " + ChatColor.RED
            + HCF.getRemaining(remaining3, true));
      }
    }
    if (eventFaction instanceof ConquestFaction) {
      lines.add("§2" + ChatColor.GRAY + ChatColor.STRIKETHROUGH + TimerSidebarProvider.STRAIGHT_LINE
          + ChatColor.STRIKETHROUGH + TimerSidebarProvider.STRAIGHT_LINE);
      final ConquestFaction conquestFaction = (ConquestFaction) eventFaction;
      // final DecimalFormat format = TimerSidebarProvider.CONQUEST_FORMATTER.get();
      conquestLines = new ArrayList<String>();
      lines.add(ChatColor.YELLOW.toString() + ChatColor.BOLD + conquestFaction.getName()
          + ChatColor.GRAY + " »");
      final ConquestTracker conquestTracker =
          (ConquestTracker) conquestFaction.getEventType().getEventTracker();
      int count = 0;
      for (final Map.Entry<PlayerFaction, Integer> entry : conquestTracker.getFactionPointsMap()
          .entrySet()) {
        String factionName = entry.getKey().getDisplayName((CommandSender) player);
        if (factionName.length() > 14) {
          factionName = factionName.substring(0 + 14);
        }
        lines.add("  " + ChatColor.RED + factionName + ChatColor.GRAY + " » " + ChatColor.WHITE
            + entry.getValue());
        if (++count == 3) {
          break;
        }
      }
    }

    if (player.hasPermission("heaven.staff") && StaffMode.isInStaffMode(player)) {
      lines.add(ChatColor.GOLD.toString() + "Vanish" + ChatColor.GRAY + " » "
          + (Core.getPlugin().getUserManager().getPlayer(player).isVanished()
              ? (ChatColor.GREEN + "Activé") : (ChatColor.RED + "Désactivé")));
    }
    if (conquestLines != null && !conquestLines.isEmpty()) {
      conquestLines.addAll(lines);
      lines = conquestLines;
    }
    lines.add("§0" + ChatColor.GRAY + TimerSidebarProvider.STRAIGHT_LINE
        + TimerSidebarProvider.STRAIGHT_LINE);
    lines.add("§eRegion: " + HCF.getPlugin().getFactionManager().getFactionAt(player.getLocation())
        .getDisplayName(player));
    return lines;
  }

  public int randInt(int min, int max) {

    // NOTE: This will (intentionally) not run as written so that folks
    // copy-pasting have to think about how to initialize their
    // Random instance. Initialization of the Random instance is outside
    // the main scope of the question, but some decent options are to have
    // a field that is initialized once and then re-used as needed or to
    // use ThreadLocalRandom (if using at least Java 1.7).
    Random rand = new Random();

    // nextInt is normally exclusive of the top value,
    // so add 1 to make it inclusive
    int randomNum = rand.nextInt((max - min) + 1) + min;

    return randomNum;
  }

  static {
    CONQUEST_FORMATTER = new ThreadLocal<DecimalFormat>() {
      @Override
      protected DecimalFormat initialValue() {
        return new DecimalFormat("00.0");
      }
    };
    EMPTY_ENTRY_FILLER = " ";
    STRAIGHT_LINE = BukkitUtils.STRAIGHT_LINE_DEFAULT.substring(0, 11);
  }

}
