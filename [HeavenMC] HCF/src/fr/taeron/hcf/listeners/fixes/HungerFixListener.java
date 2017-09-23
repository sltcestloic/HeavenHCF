package fr.taeron.hcf.listeners.fixes;

import org.bukkit.event.*;
import fr.taeron.hcf.*;
import org.bukkit.event.entity.*;
import org.bukkit.entity.*;

public class HungerFixListener implements Listener {

    
    @EventHandler
    public void onHungerChange(final FoodLevelChangeEvent e) {
        if (e.getEntity() instanceof Player) {
            final Player p = (Player)e.getEntity();
            if (HCF.getPlugin().getFactionManager().getFactionAt(p.getLocation()).isSafezone()) {
                p.setSaturation(20.0f);
                p.setHealth(20.0);
                p.setFoodLevel(20);
            }
            p.setSaturation(15.0f);
        }
    }
}
