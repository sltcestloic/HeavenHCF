package fr.taeron.hcf.listeners;

import org.bukkit.entity.Enderman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;

import fr.taeron.hcf.HCF;

public class MobTargetListener implements Listener{

	

    @EventHandler
    public void target(EntityTargetEvent e){
    	if(HCF.getPlugin().getTimerManager().eventTimer.getRemaining() > 0){
    		e.setCancelled(true);
    	} else {
    		if(!(e.getEntity() instanceof Enderman)){
    			e.setCancelled(true);
    		}
    	}
    }
}
