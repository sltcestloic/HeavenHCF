package fr.taeron.hcf.combatlog;

import org.bukkit.entity.*;
import java.util.*;

public final class PlayerCache
{
    private final Map<UUID, Player> uuidCache;
    private final Map<String, Player> nameCache;
    
    public PlayerCache() {
        this.uuidCache = new HashMap<UUID, Player>();
        this.nameCache = new HashMap<String, Player>();
    }
    
    public void addPlayer(final Player player) {
        this.uuidCache.put(player.getUniqueId(), player);
        this.nameCache.put(player.getName().toLowerCase(), player);
    }
    
    public void removePlayer(final Player player) {
        this.uuidCache.remove(player.getUniqueId());
        this.nameCache.remove(player.getName().toLowerCase());
    }
    
    public boolean isOnline(final UUID id) {
        return this.uuidCache.containsKey(id);
    }
    
    public boolean isOnline(final String name) {
        return this.nameCache.containsKey(name.toLowerCase());
    }
    
    public Player getPlayer(final UUID id) {
        return this.uuidCache.get(id);
    }
    
    public Player getPlayer(final String name) {
        return this.nameCache.get(name.toLowerCase());
    }
    
    public Collection<Player> getPlayers() {
        return Collections.unmodifiableCollection((Collection<? extends Player>)this.uuidCache.values());
    }
}
