package fr.taeron.hcf.crate.type;

import fr.taeron.hcf.crate.*;
import org.bukkit.enchantments.*;
import org.heavenmc.core.util.ItemBuilder;

import fr.taeron.hcf.*;

import org.bukkit.*;

public class HeavenKey extends EnderChestKey{
	
    @SuppressWarnings("deprecation")
	public HeavenKey() {
        super("Heaven", 3, new Location(Bukkit.getWorld("world"), -47, 83, 43));
        this.setupRarity(new ItemBuilder(Material.ENDER_PORTAL_FRAME, 1).displayName("§fEnd Portal Frame §7(1x)").build(), 5);
        this.setupRarity(new ItemBuilder(Material.ENDER_PORTAL_FRAME, 1).displayName("§fEnd Portal Frame §7(1x)").build(), 5);
        this.setupRarity(new ItemBuilder(Material.BREWING_STAND_ITEM, 3).displayName("§fBrewing Stand §7(x3)").build(), 5);
        this.setupRarity(new ItemBuilder(Material.NETHER_WARTS, 8).displayName("§fNether Wart §7(x8)").build(), 5);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_BLOCK, 3).displayName("§fDiamond Block §7(x3)").build(), 5);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_BLOCK, 8).displayName("§fDiamond Block §7(x8)").build(), 5);
        this.setupRarity(new ItemBuilder(Material.SUGAR_CANE, 6).displayName("§fSugar Cane §7(x6)").build(), 5);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_PICKAXE).enchant(Enchantment.LOOT_BONUS_BLOCKS, 3).displayName("§fDiamond Pickaxe §7(Fortune III)").build(), 5);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_SWORD).enchant(Enchantment.LOOT_BONUS_MOBS, 3).displayName("§fDiamond Sword §7(Looting III)").build(), 5);
        this.setupRarity(new ItemBuilder(Material.ENDER_PEARL, 16).displayName("§fEnder Pearl §7(x16)").build(), 5);
        this.setupRarity(new ItemBuilder(Material.INK_SACK).data((short)1).displayName("§fVie §7(x1)").build(), 5);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, (int)ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).enchant(Enchantment.DURABILITY, 3).displayName("§fDiamond Helmet §7(Unbreaking III)").build(), 5);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_CHESTPLATE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, (int)ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).enchant(Enchantment.DURABILITY, 3).displayName("§fDiamond Chestplate §7(Unbreaking III)").build(), 5);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, (int)ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).enchant(Enchantment.DURABILITY, 3).displayName("§fDiamond Leggings §7(Unbreaking III)").build(), 5);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_BOOTS).enchant(Enchantment.DURABILITY, 3).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, (int)ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).enchant(Enchantment.PROTECTION_FALL, 2).displayName("§fDiamond Boots §7(Unbreaking III)").build(), 5);
        this.setupRarity(new ItemBuilder(Material.ENDER_PORTAL_FRAME, 3).displayName("§fEnd Portal Frame §7(x3)").build(), 5);
        this.setupRarity(new ItemBuilder(Material.TRIPWIRE_HOOK).displayName(ChatColor.BLUE + "Starter Key").build(), 5);
        this.setupRarity(new ItemBuilder(Material.TRIPWIRE_HOOK).displayName(ChatColor.LIGHT_PURPLE + "Heaven Key").build(), 5); 
        this.setupRarity(new ItemBuilder(Material.GOLDEN_APPLE, 8).displayName("§fGolden Apple §7(x8)").build(), 5);
        this.setupRarity(new ItemBuilder(Material.WEB, 16).displayName("§fCobWeb §7(x16)").build(), 5);
        this.setupRarity(new ItemBuilder(Material.getMaterial(383)).data((short)92).displayName("§fCow Egg §7(x1)").build(), 5);
        this.setupRarity(new ItemBuilder(Material.getMaterial(383)).data((short)92).displayName("§fCow Egg §7(x1)").build(), 5);
    }
    
    @Override
    public ChatColor getColour() {
        return ChatColor.LIGHT_PURPLE;
    }
}
