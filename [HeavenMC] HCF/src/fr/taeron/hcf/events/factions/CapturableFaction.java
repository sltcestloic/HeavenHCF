package fr.taeron.hcf.events.factions;

import java.util.*;

public abstract class CapturableFaction extends EventFaction
{
    public CapturableFaction(final String name) {
        super(name);
    }
    
    public CapturableFaction(final Map<String, Object> map) {
        super(map);
    }
}
