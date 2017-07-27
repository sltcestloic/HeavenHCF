package fr.taeron.hcf.faction.type;

import org.bukkit.configuration.serialization.*;
import fr.taeron.hcf.faction.claim.*;
import org.bukkit.command.*;
import org.bukkit.*;
import org.bukkit.ChatColor;

import java.util.*;

public class EndPortalFaction extends ClaimableFaction implements ConfigurationSerializable
{
    public EndPortalFaction() {
        super("EndPortal");
        final World overworld = Bukkit.getWorld("world");
        final int maxHeight = overworld.getMaxHeight();
        this.addClaim(new Claim(this, new Location(overworld, 985.0, 0.0, 985.0), new Location(overworld, 1015.0, (double)maxHeight, 1015.0)), null);
        this.addClaim(new Claim(this, new Location(overworld, -1015.0, (double)maxHeight, -1015.0), new Location(overworld, -985.0, 0.0, -985.0)), null);
        this.addClaim(new Claim(this, new Location(overworld, -1015.0, 0.0, 985.0), new Location(overworld, -985.0, (double)maxHeight, 1015.0)), null);
        this.addClaim(new Claim(this, new Location(overworld, 985.0, 0.0, -1015.0), new Location(overworld, 1015.0, (double)maxHeight, -985.0)), null);
        this.safezone = false;
    }
    
    public EndPortalFaction(final Map<String, Object> map) {
        super(map);
    }
    
    public String getDisplayName(final CommandSender sender) {
        return ChatColor.DARK_AQUA + this.getName().replace("EndPortal", "End Portal");
    }
    
    public boolean isDeathban() {
        return true;
    }
}
