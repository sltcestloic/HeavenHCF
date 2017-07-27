package fr.taeron.hcf.pvpclass.event;

import org.bukkit.event.player.*;
import org.bukkit.event.*;
import fr.taeron.hcf.pvpclass.*;
import org.bukkit.entity.*;

public class PvpClassEquipEvent extends PlayerEvent
{
    private static final HandlerList handlers;
    private final PvpClass pvpClass;
    
    public PvpClassEquipEvent(final Player player, final PvpClass pvpClass) {
        super(player);
        this.pvpClass = pvpClass;
    }
    
    public static HandlerList getHandlerList() {
        return PvpClassEquipEvent.handlers;
    }
    
    public PvpClass getPvpClass() {
        return this.pvpClass;
    }
    
    public HandlerList getHandlers() {
        return PvpClassEquipEvent.handlers;
    }
    
    static {
        handlers = new HandlerList();
    }
}
