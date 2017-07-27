package fr.taeron.hcf.eventgame.faction;

import org.bukkit.configuration.serialization.*;
import com.google.common.collect.*;
import fr.taeron.hcf.eventgame.*;
import org.bukkit.command.*;
import fr.taeron.hcf.faction.claim.*;
import fr.taeron.hcf.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.heavenmc.core.util.BukkitUtils;

import fr.taeron.hcf.faction.type.*;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;

public class KothFaction extends CapturableFaction implements ConfigurationSerializable{
	
    private CaptureZone captureZone;
    
    public KothFaction(final String name) {
        super(name);
        this.setDeathban(true);
    }
    
    public KothFaction(final Map<String, Object> map) {
        super(map);
        this.setDeathban(true);
        this.captureZone = (CaptureZone) map.get("captureZone");
    }
    
    public Map<String, Object> serialize() {
        final Map<String, Object> map = super.serialize();
        map.put("captureZone", this.captureZone);
        return map;
    }
    
    public List<CaptureZone> getCaptureZones() {
        return ((this.captureZone == null) ? ImmutableList.of() : ImmutableList.of(this.captureZone));
    }
    
    public EventType getEventType() {
        return EventType.KOTH;
    }
    
    @SuppressWarnings("deprecation")
	public void printDetails(final CommandSender sender) {
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        sender.sendMessage(this.getDisplayName(sender));
        for (final Claim claim : this.claims) {
            final Location location = claim.getCenter();
            sender.sendMessage(ChatColor.YELLOW + "  Position: " + ChatColor.RED + '(' + (String)KothFaction.ENVIRONMENT_MAPPINGS.get((Object)location.getWorld().getEnvironment()) + ", " + location.getBlockX() + " | " + location.getBlockZ() + ')');
        }
        if (this.captureZone != null) {
            final long remainingCaptureMillis = this.captureZone.getRemainingCaptureMillis();
            final long defaultCaptureMillis = this.captureZone.getDefaultCaptureMillis();
            if (remainingCaptureMillis > 0L && remainingCaptureMillis != defaultCaptureMillis) {
                sender.sendMessage(ChatColor.YELLOW + "  Temps restant: " + ChatColor.RED + DurationFormatUtils.formatDurationWords(remainingCaptureMillis, true, true));
            }
            sender.sendMessage(ChatColor.YELLOW + "  Temps de capture: " + ChatColor.RED + this.captureZone.getDefaultCaptureWords());
            if (this.captureZone.getCappingPlayer() != null && sender.hasPermission("hcf.koth.checkcapper")) {
                final Player capping = this.captureZone.getCappingPlayer();
                final PlayerFaction playerFaction = HCF.getPlugin().getFactionManager().getPlayerFaction(capping);
                final String factionTag = "[" + ((playerFaction == null) ? "*" : playerFaction.getName()) + "]";
                sender.sendMessage(ChatColor.YELLOW + "  Captureur: " + ChatColor.RED + capping.getName() + ChatColor.GOLD + factionTag);
            }
        }
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
    }
    
    public CaptureZone getCaptureZone() {
        return this.captureZone;
    }
    
    public void setCaptureZone(final CaptureZone captureZone) {
        this.captureZone = captureZone;
    }
}
