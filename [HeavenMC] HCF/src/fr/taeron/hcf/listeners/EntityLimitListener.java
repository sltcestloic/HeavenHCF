package fr.taeron.hcf.listeners;

import org.bukkit.event.entity.*;
import org.bukkit.event.*;

public class EntityLimitListener implements Listener
{

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onCreatureSpawn(final CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SLIME_SPLIT) {
            return;
        }
        switch (event.getSpawnReason()) {
            case NATURAL: {
                if (event.getLocation().getChunk().getEntities().length > 25) {
                    event.setCancelled(true);
                    break;
                }
                break;
            }
            case CHUNK_GEN: {
                if (event.getLocation().getChunk().getEntities().length > 25) {
                    event.setCancelled(true);
                    break;
                }
                break;
            }
            default: {
            	break;
            }
        }
    }
}
