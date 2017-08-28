package fr.taeron.hcf.timer;

import org.bukkit.event.*;

import fr.taeron.hcf.timer.type.*;
import org.bukkit.plugin.java.*;
import org.heavenmc.core.util.Config;

import fr.taeron.hcf.*;
import fr.taeron.hcf.events.*;

import org.bukkit.plugin.*;
import java.util.*;

public class TimerManager implements Listener{
	
    public final LogoutTimer logoutTimer;
    public final EnderPearlTimer enderPearlTimer;
    public final NotchAppleTimer notchAppleTimer;
    public final PvpProtectionTimer pvpProtectionTimer;
    public final PvpClassWarmupTimer pvpClassWarmupTimer;
    public final StuckTimer stuckTimer;
    public final SpawnTagTimer spawnTagTimer;
    public final TeleportTimer teleportTimer;
    public final EventTimer eventTimer;
    public final ArcherTimer archerTimer;
    public final StarterTimer starterTimer;
    private final Set<Timer> timers;
    private final JavaPlugin plugin;
    private Config config;
    
    public TimerManager(final HCF plugin) {
        this.timers = new HashSet<Timer>();
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)plugin);
        this.registerTimer(this.archerTimer = new ArcherTimer(plugin));
        this.registerTimer(this.enderPearlTimer = new EnderPearlTimer(plugin));
        this.registerTimer(this.logoutTimer = new LogoutTimer());
        this.registerTimer(this.notchAppleTimer = new NotchAppleTimer(plugin));
        this.registerTimer(this.stuckTimer = new StuckTimer());
        this.registerTimer(this.pvpProtectionTimer = new PvpProtectionTimer(plugin));
        this.registerTimer(this.spawnTagTimer = new SpawnTagTimer(plugin));
        this.registerTimer(this.teleportTimer = new TeleportTimer(plugin));
        this.registerTimer(this.eventTimer = new EventTimer(plugin));
        this.registerTimer(this.pvpClassWarmupTimer = new PvpClassWarmupTimer(plugin));
        this.registerTimer(this.starterTimer = new StarterTimer(plugin));
        this.reloadTimerData();
    }
    
    
    
    
    public Collection<Timer> getTimers() {
        return this.timers;
    }
    
    public void registerTimer(final Timer timer) {
        this.timers.add(timer);
        if (timer instanceof Listener) {
            this.plugin.getServer().getPluginManager().registerEvents((Listener)timer, (Plugin)this.plugin);
        }
    }
    
    public void unregisterTimer(final Timer timer) {
        this.timers.remove(timer);
    }
    
    public void reloadTimerData() {
        this.config = new Config(this.plugin, "timers");
        for (final Timer timer : this.timers) {
            timer.load(this.config);
        }
    }
    
    public void saveTimerData() {
        for (final Timer timer : this.timers) {
            timer.onDisable(this.config);
        }
        this.config.save();
    }
}
