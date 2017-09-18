package fr.taeron.hcf.user;

import fr.taeron.hcf.*;

import org.bukkit.plugin.*;
import org.heavenmc.core.util.Config;


import org.bukkit.event.player.*;
import org.bukkit.event.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.*;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.*;

public class UserManager implements Listener
{
    private final HCF plugin;
    private final Map<UUID, FactionUser> users;
    private Config userConfig;
    
    public UserManager(final HCF plugin) {
        this.users = new HashMap<UUID, FactionUser>();
        this.plugin = plugin;
        this.reloadUserData();
        plugin.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)plugin);
    }
    
    @EventHandler
    public void onConnect(PlayerJoinEvent e){
    	Player p = e.getPlayer();
    	int version = ((CraftPlayer)p).getHandle().playerConnection.networkManager.getVersion();
    	if(version > 7){
    		p.kickPlayer("§cLes connections ne sont autorisées qu'en version 1.7");
    	}
    }
    
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final UUID uuid = event.getPlayer().getUniqueId();
        this.users.putIfAbsent(uuid, new FactionUser(event.getPlayer().getUniqueId()));
        Bukkit.getScheduler().runTaskLater(HCF.getPlugin(), new Runnable(){
			@Override
			public void run() {
				event.getPlayer().sendMessage("§7§m--*----------------------------------------*--");
				event.getPlayer().sendMessage("§2* §7Site: §awww.heavenmc.org");
				event.getPlayer().sendMessage("§2* §7TeamSpeak: §aheavenmc.voice.vg");
				event.getPlayer().sendMessage("§2* §7Boutique: §astore.heavenmc.org");
				event.getPlayer().sendMessage("§7§m--*----------------------------------------*--");
			}
		}, 10L);
    }
    
    
    public Map<UUID, FactionUser> getUsers() {
        return this.users;
    }
    
	public FactionUser getUserAsync(final UUID uuid) {
        synchronized (this.users) {
            return this.users.get(uuid);
        }
    }
    
    public FactionUser getUser(final UUID uuid) {
		return this.users.get(uuid);
    }
    
    public void reloadUserData() {
        this.userConfig = new Config(this.plugin, "faction-users");
        final Object object = this.userConfig.get("users");
        if (object instanceof MemorySection) {
            final MemorySection section = (MemorySection)object;
            final Collection<String> keys = (Collection<String>)section.getKeys(false);
            for (final String id : keys) {
                this.users.put(UUID.fromString(id), (FactionUser)this.userConfig.get(section.getCurrentPath() + '.' + id));
            }
        }
    }
    
    public void saveUserData() {
        final Set<Map.Entry<UUID, FactionUser>> entrySet = this.users.entrySet();
        final Map<String, FactionUser> saveMap = new LinkedHashMap<String, FactionUser>(entrySet.size());
        for (final Map.Entry<UUID, FactionUser> entry : entrySet) {
            saveMap.put(entry.getKey().toString(), entry.getValue());
        }
        this.userConfig.set("users", saveMap);
        this.userConfig.save();
    }
}
