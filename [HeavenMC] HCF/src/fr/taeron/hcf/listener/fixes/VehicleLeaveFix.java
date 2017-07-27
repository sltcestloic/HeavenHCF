package fr.taeron.hcf.listener.fixes;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleExitEvent;

public class VehicleLeaveFix implements Listener{

	
	@EventHandler
	public void fix(VehicleExitEvent e){
		e.getExited().teleport(e.getVehicle().getLocation().add(0.5, 0.2, 0.5));
	}
}
