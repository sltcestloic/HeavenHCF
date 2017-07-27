package fr.taeron.hcf.faction.event;

import org.bukkit.event.*;
import org.bukkit.command.*;
import fr.taeron.hcf.faction.event.cause.*;
import java.util.*;
import fr.taeron.hcf.faction.claim.*;

public class FactionClaimChangedEvent extends Event
{
    private static final HandlerList handlers;
    private final CommandSender sender;
    private final ClaimChangeCause cause;
    private final Collection<Claim> affectedClaims;
    
    public FactionClaimChangedEvent(final CommandSender sender, final ClaimChangeCause cause, final Collection<Claim> affectedClaims) {
        this.sender = sender;
        this.cause = cause;
        this.affectedClaims = affectedClaims;
    }
    
    public static HandlerList getHandlerList() {
        return FactionClaimChangedEvent.handlers;
    }
    
    public CommandSender getSender() {
        return this.sender;
    }
    
    public ClaimChangeCause getCause() {
        return this.cause;
    }
    
    public Collection<Claim> getAffectedClaims() {
        return this.affectedClaims;
    }
    
    public HandlerList getHandlers() {
        return FactionClaimChangedEvent.handlers;
    }
    
    static {
        handlers = new HandlerList();
    }
}
