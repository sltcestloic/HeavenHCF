package fr.taeron.hcf.faction.event;

import org.bukkit.event.*;
import fr.taeron.hcf.faction.type.*;
import com.google.common.base.*;

public abstract class FactionEvent extends Event
{
    protected final Faction faction;
    
    public FactionEvent(final Faction faction) {
        this.faction = (Faction)Preconditions.checkNotNull((Object)faction, (Object)"Faction cannot be null");
    }
    
    FactionEvent(final Faction faction, final boolean async) {
        super(async);
        this.faction = (Faction)Preconditions.checkNotNull((Object)faction, (Object)"Faction cannot be null");
    }
    
    public Faction getFaction() {
        return this.faction;
    }
}
