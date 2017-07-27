package fr.taeron.hcf.faction.type;

import java.util.*;
import org.bukkit.command.*;
import fr.taeron.hcf.*;

public class WarzoneFaction extends Faction
{
    public WarzoneFaction() {
        super("Warzone");
    }
    
    public WarzoneFaction(final Map<String, Object> map) {
        super(map);
    }
    
    @Override
    public String getDisplayName(final CommandSender sender) {
        return ConfigurationService.WARZONE_COLOUR + this.getName();
    }
}
