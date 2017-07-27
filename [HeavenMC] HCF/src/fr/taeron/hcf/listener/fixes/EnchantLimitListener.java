package fr.taeron.hcf.listener.fixes;

import org.bukkit.Material;

import com.google.common.collect.*;
import org.bukkit.enchantments.Enchantment;

import fr.taeron.hcf.*;
import org.bukkit.event.enchantment.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.player.*;
import org.bukkit.event.inventory.*;
import net.minecraft.server.v1_7_R4.*;
import org.bukkit.entity.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.meta.*;
import java.util.*;
@SuppressWarnings({ "unchecked", "rawtypes" })
public class EnchantLimitListener implements Listener
{
	private final ImmutableMap<Material, EnumToolMaterial> ITEM_TOOL_MAPPING = /* TODO:Maps.immutableEnumMap */(ImmutableMap.of(Material.IRON_INGOT, EnumToolMaterial.IRON, Material.GOLD_INGOT,
            EnumToolMaterial.GOLD, Material.DIAMOND, EnumToolMaterial.DIAMOND));

    private final ImmutableMap<Material, EnumArmorMaterial> ITEM_ARMOUR_MAPPING = /* TODO:Maps.immutableEnumMap */(ImmutableMap.of(Material.IRON_INGOT, EnumArmorMaterial.IRON, Material.GOLD_INGOT,
            EnumArmorMaterial.GOLD, Material.DIAMOND, EnumArmorMaterial.DIAMOND));
    

    
    public int getMaxLevel(final Enchantment enchant) {
        return ConfigurationService.ENCHANTMENT_LIMITS.getOrDefault(enchant, enchant.getMaxLevel());
    }
    
    
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEnchantItem(final EnchantItemEvent event) {
        final Map adding = event.getEnchantsToAdd();
        final Iterator<Map.Entry<Enchantment, Integer>> iterator = adding.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<Enchantment, Integer> entry = iterator.next();
            final Enchantment enchantment = entry.getKey();
            final int maxLevel = this.getMaxLevel(enchantment);
            if (entry.getValue() > maxLevel) {
                if (maxLevel > 0) {
                    adding.put(enchantment, maxLevel);
                }
                else {
                    iterator.remove();
                }
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityDeath(final EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            for (final ItemStack drop : event.getDrops()) {
                this.validateIllegalEnchants(drop);
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerFishEvent(final PlayerFishEvent event) {
        final Entity caught = event.getCaught();
        if (caught instanceof Item) {
            this.validateIllegalEnchants(((Item)caught).getItemStack());
        }
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPrepareAnvilRepair(final InventoryClickEvent event) {
    	if(event.getClickedInventory() == null || event.getClickedInventory().getType() == null){
    		return;
    	}
    	if(event.getClickedInventory().getType() != InventoryType.ANVIL){
    		return;
    	}
    	if(event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR){
    		return;
    	}
        final ItemStack firstAssassinEffects = event.getInventory().getItem(0);
        final ItemStack second = event.getInventory().getItem(1);
        if(firstAssassinEffects == null || firstAssassinEffects.getType() == Material.AIR){
        	return;
        }
        if(second == null || second.getType() == Material.AIR){
        	return;
        }
        if (firstAssassinEffects != null && firstAssassinEffects.getType() != Material.AIR && second != null && second.getType() != Material.AIR) {
            final Object firstItemObj = net.minecraft.server.v1_7_R4.Item.REGISTRY.a(firstAssassinEffects.getTypeId());
            if (firstItemObj instanceof net.minecraft.server.v1_7_R4.Item) {
                final net.minecraft.server.v1_7_R4.Item nmsFirstItem = (net.minecraft.server.v1_7_R4.Item)firstItemObj;
                if (nmsFirstItem instanceof ItemTool) {
                    if (this.ITEM_TOOL_MAPPING.get((Object)second.getType()) == ((ItemTool)nmsFirstItem).i()) {
                        return;
                    }
                }
                else if (nmsFirstItem instanceof ItemSword) {
                    final EnumToolMaterial comparison = (EnumToolMaterial)this.ITEM_TOOL_MAPPING.get((Object)second.getType());
                    if (comparison != null && comparison.e() == nmsFirstItem.c()) {
                        return;
                    }
                }
                else if (nmsFirstItem instanceof ItemArmor && this.ITEM_ARMOUR_MAPPING.get((Object)second.getType()) == ((ItemArmor)nmsFirstItem).m_()) {
                    return;
                }
            }
        }
        final HumanEntity repairer = event.getWhoClicked();
        if (repairer instanceof Player) {
            this.validateIllegalEnchants(event.getCurrentItem());
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void preventCustom(PlayerInteractEvent e){
    	if(e.getPlayer().getItemInHand() == null || e.getPlayer().getItemInHand().getType().equals(Material.AIR)){
    		return;
    	}
    	if(!e.getPlayer().getItemInHand().hasItemMeta()){
    		return;
    	}
    	if(!e.getPlayer().getItemInHand().getItemMeta().hasEnchant(Enchantment.DAMAGE_ALL)){
    		return;
    	}
    	Map<Enchantment, Integer> enchantments = e.getPlayer().getItemInHand().getEnchantments();
    	if (enchantments.containsKey(Enchantment.DAMAGE_ALL)) {
    		int level = enchantments.get(Enchantment.DAMAGE_ALL);
    		if(level > 2){
    			e.getPlayer().getItemInHand().getItemMeta().addEnchant(Enchantment.DAMAGE_ALL, 2, false);
    		}
    	}
    }
    
    private boolean validateIllegalEnchants(final ItemStack stack) {
        boolean updated = false;
        if (stack != null && stack.getType() != Material.AIR) {
            final ItemMeta meta = stack.getItemMeta();
            if (meta instanceof EnchantmentStorageMeta) {
                final EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta)meta;
                final Set<Map.Entry<Enchantment, Integer>> entries = enchantmentStorageMeta.getStoredEnchants().entrySet();
                for (final Map.Entry<Enchantment, Integer> entry : entries) {
                    final Enchantment enchantment = entry.getKey();
                    final int maxLevel = this.getMaxLevel(enchantment);
                    if (entry.getValue() > maxLevel) {
                        updated = true;
                        if (maxLevel > 0) {
                            enchantmentStorageMeta.addStoredEnchant(enchantment, maxLevel, false);
                        }
                        else {
                            enchantmentStorageMeta.removeStoredEnchant(enchantment);
                        }
                    }
                }
                stack.setItemMeta(meta);
            }
            else {
                final Set<Map.Entry<Enchantment, Integer>> entries2 = stack.getEnchantments().entrySet();
                for (final Map.Entry<Enchantment, Integer> entry2 : entries2) {
                    final Enchantment enchantment2 = entry2.getKey();
                    final int maxLevel2 = this.getMaxLevel(enchantment2);
                    if (entry2.getValue() > maxLevel2) {
                        updated = true;
                        stack.removeEnchantment(enchantment2);
                        if (maxLevel2 <= 0) {
                            continue;
                        }
                        stack.addEnchantment(enchantment2, maxLevel2);
                    }
                }
            }
        }
        return updated;
    }
}
