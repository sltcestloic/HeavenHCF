package fr.taeron.hcf.faction;

import org.bukkit.configuration.serialization.*;

import fr.taeron.hcf.faction.struct.*;
import org.bukkit.entity.*;
import java.util.*;
import com.google.common.collect.*;
import org.bukkit.*;
import com.google.common.base.*;
import org.heavenmc.core.util.*;


public class FactionMember implements ConfigurationSerializable{
	
    private final UUID uniqueID;
    private ChatChannel chatChannel;
    private Role role;
    
    public FactionMember(final Player player, final ChatChannel chatChannel, final Role role) {
        this.uniqueID = player.getUniqueId();
        this.chatChannel = chatChannel;
        this.role = role;
    }
    
    @SuppressWarnings({"unchecked", "rawtypes"})
	public FactionMember(final Map<String, Object> map) {
        this.uniqueID = UUID.fromString((String) map.get("uniqueID"));
        this.chatChannel = (ChatChannel)GuavaCompat.getIfPresent((Class)ChatChannel.class, (String)map.get("chatChannel")).or((Object)ChatChannel.PUBLIC);
        this.role = (Role)GuavaCompat.getIfPresent((Class)Role.class, (String)map.get("role")).or((Object)Role.MEMBER);
    }
  
    public Map<String, Object> serialize() {
        final Map<String, Object> map = Maps.newLinkedHashMap();
        map.put("uniqueID", this.uniqueID.toString());
        map.put("chatChannel", this.chatChannel.name());
        map.put("role", this.role.name());
        return map;
    }
    
    public String getName() {
        final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(this.uniqueID);
        return (offlinePlayer.hasPlayedBefore() || offlinePlayer.isOnline()) ? offlinePlayer.getName() : null;
    }
    
    public UUID getUniqueId() {
        return this.uniqueID;
    }
    
    public ChatChannel getChatChannel() {
        return this.chatChannel;
    }
    
    public void setChatChannel(final ChatChannel chatChannel) {
        Preconditions.checkNotNull((Object)chatChannel, (Object)"ChatChannel cannot be null");
        this.chatChannel = chatChannel;
    }
    
    public Role getRole() {
        return this.role;
    }
    
    public void setRole(final Role role) {
        this.role = role;
    }
    
    public Player toOnlinePlayer() {
        return Bukkit.getPlayer(this.uniqueID);
    }
}
