package fr.taeron.hcf.tablist;

import org.bukkit.entity.*;
import java.util.*;
import com.comphenix.protocol.*;
import com.comphenix.protocol.reflect.*;
import fr.taeron.hcf.HCF;

import java.lang.reflect.*;
import com.comphenix.protocol.events.*;

public class TabList
{
    HCF plugin;
    Player player;
    int defaultPing;
    HashMap<Integer, TabSlot> slots;
    HashMap<Integer, TabSlot> toRemove;
    
    TabList(final HCF plugin, final Player player) {
        this.defaultPing = 0;
        this.slots = new HashMap<Integer, TabSlot>();
        this.toRemove = new HashMap<Integer, TabSlot>();
        this.plugin = plugin;
        this.player = player;
    }
    
    public TabSlot getSlot(final int slot) {
        return this.slots.get(slot);
    }
    
    public void setDefaultPing(final int ping) {
        this.defaultPing = ping;
    }
    
    public int getDefaultPing() {
        return this.defaultPing;
    }
    
    public void clearSlot(final int slot) {
        final TabSlot tabSlot = this.slots.remove(slot);
        if (tabSlot == null) {
            return;
        }
        tabSlot.toRemove = true;
    }
    
    public TabSlot setSlot(final int slot, final String name) {
        final TabSlot tabSlot = new TabSlot(this, name);
        this.slots.put(slot, tabSlot);
        return tabSlot;
    }
    
    public TabSlot setSlot(final int slot, final String prefix, final String name, final String suffix) {
        final TabSlot tabSlot = new TabSlot(this, prefix, name, suffix);
        this.slots.put(slot, tabSlot);
        return tabSlot;
    }
    
    public void send() {
        for (int i = 0; i < 60; ++i) {
            final TabSlot slot = this.slots.get(i);
            if (slot != null) {
                this.toRemove.put(i, slot);
                slot.sent = true;
                final PacketContainer packet = this.plugin.protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO);
                packet.getStrings().write(0, slot.name);
                try {
                    packet.getBooleans().write(0, true);
                }
                catch (FieldAccessException ex) {
                    packet.getIntegers().write(1, 0);
                }
                packet.getIntegers().write(0, (-1));
                try {
                    packet.getIntegers().write(2, (-1));
                }
                catch (FieldAccessException ex3) {}
                try {
                    this.plugin.protocolManager.sendServerPacket(this.player, packet);
                }
                catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                if (slot.teamExists) {
                    final PacketContainer team = this.plugin.buildTeamPacket(slot.getName(), slot.getName(), slot.getPrefix(), slot.getSuffix(), 0, slot.getName());
                    try {
                        this.plugin.protocolManager.sendServerPacket(this.player, team);
                    }
                    catch (InvocationTargetException e2) {
                        e2.printStackTrace();
                    }
                }
            }
            else {
                String nullName = "§" + String.valueOf(i);
                if (i >= 10) {
                    nullName = "§" + String.valueOf(i / 10) + "§" + String.valueOf(i % 10);
                }
                final PacketContainer packet2 = this.plugin.protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO);
                packet2.getStrings().write(0, nullName);
                try {
                    packet2.getBooleans().write(0, true);
                }
                catch (FieldAccessException ex2) {
                    packet2.getIntegers().write(1, 0);
                }
                packet2.getIntegers().write(0, (-1));
                try {
                    packet2.getIntegers().write(2, (-1));
                }
                catch (FieldAccessException ex4) {}
                try {
                    this.plugin.protocolManager.sendServerPacket(this.player, packet2);
                }
                catch (InvocationTargetException e2) {
                    e2.printStackTrace();
                }
            }
        }
    }
    
    public void update() {
        this.clear();
        this.send();
    }
    
    public void clear() {
        for (int i = 0; i < 60; ++i) {
            final TabSlot slot = this.toRemove.remove(i);
            if (slot != null) {
                slot.sent = false;
                if (slot.teamExists) {
                    final PacketContainer team = this.plugin.buildTeamPacket(slot.getName(), slot.getName(), null, null, 1, slot.getName());
                    try {
                        this.plugin.protocolManager.sendServerPacket(this.player, team);
                    }
                    catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
                final PacketContainer packet = this.plugin.protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO);
                packet.getStrings().write(0, slot.name);
                try {
                    packet.getBooleans().write(0, false);
                }
                catch (FieldAccessException ex) {
                    packet.getIntegers().write(1, 1);
                }
                packet.getIntegers().write(0, (-1));
                try {
                    packet.getIntegers().write(2, (-1));
                }
                catch (FieldAccessException ex3) {}
                try {
                    this.plugin.protocolManager.sendServerPacket(this.player, packet);
                }
                catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            else {
                String nullName = "§" + String.valueOf(i);
                if (i >= 10) {
                    nullName = "§" + String.valueOf(i / 10) + "§" + String.valueOf(i % 10);
                }
                final PacketContainer packet2 = this.plugin.protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO);
                packet2.getStrings().write(0, nullName);
                try {
                    packet2.getBooleans().write(0, false);
                }
                catch (FieldAccessException ex2) {
                    packet2.getIntegers().write(1, 1);
                }
                packet2.getIntegers().write(0, (-1));
                try {
                    packet2.getIntegers().write(2, (-1));
                }
                catch (FieldAccessException ex4) {}
                try {
                    this.plugin.protocolManager.sendServerPacket(this.player, packet2);
                }
                catch (InvocationTargetException e2) {
                    e2.printStackTrace();
                }
            }
        }
    }
}
