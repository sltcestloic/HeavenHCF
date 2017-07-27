package fr.taeron.hcf.combatlog;

import org.bukkit.entity.*;

public final class Npc
{
    private final NpcIdentity identity;
    private final Player entity;
    
    public Npc(final NpcPlayerHelper helper, final Player entity) {
        this.identity = helper.getIdentity(entity);
        this.entity = entity;
    }
    
    public NpcIdentity getIdentity() {
        return this.identity;
    }
    
    public Player getEntity() {
        return this.entity;
    }
}
