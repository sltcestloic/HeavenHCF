package fr.taeron.hcf.faction.event;

import org.bukkit.event.*;
import java.util.*;
import fr.taeron.hcf.faction.event.cause.*;
import org.bukkit.entity.*;
import fr.taeron.hcf.faction.type.*;
import com.google.common.base.*;
import com.google.common.base.Optional;

import org.bukkit.*;

public class PlayerLeaveFactionEvent extends FactionEvent implements Cancellable
{
    private static final HandlerList handlers;
    private final UUID uniqueID;
    private final FactionLeaveCause cause;
    private boolean cancelled;
    private Optional<Player> player;
    
    public PlayerLeaveFactionEvent(final Player player, final PlayerFaction playerFaction, final FactionLeaveCause cause) {
        super(playerFaction);
        Preconditions.checkNotNull((Object)player, (Object)"Player cannot be null");
        Preconditions.checkNotNull((Object)playerFaction, (Object)"Player faction cannot be null");
        Preconditions.checkNotNull((Object)"Leave cause cannot be null");
        this.player = Optional.of(player);
        this.uniqueID = player.getUniqueId();
        this.cause = cause;
    }
    
    public PlayerLeaveFactionEvent(final UUID playerUUID, final PlayerFaction playerFaction, final FactionLeaveCause cause) {
        super(playerFaction);
        Preconditions.checkNotNull((Object)playerUUID, (Object)"Player UUID cannot be null");
        Preconditions.checkNotNull((Object)playerFaction, (Object)"Player faction cannot be null");
        Preconditions.checkNotNull((Object)"Leave cause cannot be null");
        this.uniqueID = playerUUID;
        this.cause = cause;
    }
    
    public static HandlerList getHandlerList() {
        return PlayerLeaveFactionEvent.handlers;
    }
    
    public Optional<Player> getPlayer() {
        if (this.player == null) {
            this.player = Optional.fromNullable(Bukkit.getPlayer(this.uniqueID));
        }
        return this.player;
    }
    
    public UUID getUniqueID() {
        return this.uniqueID;
    }
    
    public FactionLeaveCause getLeaveCause() {
        return this.cause;
    }
    
    public HandlerList getHandlers() {
        return PlayerLeaveFactionEvent.handlers;
    }
    
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    static {
        handlers = new HandlerList();
    }
}
