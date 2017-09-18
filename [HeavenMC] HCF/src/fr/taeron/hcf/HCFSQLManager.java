package fr.taeron.hcf;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.heavenmc.core.mysql.MySQLManager;

public class HCFSQLManager extends MySQLManager{

	public HCFSQLManager(JavaPlugin m) {
		super(m);
	}

	@Override
	public void loadPlayer(Player p) {
		this.loadInfos(HCF.getPlugin().getUserManager().getUser(p.getUniqueId()));
	}

	@Override
	public void saveData(Player p) {
		this.saveSettings(HCF.getPlugin().getUserManager().getUser(p.getUniqueId()));
	}
}
