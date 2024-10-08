package fr.taeron.hcf.pvpclass.bard;

import org.bukkit.scheduler.*;
import com.google.common.base.*;

public class BardData
{
    public static final double MIN_ENERGY = 0.0;
    public static final double MAX_ENERGY = 120.0;
    public static final long MAX_ENERGY_MILLIS = 120000L;
    public long buffCooldown;
    public BukkitTask heldTask;
    private long energyStart;
    
    public long getRemainingBuffDelay() {
        return this.buffCooldown - System.currentTimeMillis();
    }
    
    public void startEnergyTracking() {
        this.setEnergy(0.0);
    }
    
    public long getEnergyMillis() {
        if (this.energyStart == 0L) {
            return 0L;
        }
        return Math.min(120000L, (long)(1.25 * (System.currentTimeMillis() - this.energyStart)));
    }
    
    public double getEnergy() {
        final double value = this.getEnergyMillis() / 1000.0;
        return Math.round(value * 10.0) / 10.0;
    }
    
    public void setEnergy(final double energy) {
        Preconditions.checkArgument(energy >= 0.0, (Object)"Energy cannot be less than 0.0");
        Preconditions.checkArgument(energy <= 120.0, (Object)"Energy cannot be more than 120.0");
        this.energyStart = (long)(System.currentTimeMillis() - 1000.0 * energy);
    }
}
