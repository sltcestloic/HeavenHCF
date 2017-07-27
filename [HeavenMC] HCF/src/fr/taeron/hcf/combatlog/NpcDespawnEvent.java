package fr.taeron.hcf.combatlog;

import org.bukkit.event.*;

public final class NpcDespawnEvent extends Event
{
    private static final HandlerList handlers;
    private final Npc npc;
    private final NpcDespawnReason reason;
    
    public NpcDespawnEvent(final Npc npc, final NpcDespawnReason reason) {
        this.npc = npc;
        this.reason = reason;
    }
    
    public static HandlerList getHandlerList() {
        return NpcDespawnEvent.handlers;
    }
    
    public HandlerList getHandlers() {
        return NpcDespawnEvent.handlers;
    }
    
    public Npc getNpc() {
        return this.npc;
    }
    
    public NpcDespawnReason getDespawnReason() {
        return this.reason;
    }
    
    static {
        handlers = new HandlerList();
    }
}
