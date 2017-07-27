package fr.taeron.hcf.combatlog;

import java.util.*;
import org.bukkit.entity.*;

public final class NpcIdentity
{
    private final UUID id;
    private final String name;
    
    public NpcIdentity(final UUID id, final String name) {
        this.id = id;
        this.name = name;
    }
    
    public NpcIdentity(final Player player) {
        this(player.getUniqueId(), player.getName());
    }
    
    public UUID getId() {
        return this.id;
    }
    
    public String getName() {
        return this.name;
    }
}
