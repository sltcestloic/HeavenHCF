package fr.taeron.hcf.faction.type;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import fr.taeron.hcf.faction.claim.Claim;

public class GlowMountainFaction extends ClaimableFaction implements ConfigurationSerializable{
	
    public GlowMountainFaction() {
        super("GlowstoneMountain");
        final World overworld = Bukkit.getWorld("world_nether");
        final int maxHeight = overworld.getMaxHeight();
        Location point1 = new Location(Bukkit.getWorld("world_nether"), -142, 0, 172);
        Location point2 = new Location(Bukkit.getWorld("world_nether"), 3, maxHeight, 320);
        this.addClaim(new Claim(this, point1, point2), Bukkit.getConsoleSender());
        this.safezone = false;
    }
    
    public GlowMountainFaction(final Map<String, Object> map) {
        super(map);
    }
    
    @Override
    public String getDisplayName(final CommandSender sender) {
        return ChatColor.YELLOW.toString() + ChatColor.BOLD + this.getName();
    }

    public boolean isDeathban() {
        return true;
    }
}
