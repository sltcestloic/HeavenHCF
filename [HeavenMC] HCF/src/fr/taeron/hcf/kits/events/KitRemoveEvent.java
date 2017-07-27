package fr.taeron.hcf.kits.events;

import org.bukkit.event.*;

import fr.taeron.hcf.kits.*;

public class KitRemoveEvent extends Event implements Cancellable
{
    private static final HandlerList handlers;
    private final Kit kit;
    private boolean cancelled;
    
    public KitRemoveEvent(final Kit kit) {
        this.cancelled = false;
        this.kit = kit;
    }
    
    public static HandlerList getHandlerList() {
        return KitRemoveEvent.handlers;
    }
    
    public Kit getKit() {
        return this.kit;
    }
    
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    public HandlerList getHandlers() {
        return KitRemoveEvent.handlers;
    }
    
    static {
        handlers = new HandlerList();
    }
}
