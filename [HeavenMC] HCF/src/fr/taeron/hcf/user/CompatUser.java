package fr.taeron.hcf.user;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.heavenmc.core.Core;
import org.heavenmc.core.user.HeavenUser;

import fr.taeron.hcf.HCF;
import fr.taeron.hcf.scoreboard.provider.TimerSidebarProvider;

public class CompatUser extends HeavenUser{

	public CompatUser(UUID u, int id) {
		super(u, id);
		this.setScoreboard(new TimerSidebarProvider(HCF.getPlugin()));
        Core.getInstance().getPlayerManager().addPlayer(this, this.getPlayer());
	}

	@Override
	public String getTeamFor(Player p) {
		if(HCF.getPlugin().getTimerManager().archerTimer.getRemaining(this.getUUID()) > 0){
			return "§c";
		}
		if(HCF.getPlugin().getFactionManager().getPlayerFaction(this.getUUID()) == null){
			return "§e";
		}
		if(HCF.getPlugin().getFactionManager().getPlayerFaction(p.getUniqueId()) == HCF.getPlugin().getFactionManager().getPlayerFaction(this.getUUID())){
			return "§a";
		}
		
		return "§e";
	}

	@Override
	public Player getRandomPlayer() {
		ArrayList<Player> players = new ArrayList<Player>();
		for (Player z : Bukkit.getOnlinePlayers()){
			if(z != this.getPlayer() && z.getGameMode() != GameMode.CREATIVE && !z.getAllowFlight()){
				players.add(z);				
			}
			
		}
		Player randomPlayer = players.get(new Random().nextInt(players.size()));
		return randomPlayer;
	}

}
