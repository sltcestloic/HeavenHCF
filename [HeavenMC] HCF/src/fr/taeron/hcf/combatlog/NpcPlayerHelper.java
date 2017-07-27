package fr.taeron.hcf.combatlog;

import org.bukkit.entity.*;

public interface NpcPlayerHelper
{
    Player spawn(Player p0);
    
    void despawn(Player p0);
    
    boolean isNpc(Player p0);
    
    NpcIdentity getIdentity(Player p0);
    
    void updateEquipment(Player p0);
    
    void syncOffline(Player p0);
    
    void createPlayerList(Player p0);
    
    void removePlayerList(Player p0);
}
