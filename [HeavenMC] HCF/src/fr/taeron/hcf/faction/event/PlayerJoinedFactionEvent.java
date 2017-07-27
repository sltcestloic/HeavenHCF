package fr.taeron.hcf.faction.event;

import org.bukkit.event.*;
import java.util.*;
import java.util.Optional;

import org.bukkit.entity.*;
import fr.taeron.hcf.faction.type.*;
import org.bukkit.*;

public class PlayerJoinedFactionEvent extends FactionEvent
{
    private static final HandlerList handlers;
    private final UUID uniqueID;
    private Optional<Player> player;
    
    public PlayerJoinedFactionEvent(final Player player, final PlayerFaction playerFaction) {
        super(playerFaction);
        this.player = Optional.of(player);
        this.uniqueID = player.getUniqueId();
    }
    
    public PlayerJoinedFactionEvent(final UUID playerUUID, final PlayerFaction playerFaction) {
        super(playerFaction);
        this.uniqueID = playerUUID;
    }
    
    public static HandlerList getHandlerList() {
        return PlayerJoinedFactionEvent.handlers;
    }
    
    @Override
    public PlayerFaction getFaction() {
        return (PlayerFaction)this.faction;
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
    
    public HandlerList getHandlers() {
        return PlayerJoinedFactionEvent.handlers;
    }
    
    static {
        handlers = new HandlerList();
    }
}
