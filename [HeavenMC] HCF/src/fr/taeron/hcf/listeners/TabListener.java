package fr.taeron.hcf.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.heavenmc.core.tablist.tab.PlayerTab;
import org.heavenmc.core.tablist.tab.event.PlayerTabCreateEvent;

import fr.taeron.hcf.HCF;
import fr.taeron.hcf.faction.type.PlayerFaction;
import fr.taeron.hcf.user.FactionUser;

public class TabListener implements Listener{

	
	@EventHandler
	public void createTab(PlayerTabCreateEvent e){
		PlayerTab tab = e.getPlayerTab();
		tab.getByPosition(0, 1).text("§ePosition:").send();
		tab.getByPosition(2, 1).text("§eEnd Portal:").send();
		tab.getByPosition(0, 2).text("§f" + e.getPlayer().getLocation().getBlockX() + ", " + e.getPlayer().getLocation().getBlockY() + ", " + e.getPlayer().getLocation().getBlockZ()).send();
		tab.getByPosition(2, 2).text("§f1000, 1000").send();
		tab.getByPosition(0, 4).text("§eStats:").send();
		tab.getByPosition(2, 4).text("§eKit de la map:").send();
		tab.getByPosition(0, 5).text("  §eKills: §f" + HCF.getPlugin().getUserManager().getUser(e.getPlayer().getUniqueId()).getKills()).send();
		tab.getByPosition(2, 5).text("§fP1, T1").send();
		tab.getByPosition(0, 6).text("  §eMorts: §f" + HCF.getPlugin().getUserManager().getUser(e.getPlayer().getUniqueId()).getDeaths()).send();
		tab.getByPosition(2, 7).text("§eSOTW:").send();
		tab.getByPosition(0, 8).text("§eConnectés: §f" + Bukkit.getOnlinePlayersList().size()).send();
		tab.getByPosition(2, 8).text("§f23/09/2017").send();
		tab.getByPosition(0, 10).text("§eMinerais:").send();
		tab.getByPosition(2, 10).text("§eFaction:").send();
		FactionUser user = HCF.getPlugin().getUserManager().getUser(e.getPlayer().getUniqueId());
		tab.getByPosition(0, 11).text("§bDiamonds: §f" + user.diamonds).send();
		tab.getByPosition(0, 12).text("§eEmeralds: §f" + user.emerald).send();
		tab.getByPosition(0, 13).text("§6Golds: §f" + user.gold).send();
		tab.getByPosition(0, 14).text("§7Irons: §f" + user.iron).send();
		tab.getByPosition(0, 15).text("§cRedstone: §f" + user.redstone).send();
		tab.getByPosition(0, 16).text("§9Lapis: §f" + user.lapis).send();
		PlayerFaction f =  HCF.getPlugin().getFactionManager().getPlayerFaction(e.getPlayer().getUniqueId());
		if(f == null){
			tab.getByPosition(2, 11).text("§eNom: §fN/A").send();
			tab.getByPosition(2, 12).text("§eChef: §fN/A").send();
			tab.getByPosition(2, 13).text("§eDTR: §fN/A").send();
			tab.getByPosition(2, 14).text("§eMembres: §fN/A").send();
			tab.getByPosition(2, 15).text("§eBalance: §fN/A").send();
			tab.getByPosition(2, 16).text("§eHome: §fN/A").send();
		} else {
			tab.getByPosition(2, 11).text("§eNom: §f" + f.getName()).send();
			tab.getByPosition(2, 12).text("§eChef: §f" + f.getLeader().getName()).send();
			tab.getByPosition(2, 13).text("§eDTR: §f" +  String.valueOf(f.getDeathsUntilRaidable()).substring(0, 3) + f.getRegenStatus().getSymbol()).send();
			tab.getByPosition(2, 14).text("§eMembres: §f" + f.getOnlineMembers().size() + "/" + f.getMembers().size()).send();
			tab.getByPosition(2, 15).text("§eBalance: §f" + String.valueOf(f.getBalance())).send();
			if(f.getHome() != null){
				tab.getByPosition(2, 16).text("§eHome: §f" + f.getHome().getBlockX() + ", " + f.getHome().getBlockY() + ", " + f.getHome().getBlockZ());
			} else {
				tab.getByPosition(2, 16).text("§eHome: §fN/A").send();
			}
		}
	}
}
