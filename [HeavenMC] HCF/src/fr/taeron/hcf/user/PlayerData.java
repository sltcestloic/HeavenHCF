package fr.taeron.hcf.user;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.heavenmc.core.Core;
import org.heavenmc.core.user.HeavenUser;

public class PlayerData extends HeavenUser {

	public PlayerData(UUID u, int id) {
		super(u, id);
		Core.getInstance().getPlayerManager().addPlayer(this, Bukkit.getPlayer(u));
	}

	@Override
	public String getTeamFor(Player arg0) {
		return null;
	}
}
