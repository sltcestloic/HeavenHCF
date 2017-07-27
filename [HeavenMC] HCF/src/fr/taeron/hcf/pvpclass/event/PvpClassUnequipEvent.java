package fr.taeron.hcf.pvpclass.event;

import org.bukkit.event.player.*;
import org.bukkit.event.*;


import fr.taeron.hcf.pvpclass.*;
import org.bukkit.entity.*;

public class PvpClassUnequipEvent extends PlayerEvent
{
    private static final HandlerList handlers;
    private final PvpClass pvpClass;
    
    public PvpClassUnequipEvent(final Player player, final PvpClass pvpClass) {
        super(player);
        this.pvpClass = pvpClass;
    }
    
    public static HandlerList getHandlerList() {
        return PvpClassUnequipEvent.handlers;
    }
    
    public PvpClass getPvpClass() {
        return this.pvpClass;
    }
    
    public HandlerList getHandlers() {
        return PvpClassUnequipEvent.handlers;
    }
    
    static {
        handlers = new HandlerList();
    }
}
