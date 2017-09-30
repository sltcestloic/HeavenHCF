package fr.taeron.hcf.crate.type;

import fr.taeron.hcf.crate.*;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.heavenmc.core.util.ItemBuilder;

public class AquaKey extends EnderChestKey
{
    public AquaKey() {
        super("Aqua", 3, new Location(Bukkit.getWorld("world"), -100, 100, -100));
        this.setupRarity(new ItemBuilder(Material.DIAMOND_PICKAXE).enchant(Enchantment.DIG_SPEED, 3).enchant(Enchantment.LOOT_BONUS_BLOCKS, 2).enchant(Enchantment.DURABILITY, 3).build(), 5);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_PICKAXE).enchant(Enchantment.DIG_SPEED, 3).enchant(Enchantment.SILK_TOUCH, 1).enchant(Enchantment.DURABILITY, 3).build(), 5);
        this.setupRarity(new ItemBuilder(Material.TRIPWIRE_HOOK).displayName(ChatColor.LIGHT_PURPLE + "Heaven Key").build(), 5);
        this.setupRarity(new ItemBuilder(Material.TRIPWIRE_HOOK).displayName(ChatColor.LIGHT_PURPLE + "Master Key").build(), 1);
        this.setupRarity(new ItemBuilder(Material.TRIPWIRE_HOOK, 3).displayName(ChatColor.BLUE + "Starter Key").build(), 5); 
        this.setupRarity(new ItemBuilder(Material.DIAMOND_BLOCK, 2).build(), 5);
        this.setupRarity(new ItemBuilder(Material.GOLD_BLOCK, 8).build(), 5);
        this.setupRarity(new ItemBuilder(Material.IRON_BLOCK, 16).build(), 5);
        this.setupRarity(new ItemStack(Material.ANVIL), 7);
        this.setupRarity(new ItemStack(Material.ENDER_PORTAL_FRAME), 5);
        this.setupRarity(new ItemStack(Material.ENCHANTMENT_TABLE), 7);
        this.setupRarity(new ItemStack(Material.NETHER_STAR), 5);
        this.setupRarity(new ItemStack(Material.ENDER_PEARL, 8), 5);
        this.setupRarity(new ItemStack(Material.SULPHUR, 32), 5);
        this.setupRarity(new ItemStack(Material.BREWING_STAND, 3), 5);
        this.setupRarity(new ItemStack(Material.GLOWSTONE, 8), 5);
        this.setupRarity(new ItemStack(Material.EXP_BOTTLE, 32), 5);
        this.setupRarity(new ItemStack(Material.WEB, 8), 5);
        this.setupRarity(new ItemStack(Material.GOLDEN_APPLE, 8), 5);
        this.setupRarity(new ItemStack(Material.HOPPER, 4), 5);
        this.setupRarity(new ItemStack(Material.MONSTER_EGG, 2, (byte)92), 5);
    }
    
    @Override
    public ChatColor getColour() {
        return ChatColor.AQUA;
    }
    
    @Override
    public boolean getBroadcastItems() {
        return true;
    }
}
