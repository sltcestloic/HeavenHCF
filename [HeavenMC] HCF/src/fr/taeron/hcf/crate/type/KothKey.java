package fr.taeron.hcf.crate.type;

import fr.taeron.hcf.crate.*;
import org.bukkit.enchantments.*;
import fr.taeron.hcf.*;
import org.bukkit.*;
import org.bukkit.inventory.*;
import org.heavenmc.core.util.ItemBuilder;

public class KothKey extends EnderChestKey
{
    public KothKey() {
        super("Koth", 6, new Location(Bukkit.getWorld("world"), -44, 83, 53));
        this.setupRarity(new ItemBuilder(Material.DIAMOND_SWORD).enchant(Enchantment.FIRE_ASPECT, 2).enchant(Enchantment.DAMAGE_ALL, (int)ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.DAMAGE_ALL)).displayName(ChatColor.RED + "KOTH Fire").build(), 3);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_BLOCK, 16).displayName("§fDiamond Block §7(x16)").build(), 15);
        this.setupRarity(new ItemBuilder(Material.GOLD_BLOCK, 16).displayName("§fGold Block §8(x16)").build(), 15);
        this.setupRarity(new ItemBuilder(Material.IRON_BLOCK, 16).displayName("§fIron Block §d(x16)").build(), 15);
        this.setupRarity(new ItemBuilder(Material.TRIPWIRE_HOOK, 2).displayName(ChatColor.LIGHT_PURPLE + "Master Key").build(), 2);
        this.setupRarity(new ItemBuilder(Material.TRIPWIRE_HOOK, 4).displayName(ChatColor.LIGHT_PURPLE + "Heaven Key").build(), 2); 
        this.setupRarity(new ItemBuilder(Material.GOLD_HELMET).enchant(Enchantment.DURABILITY, 4).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, (int)ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).displayName(ChatColor.RED + "Bard Helmet").build(), 1);
        this.setupRarity(new ItemBuilder(Material.GOLD_CHESTPLATE).enchant(Enchantment.DURABILITY, 4).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, (int)ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).displayName(ChatColor.RED + "Bard Chestplate").build(), 1);
        this.setupRarity(new ItemBuilder(Material.GOLD_LEGGINGS).enchant(Enchantment.DURABILITY, 4).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, (int)ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).displayName(ChatColor.RED + "Bard Leggings").build(), 1);
        this.setupRarity(new ItemBuilder(Material.GOLD_BOOTS).enchant(Enchantment.DURABILITY, 4).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, (int)ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).displayName(ChatColor.RED + "Bard Boots").build(), 1);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_SWORD).enchant(Enchantment.LOOT_BONUS_MOBS, 5).displayName(ChatColor.RED + "KOTH Looting").build(), 7);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_PICKAXE).enchant(Enchantment.LOOT_BONUS_BLOCKS, 4).displayName(ChatColor.RED + "KOTH Fortune").build(), 5);
        this.setupRarity(new ItemBuilder(Material.SKULL_ITEM, 2).data((short)1).build(), 6);
        this.setupRarity(new ItemStack(Material.BEACON), 2);
        this.setupRarity(new ItemBuilder(Material.NETHER_STAR).displayName("§fNether Star §7(x1)").build(), 3);
        this.setupRarity(new Crowbar().getItemIfPresent(), 5);
        this.setupRarity(new ItemBuilder(Material.GOLDEN_APPLE).displayName("§fGolden Apple §7(x1)").data((short)1).build(), 3);
        this.setupRarity(new ItemBuilder(Material.GOLDEN_APPLE).displayName("§fGolden Apple §8(x1)").data((short)1).build(), 2);
        this.setupRarity(new ItemBuilder(Material.GOLDEN_APPLE, 3).displayName("§fGolden Apple §7(x3)").data((short)1).build(), 3);
        this.setupRarity(new ItemBuilder(Material.GOLDEN_APPLE, 5).displayName("§fGolden Apple §7(x5)").data((short)1).build(), 1);
        this.setupRarity(new ItemBuilder(Material.BOW).enchant(Enchantment.ARROW_DAMAGE, (int)ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.ARROW_DAMAGE)).enchant(Enchantment.ARROW_FIRE, 1).enchant(Enchantment.ARROW_INFINITE, 1).displayName(ChatColor.RED + "KOTH Bow").build(), 3);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_HELMET).enchant(Enchantment.DURABILITY, 4).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, (int)ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).displayName(ChatColor.RED + "KOTH Helmet").build(), 1);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_CHESTPLATE).enchant(Enchantment.DURABILITY, 4).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, (int)ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).displayName(ChatColor.RED + "KOTH Chestplate").build(), 1);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_LEGGINGS).enchant(Enchantment.DURABILITY, 4).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, (int)ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).displayName(ChatColor.RED + "KOTH Leggings").build(), 1);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_BOOTS).enchant(Enchantment.DURABILITY, 4).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, (int)ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).displayName(ChatColor.RED + "KOTH Boots").build(), 1);
    }
    
    @Override
    public ChatColor getColour() {
        return ChatColor.YELLOW;
    }
    
    @Override
    public boolean getBroadcastItems() {
        return true;
    }
}
