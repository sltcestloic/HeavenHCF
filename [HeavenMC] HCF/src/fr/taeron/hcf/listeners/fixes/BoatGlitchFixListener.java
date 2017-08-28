package fr.taeron.hcf.listeners.fixes;

import org.bukkit.event.vehicle.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.block.*;
import org.bukkit.event.*;

public class BoatGlitchFixListener implements Listener
{
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onVehicleCreate(final VehicleCreateEvent event) {
        final Vehicle vehicle = event.getVehicle();
        if (vehicle instanceof Boat) {
            final Boat boat = (Boat)vehicle;
            final Block belowBlock = boat.getLocation().add(0.0, -1.0, 0.0).getBlock();
            if (belowBlock.getType() != Material.WATER && belowBlock.getType() != Material.STATIONARY_WATER) {
                boat.remove();
            }
        }
    }
}
