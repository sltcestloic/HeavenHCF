package fr.taeron.hcf.pvpclass;

import org.bukkit.potion.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import java.util.*;
import java.util.concurrent.*;

public abstract class PvpClass
{
    public static final long DEFAULT_MAX_DURATION;
    protected final Set<PotionEffect> passiveEffects;
    protected final String name;
    protected final long warmupDelay;
    
    public PvpClass(final String name, final long warmupDelay) {
        this.passiveEffects = new HashSet<PotionEffect>();
        this.name = name;
        this.warmupDelay = warmupDelay;
    }
    
    public String getName() {
        return this.name;
    }
    
    public long getWarmupDelay() {
        return this.warmupDelay;
    }
    
    public boolean onEquip(final Player player) {
        for (final PotionEffect effect : this.passiveEffects) {
            player.addPotionEffect(effect, true);
        }
        player.sendMessage(ChatColor.YELLOW + "Tu as équipé la classe " + ChatColor.GOLD + this.name);
        return true;
    }
    
    public void onUnequip(final Player player) {
        for (final PotionEffect effect : this.passiveEffects) {
            for (final PotionEffect active : player.getActivePotionEffects()) {
                if (active.getDuration() > PvpClass.DEFAULT_MAX_DURATION && active.getType().equals((Object)effect.getType()) && active.getAmplifier() == effect.getAmplifier()) {
                    player.removePotionEffect(effect.getType());
                    break;
                }
            }
        }
        player.sendMessage(ChatColor.YELLOW + "Tu as déséquipé la classe " + ChatColor.GOLD + this.name);
    }
    
    public abstract boolean isApplicableFor(final Player p0);
    
    static {
        DEFAULT_MAX_DURATION = TimeUnit.MINUTES.toMillis(8L);
    }
}
