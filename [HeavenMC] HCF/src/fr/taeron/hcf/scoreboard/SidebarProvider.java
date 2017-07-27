package fr.taeron.hcf.scoreboard;

import org.bukkit.entity.*;
import java.util.*;

public interface SidebarProvider
{
    String getTitle();
    
    List<SidebarEntry> getLines(Player p0);
}
