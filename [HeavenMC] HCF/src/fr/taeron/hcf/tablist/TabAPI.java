package fr.taeron.hcf.tablist;

import org.bukkit.entity.*;

import fr.taeron.hcf.HCF;

public class TabAPI
{
    static HCF plugin;
    
    public TabAPI(final HCF tabPlugin) {
        TabAPI.plugin = tabPlugin;
    }
    
    public static TabList createTabListForPlayer(final Player player) {
        final TabList list = new TabList(TabAPI.plugin, player);
        TabAPI.plugin.tabLists.put(player.getName(), list);
        return list;
    }
    
    public static TabList getPlayerTabList(final Player player) {
        return TabAPI.plugin.tabLists.get(player.getName());
    }
}