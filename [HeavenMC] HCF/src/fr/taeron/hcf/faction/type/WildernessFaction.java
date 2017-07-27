package fr.taeron.hcf.faction.type;

import java.util.*;
import org.bukkit.command.*;
import fr.taeron.hcf.*;

public class WildernessFaction extends Faction
{
    public WildernessFaction() {
        super("The Wilderness");
    }
    
    public WildernessFaction(final Map<String, Object> map) {
        super(map);
    }
    
    @Override
    public String getDisplayName(final CommandSender sender) {
        return ConfigurationService.WILDERNESS_COLOUR + this.getName();
    }
}
