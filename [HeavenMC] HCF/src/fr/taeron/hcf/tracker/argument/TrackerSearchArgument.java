package fr.taeron.hcf.tracker.argument;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.heavenmc.core.util.command.CommandArgument;

import fr.taeron.hcf.HCF;
import fr.taeron.hcf.user.FactionUser;

public class TrackerSearchArgument extends CommandArgument{

	public TrackerSearchArgument() {
		super("search", "Rechercher un joueur à traquer");
	}

	@Override
	public String getUsage(String label) {
		return ChatColor.YELLOW + "/" + label + " search";
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)){
			return false;
		}
		Player p = (Player) sender;
		FactionUser user = HCF.getPlugin().getUserManager().getUser(p.getUniqueId());
		if(user.isTrackingPlayer()){
			sender.sendMessage(ChatColor.RED + "Tu es déjà en train de traquer un joueur.");
			return false;
		}
		if(user.getTrackingStartTime() != 0){
			String wait = "2 heures";
			long time = TimeUnit.HOURS.toMillis(2L);
			long elapsed = System.currentTimeMillis() - user.getTrackingStartTime();
			if(p.hasPermission("heaven.vip")){
				time = TimeUnit.MINUTES.toMillis(90L);
				wait = "1 heure 30";
			}
			if(p.hasPermission("heaven.pro")){
				time = TimeUnit.MINUTES.toMillis(60L);
				wait = "1 heure";
			}
			if(p.hasPermission("heaven.elite")){
				time = TimeUnit.MINUTES.toMillis(30L);
				wait = "30 minutes";
			}
			if(elapsed < time){
				p.sendMessage(ChatColor.RED + "Tu as déjà lancé une traque il y à " + HCF.getRemaining(user.getTrackingStartTime(), true, false) + ". Tu dois attendre " + wait + " entre chaque traque.");
				if(!p.hasPermission("heaven.vip") && !p.hasPermission("heaven.pro") && !p.hasPermission("heaven.elite")){
					p.sendMessage("§e§oSi tu le souhaites, tu peux acheter un grade pour avoir un temps d'attente réduit et pour ne plus voir ce message.");
				}
				return false;
			}
		}
		ArrayList<Player> rdn = new ArrayList<Player>();
		for(Player randy : Bukkit.getOnlinePlayers()){
			if(HCF.getPlugin().getFactionManager().getClaimAt(randy.getLocation()).getFaction().isDeathban() && HCF.getPlugin().getFactionManager().getPlayerFaction(randy) != null && HCF.getPlugin().getFactionManager().getPlayerFaction(randy) != HCF.getPlugin().getFactionManager().getPlayerFaction(p) && !HCF.getPlugin().getFactionManager().getPlayerFaction(p).getAlliedFactions().contains(HCF.getPlugin().getFactionManager().getPlayerFaction(randy))){
				rdn.add(randy);
			}
		}
		if(rdn.size() < 1){
			sender.sendMessage(ChatColor.RED + "Impossible de trouver un joueur à traquer, réessayes plus tard !");
			return false;
		}
		Player t = rdn.get(new Random().nextInt(rdn.size()));
		if(t == null){
			sender.sendMessage(ChatColor.RED + "Le tracker est tombé sur un joueur déconnecté, merci de réessayer !");
			return false;
		}
		user.setTrackedUser(t);
		FactionUser tracked = HCF.getPlugin().getUserManager().getUser(t.getUniqueId());
		sender.sendMessage("§eTraque lancée sur §6" + t.getName() + "§7(" + tracked.getKills() + " kills)");
		return false;
	}
}
