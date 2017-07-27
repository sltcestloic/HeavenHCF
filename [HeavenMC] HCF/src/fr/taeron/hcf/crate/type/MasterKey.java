package fr.taeron.hcf.crate.type;

import fr.taeron.hcf.ConfigurationService;
import fr.taeron.hcf.crate.*;

import org.bukkit.enchantments.*;
import org.heavenmc.core.util.ItemBuilder;
import org.bukkit.*;

public class MasterKey extends EnderChestKey{
	
    public MasterKey() {
        super("Master", 2, new Location(Bukkit.getWorld("world"), -21, 75, -14));
        this.setupRarity(new ItemBuilder(Material.ENDER_PORTAL_FRAME, 5).displayName("§fEnd Portal Frame §7(5x)").build(), 5);
        this.setupRarity(new ItemBuilder(Material.ENDER_PORTAL_FRAME, 7).displayName("§fEnd Portal Frame §7(7x)").build(), 5);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_BLOCK, 10).displayName("§fDiamond Block §7(x10)").build(), 5);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_BLOCK, 18).displayName("§fDiamond Block §7(x18)").build(), 5);
        this.setupRarity(new ItemBuilder(Material.SKULL_ITEM, 3).data((short)1).displayName("§fWither Skull §7(x3)").build(), 5);
        this.setupRarity(new ItemBuilder(Material.EMERALD_BLOCK, 10).displayName("§fEmerald Block §7(x10)").build(), 5);
        this.setupRarity(new ItemBuilder(Material.EMERALD_BLOCK, 18).displayName("§fEmerald Block §7(x18)").build(), 5);
        this.setupRarity(new ItemBuilder(Material.SULPHUR, 64).displayName("§fGun Powder §7(x64)").build(), 5);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_PICKAXE).enchant(Enchantment.LOOT_BONUS_BLOCKS, 4).displayName("§fDiamond Pickaxe §7(Fortune IV)").build(), 5);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_SWORD).enchant(Enchantment.LOOT_BONUS_MOBS, 4).displayName("§fDiamond Sword §7(Looting IV)").build(), 5);
        this.setupRarity(new ItemBuilder(Material.ENDER_PEARL, 64).displayName("§fEnder Pearl §7(x64)").build(), 5);
        this.setupRarity(new ItemBuilder(Material.INK_SACK).data((short)1).displayName("§fVie §7(x1)").build(), 5);
        this.setupRarity(new ItemBuilder(Material.INK_SACK, 3).data((short)1).displayName("§fVies §7(x3)").build(), 5);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, (int)ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).enchant(Enchantment.DURABILITY, 4).displayName("§fDiamond Helmet §7(Unbreaking IV)").build(), 5);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_CHESTPLATE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, (int)ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).enchant(Enchantment.DURABILITY, 4).displayName("§fDiamond Chestplate §7(Unbreaking IV)").build(), 5);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, (int)ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).enchant(Enchantment.DURABILITY, 4).displayName("§fDiamond Leggings §7(Unbreaking IV)").build(), 5);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_BOOTS).enchant(Enchantment.DURABILITY, 4).enchant(Enchantment.PROTECTION_FALL, 4).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, (int)ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).displayName("§fDiamond Boots §7(Unbreaking IV)").build(), 5);
        this.setupRarity(new ItemBuilder(Material.BEACON).displayName("§fBeacon §7(x1)").build(), 5);
        this.setupRarity(new ItemBuilder(Material.TRIPWIRE_HOOK).displayName(ChatColor.LIGHT_PURPLE + "Master Key").build(), 5);
        this.setupRarity(new ItemBuilder(Material.TRIPWIRE_HOOK).displayName(ChatColor.LIGHT_PURPLE + "Heaven Key").build(), 5); 
    }
    
    public ChatColor getColour() {
        return ChatColor.LIGHT_PURPLE;
    }
    
    public boolean getBroadcastItems() {
        return false;
    }
}
