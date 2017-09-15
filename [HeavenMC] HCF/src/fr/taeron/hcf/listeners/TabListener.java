package fr.taeron.hcf.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.heavenmc.core.tablist.tab.PlayerTab;
import org.heavenmc.core.tablist.tab.event.PlayerTabCreateEvent;

import fr.taeron.hcf.HCF;

public class TabListener implements Listener{

	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void createTab(PlayerTabCreateEvent e){
		PlayerTab tab = e.getPlayerTab();
		tab.getByPosition(0, 1).text("§aPosition:").send();
		tab.getByPosition(2, 1).text("§aEnd Portal:").send();
		tab.getByPosition(0, 2).text("§f" + e.getPlayer().getLocation().getBlockX() + ", " + e.getPlayer().getLocation().getBlockZ()).send();
		tab.getByPosition(2, 2).text("§f1000, 1000").send();
		tab.getByPosition(0, 4).text("§aStats:").send();
		tab.getByPosition(2, 4).text("§aKit de la map:").send();
		tab.getByPosition(0, 5).text("  §aKills: §f" + HCF.getPlugin().getUserManager().getUser(e.getPlayer().getUniqueId()).getKills()).send();
		tab.getByPosition(2, 5).text("§fP1, T1").send();
		tab.getByPosition(0, 6).text("  §aMorts: §f" + HCF.getPlugin().getUserManager().getUser(e.getPlayer().getUniqueId()).getDeaths()).send();
		tab.getByPosition(2, 7).text("§aSOTW:").send();
		tab.getByPosition(0, 8).text("§aConnectés: §f" + Bukkit.getOnlinePlayers().length).send();
		tab.getByPosition(2, 8).text("§f23/09/2017").send();
		tab.getByPosition(0, 10).text("§aMinerais:").send();
		tab.getByPosition(2, 10).text("§aFaction:").send();
	}
}
