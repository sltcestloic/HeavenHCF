package fr.taeron.hcf.eventgame.koth.argument;

import fr.taeron.hcf.*;
import org.bukkit.command.*;
import org.heavenmc.core.util.JavaUtils;
import org.heavenmc.core.util.command.CommandArgument;
import org.bukkit.*;
import fr.taeron.hcf.eventgame.faction.*;
import fr.taeron.hcf.faction.type.*;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;
import fr.taeron.hcf.eventgame.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class KothSetCapDelayArgument extends CommandArgument{
	
    private final HCF plugin;
    
    public KothSetCapDelayArgument(final HCF plugin) {
        super("setcapdelay", "Sets the cap delay of a KOTH");
        this.plugin = plugin;
        this.aliases = new String[] { "setcapturedelay" };
        this.permission = "command.koth.argument." + this.getName();
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <kothName> <capDelay>";
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Utilisation: " + this.getUsage(label));
            return true;
        }
        final Faction faction = this.plugin.getFactionManager().getFaction(args[1]);
        if (faction == null || !(faction instanceof KothFaction)) {
            sender.sendMessage(ChatColor.RED + "Il n'y a pas de KOTH nommé '" + args[1] + "'.");
            return true;
        }
        final long duration = JavaUtils.parse(StringUtils.join((Object[])args, ' ', 2, args.length));
        if (duration == -1L) {
            sender.sendMessage(ChatColor.RED + "Durée invalide, exemple: 10m 1s");
            return true;
        }
        final KothFaction kothFaction = (KothFaction)faction;
        final CaptureZone captureZone = kothFaction.getCaptureZone();
        if (captureZone == null) {
            sender.sendMessage(ChatColor.RED + kothFaction.getDisplayName(sender) + ChatColor.RED + " n'a pas de zone de capture.");
            return true;
        }
        if (captureZone.isActive() && duration < captureZone.getRemainingCaptureMillis()) {
            captureZone.setRemainingCaptureMillis(duration);
        }
        captureZone.setDefaultCaptureMillis(duration);
        sender.sendMessage(ChatColor.YELLOW + "Tu as défini la zone de capture du KOTH " + ChatColor.WHITE + kothFaction.getDisplayName(sender) + ChatColor.YELLOW + " à " + ChatColor.WHITE + DurationFormatUtils.formatDurationWords(duration, true, true) + ChatColor.WHITE + '.');
        return true;
    }
    
    @SuppressWarnings("unchecked")
	public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length != 2) {
            return Collections.emptyList();
        }
        return (List<String>) this.plugin.getFactionManager().getFactions().stream().filter(faction -> faction instanceof KothFaction).map((Function<? super Faction, ?>)Faction::getName).collect(Collectors.toList());
    }
}
