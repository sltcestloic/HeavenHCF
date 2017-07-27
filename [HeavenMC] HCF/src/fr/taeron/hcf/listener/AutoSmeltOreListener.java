package fr.taeron.hcf.listener;

import org.bukkit.event.block.*;
import org.bukkit.enchantments.*;
import org.bukkit.inventory.*;

import fr.taeron.hcf.HCF;
import fr.taeron.hcf.user.FactionUser;

import org.bukkit.entity.*;
import org.bukkit.block.*;
import org.bukkit.*;
import org.bukkit.event.*;

public class AutoSmeltOreListener implements Listener
{
    
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onBlockBreak(final BlockBreakEvent event) {
		Player player = event.getPlayer();
        ItemStack stack = player.getItemInHand();
        FactionUser user = HCF.getPlugin().getUserManager().getUser(player.getUniqueId());
        if (stack != null && stack.getType() != Material.AIR && !stack.containsEnchantment(Enchantment.SILK_TOUCH)) {
            final Block block = event.getBlock();
            Material dropType = null;
            switch (block.getType()) {
                case IRON_ORE: {
                	user.iron += 1;
                    dropType = Material.IRON_INGOT;
                    break;
                }
                case GOLD_ORE: {
                	user.gold += 1;
                    dropType = Material.GOLD_INGOT;
                    break;
                }
                case COAL_ORE: {
                	user.coal += 1;
                	break;
                }
                case DIAMOND_ORE: {
                	user.diamonds += 1;
                	break;
                }
                case EMERALD_ORE: {
                	user.emerald += 1;
                	break;
                }
                case REDSTONE_ORE: {
                	user.redstone += 1;
                	break;
                }
                case LAPIS_ORE: {
                	user.lapis += 1;
                	break;
                }
                default: {
                    return;
                }
            }
            if(dropType == null){
            	return;
            }
            final Location location = block.getLocation();
            final World world = location.getWorld();
            final ItemStack drop = new ItemStack(dropType, 1);
            world.dropItemNaturally(location, drop);
            block.setType(Material.AIR);
            block.getState().update();
        }
    }
}

