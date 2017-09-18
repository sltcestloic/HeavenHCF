package fr.taeron.hcf.pvpclass;

import fr.taeron.hcf.*;
import fr.taeron.hcf.pvpclass.archer.*;
import fr.taeron.hcf.pvpclass.bard.*;
import fr.taeron.hcf.pvpclass.type.*;
import org.bukkit.plugin.*;
import org.bukkit.*;
import java.util.*;
import org.bukkit.entity.*;
import javax.annotation.*;
import org.bukkit.event.*;
import fr.taeron.hcf.pvpclass.event.*;

public class PvpClassManager
{
    private final Map<UUID, PvpClass> equippedClass;
    private final Map<String, PvpClass> pvpClasses;
    
    public PvpClassManager(final HCF plugin) {
        this.equippedClass = new HashMap<UUID, PvpClass>();
        (this.pvpClasses = new HashMap<String, PvpClass>()).put("Archer", new ArcherClass(plugin));
        this.pvpClasses.put("Bard", new BardClass(plugin));
        this.pvpClasses.put("Mineur", new MinerClass(plugin));
        for (final PvpClass pvpClass : this.pvpClasses.values()) {
            if (pvpClass instanceof Listener) {
                plugin.getServer().getPluginManager().registerEvents((Listener)pvpClass, (Plugin)plugin);
            }	
        }
    }
    
    public void onDisable() {
        for (final Map.Entry<UUID, PvpClass> entry : new HashMap<UUID, PvpClass>(this.equippedClass).entrySet()) {
            this.setEquippedClass(Bukkit.getPlayer((UUID)entry.getKey()), null);
        }
        this.pvpClasses.clear();
        this.equippedClass.clear();
    }
    
    public Collection<PvpClass> getPvpClasses() {
        return this.pvpClasses.values();
    }
    
    public PvpClass getPvpClass(final String name) {
        return this.pvpClasses.get(name);
    }
    
    public PvpClass getEquippedClass(final Player player) {
        synchronized (this.equippedClass) {
            return this.equippedClass.get(player.getUniqueId());
        }
    }
    
    public boolean hasClassEquipped(final Player player, final PvpClass pvpClass) {
        final PvpClass equipped = this.getEquippedClass(player);
        return equipped != null && equipped.equals(pvpClass);
    }
    
    public void setEquippedClass(final Player player, @Nullable final PvpClass pvpClass) {
        final PvpClass equipped = this.getEquippedClass(player);
        if (equipped != null) {
            if (pvpClass == null) {
                this.equippedClass.remove(player.getUniqueId());
                equipped.onUnequip(player);
                Bukkit.getPluginManager().callEvent((Event)new PvpClassUnequipEvent(player, equipped));
                return;
            }
        }
        else if (pvpClass == null) {
            return;
        }
        if (pvpClass.onEquip(player)) {
            this.equippedClass.put(player.getUniqueId(), pvpClass);
            Bukkit.getPluginManager().callEvent((Event)new PvpClassEquipEvent(player, pvpClass));
        }
    }
}
