package fr.taeron.hcf.faction.event;

import org.bukkit.event.*;
import java.util.*;
import java.util.Optional;

import fr.taeron.hcf.faction.event.cause.*;
import org.bukkit.entity.*;
import fr.taeron.hcf.faction.type.*;
import org.bukkit.*;

public class PlayerLeftFactionEvent extends FactionEvent
{
    private static final HandlerList handlers;
    private final UUID uniqueID;
    private final FactionLeaveCause cause;
    private Optional<Player> player;
    
    public PlayerLeftFactionEvent(final Player player, final PlayerFaction playerFaction, final FactionLeaveCause cause) {
        super(playerFaction);
        this.player = Optional.of(player);
        this.uniqueID = player.getUniqueId();
        this.cause = cause;
    }
    
    public PlayerLeftFactionEvent(final UUID playerUUID, final PlayerFaction playerFaction, final FactionLeaveCause cause) {
        super(playerFaction);
        this.uniqueID = playerUUID;
        this.cause = cause;
    }
    
    public static HandlerList getHandlerList() {
        return PlayerLeftFactionEvent.handlers;
    }
    
    @Override
    public PlayerFaction getFaction() {
        return (PlayerFaction)super.getFaction();
    }
    
    public Optional<Player> getPlayer() {
        if (this.player == null) {
            this.player = Optional.ofNullable(Bukkit.getPlayer(this.uniqueID));
        }
        return this.player;
    }
    
    public UUID getUniqueID() {
        return this.uniqueID;
    }
    
    public FactionLeaveCause getCause() {
        return this.cause;
    }
    
    public HandlerList getHandlers() {
        return PlayerLeftFactionEvent.handlers;
    }
    
    static {
        handlers = new HandlerList();
    }
}
