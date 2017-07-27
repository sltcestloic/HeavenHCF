package fr.taeron.hcf.deathban;

import net.minecraft.util.gnu.trove.map.*;
import java.util.*;
import org.bukkit.entity.*;
import java.util.concurrent.*;

public interface DeathbanManager
{
    public static final long MAX_DEATHBAN_TIME = TimeUnit.HOURS.toMillis(8L);
    
    TObjectIntMap<UUID> getLivesMap();
    
    int getLives(UUID p0);
    
    int setLives(UUID p0, int p1);
    
    int addLives(UUID p0, int p1);
    
    int takeLives(UUID p0, int p1);
    
    double getDeathBanMultiplier(Player p0);
    
    Deathban applyDeathBan(Player p0, String p1);
    
    Deathban applyDeathBan(UUID p0, Deathban p1);
    
    void reloadDeathbanData();
    
    void saveDeathbanData();
}
