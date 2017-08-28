package fr.taeron.hcf.listeners.fixes;

import org.bukkit.event.entity.*;
import org.bukkit.craftbukkit.v1_7_R4.entity.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;

public class InfinityArrowFixListener implements Listener
{
    @SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.NORMAL)
    public void onProjectileHit(final ProjectileHitEvent event) {
        final Entity entity = (Entity)event.getEntity();
        if (entity instanceof Arrow) {
            final Arrow arrow = (Arrow)entity;
            if (!(arrow.getShooter() instanceof Player) || ((CraftArrow)arrow).getHandle().fromPlayer == 2) {
                arrow.remove();
            }
        }
    }
}
