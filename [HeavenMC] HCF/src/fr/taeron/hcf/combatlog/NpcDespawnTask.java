package fr.taeron.hcf.combatlog;

import org.bukkit.plugin.*;

import fr.taeron.hcf.HCF;

public class NpcDespawnTask implements Runnable
{
    private final HCF plugin;
    private final Npc npc;
    private long time;
    private int taskId;
    
    public NpcDespawnTask(final HCF plugin, final Npc npc, final long time) {
        this.plugin = plugin;
        this.npc = npc;
        this.time = time;
    }
    
    public long getTime() {
        return this.time;
    }
    
    public void setTime(final long time) {
        this.time = time;
    }
    
    public Npc getNpc() {
        return this.npc;
    }
    
    public void start() {
        this.taskId = this.plugin.getServer().getScheduler().runTaskTimer((Plugin)this.plugin, (Runnable)this, 1L, 1L).getTaskId();
    }
    
    public void stop() {
        this.plugin.getServer().getScheduler().cancelTask(this.taskId);
    }
    
    @Override
    public void run() {
        if (this.time > System.currentTimeMillis()) {
            return;
        } 
        this.plugin.getNpcManager().despawn(this.npc);
    }
}
