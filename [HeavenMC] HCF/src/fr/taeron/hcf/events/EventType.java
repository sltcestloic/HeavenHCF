package fr.taeron.hcf.events;

import fr.taeron.hcf.*;
import fr.taeron.hcf.events.trackers.*;

import com.google.common.collect.*;

@SuppressWarnings({ "unchecked", "rawtypes" })
public enum EventType
{
    @SuppressWarnings("deprecation")
	CONQUEST("Conquest", (EventTracker)new ConquestTracker(HCF.getPlugin())), 
    @SuppressWarnings("deprecation")
	KOTH("KOTH", (EventTracker)new KothTracker(HCF.getPlugin()));
    
    private static final ImmutableMap<String, EventType> byDisplayName;
    @SuppressWarnings("deprecation")
	private final EventTracker eventTracker;
    private final String displayName;
    
    @SuppressWarnings("deprecation")
	private EventType(final String displayName, final EventTracker eventTracker) {
        this.displayName = displayName;
        this.eventTracker = eventTracker;
    }
    
    @Deprecated
    public static EventType getByDisplayName(final String name) {
        return (EventType)EventType.byDisplayName.get((Object)name.toLowerCase());
    }
    
    @SuppressWarnings("deprecation")
	public EventTracker getEventTracker() {
        return this.eventTracker;
    }
    
    public String getDisplayName() {
        return this.displayName;
    }
    
    static {
        final ImmutableMap.Builder<String, EventType> builder = (ImmutableMap.Builder<String, EventType>)new ImmutableBiMap.Builder();
        for (final EventType eventType : values()) {
            builder.put(eventType.displayName.toLowerCase(), eventType);
        }
        byDisplayName = builder.build();
    }
}
