package fr.taeron.hcf.events.factions;

import fr.taeron.hcf.faction.type.*;
import org.bukkit.command.*;
import org.heavenmc.core.util.cuboid.Cuboid;

import fr.taeron.hcf.events.*;
import fr.taeron.hcf.faction.claim.*;
import org.bukkit.*;
import java.util.*;

public abstract class EventFaction extends ClaimableFaction
{
    public EventFaction(final String name) {
        super(name);
        this.setDeathban(true);
    }
    
    public EventFaction(final Map<String, Object> map) {
        super(map);
        this.setDeathban(true);
    }
    
    @Override
    public String getDisplayName(final Faction faction) {
        if (this.getEventType() == EventType.KOTH && !this.getName().contains("Citadel")) {
            return ChatColor.GOLD.toString() + this.getEventType().getDisplayName() + ' ' + this.getName();
        }
        return ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + "Citadel";
    }
    
    @Override
    public String getDisplayName(final CommandSender sender) {
        if (this.getEventType() == EventType.KOTH && !this.getName().contains("Citadel")) {
            return "§9§l" + this.getEventType().getDisplayName() + ' ' + this.getName();
        }
        return ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + "Citadel";
    }
    
    public void setClaim(Cuboid cuboid, final CommandSender sender) {
        this.removeClaims(this.getClaims(), sender);
        final Location min = cuboid.getMinimumPoint();
        min.setY(0);
        final Location max = cuboid.getMaximumPoint();
        max.setY(256);
        this.addClaim(new Claim(this, min, max), sender);
    }
    
    public abstract EventType getEventType();
    
    public abstract List<CaptureZone> getCaptureZones();
}
