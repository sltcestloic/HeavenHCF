package fr.taeron.hcf.listener;

import org.bukkit.event.entity.*;
import org.bukkit.entity.*;
import org.bukkit.enchantments.*;
import org.bukkit.event.*;

public class ExpListener implements Listener
{
    String permission;
    
    public ExpListener() {
        this.permission = "hcf.doublexp";
    }
    
    @EventHandler
    public void onXPDrop(final EntityDeathEvent e) {
        if (e.getEntity().getKiller() instanceof Player && e.getEntity().getKiller() != null) {
            if (e.getEntity().getKiller().getPlayer().getItemInHand().getEnchantments().containsKey(Enchantment.LOOT_BONUS_MOBS)) {
                e.setDroppedExp(e.getDroppedExp() * e.getEntity().getKiller().getPlayer().getItemInHand().getEnchantments().get(Enchantment.LOOT_BONUS_MOBS));
            }
            if (e.getEntity().getKiller().hasPermission(this.permission)) {
                e.setDroppedExp(e.getDroppedExp() * 2);
            }
        }
    }
}
