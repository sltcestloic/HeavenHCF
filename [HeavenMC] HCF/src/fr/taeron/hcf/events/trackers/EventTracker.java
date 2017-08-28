package fr.taeron.hcf.events.trackers;

import org.bukkit.entity.*;

import fr.taeron.hcf.events.*;
import fr.taeron.hcf.events.factions.*;

@Deprecated
public interface EventTracker
{
    EventType getEventType();
    
    void tick(EventTimer p0, EventFaction p1);
    
    void onContest(EventFaction p0, EventTimer p1);
    
    boolean onControlTake(Player p0, CaptureZone p1);
    
    boolean onControlLoss(Player p0, CaptureZone p1, EventFaction p2);
    
    void stopTiming();
}
