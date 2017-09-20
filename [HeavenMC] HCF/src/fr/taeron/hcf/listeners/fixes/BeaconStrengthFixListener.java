package fr.taeron.hcf.listeners.fixes;

import org.bukkit.event.entity.*;
import org.bukkit.potion.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;

public class BeaconStrengthFixListener implements Listener
{
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPotionEffectAdd(final PotionEffectAddEvent event) {
        final LivingEntity entity = event.getEntity();
        if (entity instanceof Player && event.getCause() == PotionEffectAddEvent.EffectCause.BEACON) {
            final PotionEffect effect = event.getEffect();
            if (effect.getAmplifier() > 1 && effect.getType().equals((Object)PotionEffectType.INCREASE_DAMAGE)) {
                entity.addPotionEffect(new PotionEffect(effect.getType(), effect.getDuration(), 0, effect.isAmbient()));
                event.setCancelled(true);
            }
        }
        if (entity instanceof Player && event.getCause() == PotionEffectAddEvent.EffectCause.POTION_SPLASH) {
        	final PotionEffect effect = event.getEffect();
        	if(effect.getType().equals((Object)PotionEffectType.INCREASE_DAMAGE)) {
        		event.setCancelled(true);
        	}
        }
    }
}