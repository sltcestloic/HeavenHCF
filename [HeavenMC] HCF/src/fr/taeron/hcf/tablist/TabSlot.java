package fr.taeron.hcf.tablist;

import java.lang.reflect.*;
import com.comphenix.protocol.events.*;

public class TabSlot
{
    TabList list;
    boolean sent;
    boolean teamExists;
    boolean toRemove;
    String prefix;
    String name;
    String suffix;
    private int ping;
    
    TabSlot(final TabList list, final String prefix, final String name, final String suffix) {
        this.list = list;
        this.prefix = prefix.substring(0, Math.min(prefix.length(), 16));
        this.name = name.substring(0, Math.min(name.length(), 16));
        this.suffix = suffix.substring(0, Math.min(suffix.length(), 16));
        this.teamExists = true;
        this.sent = false;
        this.ping = list.defaultPing;
    }
    
    TabSlot(final TabList list, final String name) {
        this.list = list;
        this.name = name.substring(0, Math.min(name.length(), 16));
        this.teamExists = false;
        this.sent = false;
        this.ping = list.defaultPing;
    }
    
    public void setPing(final int ping) {
        this.ping = ping;
    }
    
    public String getPrefix() {
        return this.prefix;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getSuffix() {
        return this.suffix;
    }
    
    public int getPing() {
        return this.ping;
    }
    
    public void createPrefixAndSuffix(final String prefix, final String suffix) {
        if (this.toRemove) {
            return;
        }
        if (this.teamExists) {
            this.updatePrefixAndSuffix(prefix, suffix);
            return;
        }
        this.teamExists = true;
        this.prefix = prefix.substring(0, Math.min(prefix.length(), 16));
        this.suffix = suffix.substring(0, Math.min(prefix.length(), 16));
        final PacketContainer packet = this.list.plugin.buildTeamPacket(this.name, this.name, prefix, suffix, 0, this.name);
        try {
            this.list.plugin.protocolManager.sendServerPacket(this.list.player, packet);
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    
    public void updatePrefixAndSuffix(final String prefix, final String suffix) {
        if (this.toRemove) {
            return;
        }
        if (!this.teamExists) {
            this.createPrefixAndSuffix(prefix, suffix);
            return;
        }
        this.prefix = prefix.substring(0, Math.min(prefix.length(), 16));
        this.suffix = suffix.substring(0, Math.min(prefix.length(), 16));
        final PacketContainer packet = this.list.plugin.buildTeamPacket(this.name, this.name, prefix, suffix, 2, this.name);
        try {
            this.list.plugin.protocolManager.sendServerPacket(this.list.player, packet);
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    
    public void removePrefixAndSuffix() {
        if (this.toRemove || !this.teamExists) {
            return;
        }
        this.teamExists = false;
        final PacketContainer packet = this.list.plugin.buildTeamPacket(this.name, this.name, null, null, 1, this.name);
        try {
            this.list.plugin.protocolManager.sendServerPacket(this.list.player, packet);
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
