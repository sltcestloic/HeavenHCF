package fr.taeron.hcf.listener;

import org.bukkit.event.player.*;
import org.bukkit.*;
import org.bukkit.event.*;

public class EndListener implements Listener
{
    private final Location endExitLocation;
    
    public EndListener() {
        this.endExitLocation = new Location(Bukkit.getWorld("world"), 0.0, 75.0, 300.0);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerPortal(final PlayerPortalEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
            if (event.getTo().getWorld().getEnvironment() == World.Environment.THE_END) {
                event.setTo(event.getTo().getWorld().getSpawnLocation().clone().add(0.5, 0.0, 0.5));
            }
            else if (event.getFrom().getWorld().getEnvironment() == World.Environment.THE_END) {
                event.setTo(this.endExitLocation);
            }
        }
    }
}
