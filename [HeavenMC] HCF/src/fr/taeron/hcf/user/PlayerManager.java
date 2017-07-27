package fr.taeron.hcf.user;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.heavenmc.core.Core;

import fr.taeron.hcf.HCF;

public class PlayerManager extends HashMap<UUID, PlayerData>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public void loadPlayer(Player p){
		new BukkitRunnable(){
			@Override
			public void run() {
				java.sql.Connection c = Core.getInstance().getConnection();
				try {
					PreparedStatement s = c.prepareStatement("SELECT * FROM `players` WHERE uuid = ?");
					s.setString(1, p.getUniqueId().toString());
					ResultSet rs = s.executeQuery();
					if (rs.next()) {
						int playerid = rs.getInt("playerid");
						PlayerData pd = new PlayerData(p.getUniqueId(), playerid);
						PlayerManager.this.put(p.getUniqueId(), pd);
					}
				} catch (SQLException e){
				}
			}
		}.runTaskAsynchronously(HCF.getPlugin());
	}
	
	public PlayerData getPlayer(Player p){
		return this.get(p.getUniqueId());
	}
	
	public void removePlayer(Player p){
		this.remove(p.getUniqueId());
	}
}
