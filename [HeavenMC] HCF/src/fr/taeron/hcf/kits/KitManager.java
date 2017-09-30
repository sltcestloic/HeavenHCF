package fr.taeron.hcf.kits;

import java.util.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;

public interface KitManager
{
    public static final int UNLIMITED_USES = Integer.MAX_VALUE;
    
    List<Kit> getKits();
    
    Kit getKit(String p0);
    
    Kit getKit(UUID p0);
    
    boolean containsKit(Kit p0);
    
    void createKit(Kit p0);
    
    void removeKit(Kit p0);
    
    Inventory getGui(Player p0);
    
    void reloadKitData();
    
    void saveKitData();
   }
