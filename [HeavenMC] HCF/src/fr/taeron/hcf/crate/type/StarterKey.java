package fr.taeron.hcf.crate.type;

import fr.taeron.hcf.crate.*;
import org.bukkit.inventory.*;
import org.bukkit.*;

public class StarterKey extends EnderChestKey
{
    public StarterKey() {
        super("Starter", 3, new Location(Bukkit.getWorld("world"), -47, 83, 47));
        this.setupRarity(new ItemStack(Material.SPECKLED_MELON, 10), 10);
        this.setupRarity(new ItemStack(Material.SULPHUR, 6), 10);
        this.setupRarity(new ItemStack(Material.BLAZE_ROD, 4), 7);
        this.setupRarity(new ItemStack(Material.SUGAR, 6), 10);
        this.setupRarity(new ItemStack(Material.SPIDER_EYE, 4), 10);
        this.setupRarity(new ItemStack(Material.GLOWSTONE_DUST, 8), 10);
        this.setupRarity(new ItemStack(Material.GLASS_BOTTLE, 16), 15);
        this.setupRarity(new ItemStack(Material.ENDER_PEARL, 1), 5);
        this.setupRarity(new ItemStack(Material.POTATO, 4), 3);
        this.setupRarity(new ItemStack(Material.COOKED_BEEF, 8), 16);
        this.setupRarity(new ItemStack(Material.GOLDEN_APPLE, 4), 1);
        this.setupRarity(new ItemStack(Material.SUGAR_CANE, 4), 3);
    }
    
    @Override
    public ChatColor getColour() {
        return ChatColor.BLUE;
    }
}
