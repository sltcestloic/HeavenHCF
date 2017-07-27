package fr.taeron.hcf.tablist.listeners;

import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import fr.taeron.hcf.HCF;
import fr.taeron.hcf.tablist.TabList;
import fr.taeron.hcf.tablist.TabSlot;

public class TabPacketListener extends PacketAdapter {
	
    private HCF plugin;
    
    @SuppressWarnings("deprecation")
	public TabPacketListener(HCF plugin) {
        super(plugin, ConnectionSide.SERVER_SIDE, new Integer[] { 201 });
        this.plugin = plugin;
        plugin.protocolManager.addPacketListener((com.comphenix.protocol.events.PacketListener)this);
    }
    
    @SuppressWarnings("deprecation")
	public void onPacketSending(PacketEvent event) {
    	CraftPlayer cp = (CraftPlayer) event.getPlayer();
    	if(cp.getHandle().playerConnection.networkManager.getVersion() > 5){
    		return;
    	}
        if (!event.isCancelled() && event.getPacketID() == 201) {
            final PacketContainer packet = event.getPacket();
            final Player player = event.getPlayer();
            int ping = (int)packet.getIntegers().read(0);
            if (ping != -1) {
                try {
                    ping = (int)packet.getIntegers().read(2);
                }
                catch (Exception ex) {}
            }
            if (ping == -1) {
                final TabList list = this.plugin.tabLists.get(player.getName());
                ping = list.getDefaultPing();
                final String name = (String)packet.getStrings().read(0);
                for (int i = 0; i < 60; ++i) {
                    final TabSlot slot = list.getSlot(i);
                    if (slot != null && slot.getName().equals(name)) {
                        ping = slot.getPing();
                        break;
                    }
                }
                for (int i = 0; i < 60; ++i) {
                    final TabSlot slot = list.getSlot(i);
                    for (int j = 0; j < 60; ++j) {
                        final TabSlot tabSlot = list.getSlot(j);
                        if (slot != null && tabSlot != null && i != j && slot.getName().equals(tabSlot.getName())) {
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
                packet.getIntegers().write(0, ping);
                try {
                    packet.getIntegers().write(2, ping);
                }
                catch (Exception ex2) {}
                event.setPacket(packet);
                return;
            }
            event.setCancelled(true);
        }
    }
}
