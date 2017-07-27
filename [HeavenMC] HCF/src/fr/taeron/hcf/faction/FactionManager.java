package fr.taeron.hcf.faction;

import fr.taeron.hcf.faction.claim.*;
import org.bukkit.*;
import org.bukkit.block.*;
import java.util.*;
import fr.taeron.hcf.faction.type.*;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;

import org.bukkit.entity.*;
import org.bukkit.command.*;
import java.util.concurrent.*;

public interface FactionManager
{
    public static final long MAX_DTR_REGEN_MILLIS = TimeUnit.HOURS.toMillis(3L);
    public static final String MAX_DTR_REGEN_WORDS = DurationFormatUtils.formatDurationWords(FactionManager.MAX_DTR_REGEN_MILLIS, true, true);
    
    Map<String, ?> getFactionNameMap();
    
    Collection<Faction> getFactions();
    
    Claim getClaimAt(Location p0);
    
    Claim getClaimAt(World p0, int p1, int p2);
    
    Faction getFactionAt(Location p0);
    
    Faction getFactionAt(Block p0);
    
    Faction getFactionAt(World p0, int p1, int p2);
    
    Faction getFaction(String p0);
    
    Faction getFaction(UUID p0);
    
    @Deprecated
    PlayerFaction getContainingPlayerFaction(String p0);
    
    @Deprecated
    PlayerFaction getPlayerFaction(Player p0);
    
    PlayerFaction getPlayerFaction(UUID p0);
    
    Faction getContainingFaction(String p0);
    
    boolean containsFaction(Faction p0);
    
    boolean createFaction(Faction p0);
    
    boolean createFaction(Faction p0, CommandSender p1);
    
    boolean removeFaction(Faction p0, CommandSender p1);
    
    void reloadFactionData();
    
    void saveFactionData();
}
