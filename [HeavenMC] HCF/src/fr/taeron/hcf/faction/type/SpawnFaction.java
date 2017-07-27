package fr.taeron.hcf.faction.type;

import org.bukkit.configuration.serialization.*;
import fr.taeron.hcf.*;
import org.bukkit.*;
import fr.taeron.hcf.faction.claim.*;
import java.util.*;

public class SpawnFaction extends ClaimableFaction implements ConfigurationSerializable {
	
    public SpawnFaction() {
        super("Spawn");
        this.safezone = true;
        for (final World world : Bukkit.getWorlds()) {
            final World.Environment environment = world.getEnvironment();
            if (environment != World.Environment.THE_END) {
                final double radius = ConfigurationService.SPAWN_RADIUS_MAP.get(world.getEnvironment()) - 2;
                this.addClaim(new Claim(this, new Location(world, radius, 1111.0, radius), new Location(world, -radius, (double)world.getMaxHeight(), -radius)), null);
            }
        }
    }
    
    public SpawnFaction(final Map<String, Object> map) {
        super(map);
    }
    
    public boolean isDeathban() {
        return false;
    }
}
