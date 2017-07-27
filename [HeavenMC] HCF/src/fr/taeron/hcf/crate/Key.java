package fr.taeron.hcf.crate;

import org.bukkit.*;
import org.bukkit.inventory.*;
import org.heavenmc.core.util.Config;

public abstract class Key
{
    private String name;
    
    public Key(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public abstract ChatColor getColour();
    
    public String getDisplayName() {
        return this.getColour() + this.name;
    }
    
    public abstract ItemStack getItemStack();
    
    public void load(Config config) {
    }
    
    public void save(Config config) {
    }
}
