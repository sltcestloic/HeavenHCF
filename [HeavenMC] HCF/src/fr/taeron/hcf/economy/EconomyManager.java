package fr.taeron.hcf.economy;

import net.minecraft.util.gnu.trove.map.*;
import java.util.*;

public interface EconomyManager
{
    public static final char ECONOMY_SYMBOL = '$';
    
    TObjectIntMap<UUID> getBalanceMap();
    
    int getBalance(UUID p0);
    
    int setBalance(UUID p0, int p1);
    
    int addBalance(UUID p0, int p1);
    
    int subtractBalance(UUID p0, int p1);
    
    void reloadEconomyData();
    
    void saveEconomyData();
}
