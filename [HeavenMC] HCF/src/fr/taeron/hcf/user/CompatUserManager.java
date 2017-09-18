package fr.taeron.hcf.user;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

public class CompatUserManager extends HashMap<UUID, CompatUser>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public CompatUser getPlayer(Player p){
		return this.get(p.getUniqueId());
	}
	
	public CompatUser registerPlayer(Player p, int i){
		return this.put(p.getUniqueId(), new CompatUser(p.getUniqueId(), i));
	}
	
	public CompatUser removePlayer(Player p){
		return this.remove(p.getUniqueId());
	}
}
