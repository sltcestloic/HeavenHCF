package fr.taeron.hcf.crate;

import org.bukkit.inventory.*;
import org.bukkit.*;
import com.google.common.collect.*;
import java.util.*;
import org.bukkit.inventory.meta.*;

public class EnderChestKey extends Key
{
    private final ItemStack[] items;
    private int rolls;
    private Location loc;
    
    public EnderChestKey(final String name, final int rolls, Location loc) {
        super(name);
        this.items = new ItemStack[100];
        this.rolls = rolls;
        this.loc = loc;
        
    }
    
    public void setLocation(Location loc){
    	this.loc = loc;
    }
    
    public Location getLocation(){
    	return this.loc;
    }
    
    public boolean getBroadcastItems() {
        return false;
    }
    
    public int getRolls() {
        return this.rolls;
    }
    
    public void setRolls(final int rolls) {
        this.rolls = rolls;
    }
    
    public ItemStack[] getLoot() {
        return Arrays.copyOf(this.items, this.items.length);
    }
    
    public void setupRarity(final ItemStack stack, final int percent) {
        int currentItems = 0;
        for (final ItemStack item : this.items) {
            if (item != null && item.getType() != Material.AIR) {
                ++currentItems;
            }
        }
        for (int min = Math.min(100, currentItems + percent), i = currentItems; i < min; ++i) {
            this.items[i] = stack;
        }
    }
    
    @Override
    public ChatColor getColour() {
        return ChatColor.GOLD;
    }
    
    @Override
    public ItemStack getItemStack() {
        final ItemStack stack = new ItemStack(Material.TRIPWIRE_HOOK, 1);
        final ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(this.getColour() + this.getName() + " Key");
        meta.setLore(Lists.newArrayList(new String[] { ChatColor.GRAY + "Clique sur un enderchest au spawn pour utiliser cette clé." }));
        stack.setItemMeta(meta);
        return stack;
    }
}
