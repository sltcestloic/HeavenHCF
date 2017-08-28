package fr.taeron.hcf.faction.event;

import org.bukkit.event.*;

import fr.taeron.hcf.events.*;
import fr.taeron.hcf.events.factions.*;

import org.bukkit.entity.*;

import com.google.common.base.*;

public class CaptureZoneLeaveEvent extends FactionEvent implements Cancellable
{
    private static final HandlerList handlers;
    private final CaptureZone captureZone;
    private final Player player;
    private boolean cancelled;
    
    public CaptureZoneLeaveEvent(final Player player, final CapturableFaction capturableFaction, final CaptureZone captureZone) {
        super(capturableFaction);
        Preconditions.checkNotNull((Object)player, (Object)"Player cannot be null");
        Preconditions.checkNotNull((Object)captureZone, (Object)"Capture zone cannot be null");
        this.captureZone = captureZone;
        this.player = player;
    }
    
    public static HandlerList getHandlerList() {
        return CaptureZoneLeaveEvent.handlers;
    }
    
    @Override
    public CapturableFaction getFaction() {
        return (CapturableFaction)super.getFaction();
    }
    
    public CaptureZone getCaptureZone() {
        return this.captureZone;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    public HandlerList getHandlers() {
        return CaptureZoneLeaveEvent.handlers;
    }
    
    static {
        handlers = new HandlerList();
    }
}
