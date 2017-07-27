package fr.taeron.hcf.listener.fixes;

import org.bukkit.event.weather.*;
import org.bukkit.event.*;

public class WeatherFixListener implements Listener
{
    @EventHandler
    public void onWeatherChange(final WeatherChangeEvent e) {
    	if(e.toWeatherState()){
    		e.setCancelled(true);
    	}
    }
}
