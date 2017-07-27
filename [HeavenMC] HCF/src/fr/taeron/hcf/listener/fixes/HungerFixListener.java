package fr.taeron.hcf.listener.fixes;

import org.bukkit.event.*;
import org.bukkit.event.player.*;
import fr.taeron.hcf.*;
import org.bukkit.event.entity.*;
import org.bukkit.entity.*;

public class HungerFixListener implements Listener {

    
    @EventHandler
    public void onMove(final PlayerMoveEvent e) {
        if (HCF.getPlugin().getFactionManager().getFactionAt(e.getPlayer().getLocation()).isSafezone() && e.getPlayer().getFoodLevel() < 20) {
            e.getPlayer().setFoodLevel(20);
            e.getPlayer().setSaturation(20.0f);
        }
    }
    
    @EventHandler
    public void onHungerChange(final FoodLevelChangeEvent e) {
        if (e.getEntity() instanceof Player) {
            final Player p = (Player)e.getEntity();
            if (HCF.getPlugin().getFactionManager().getFactionAt(p.getLocation()).isSafezone()) {
                p.setSaturation(20.0f);
                p.setHealth(20.0);
            }
            p.setSaturation(15.0f);
        }
    }
}
