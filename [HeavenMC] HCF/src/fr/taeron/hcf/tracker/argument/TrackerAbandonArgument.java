package fr.taeron.hcf.tracker.argument;

import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.heavenmc.core.util.command.CommandArgument;

import fr.taeron.hcf.HCF;
import fr.taeron.hcf.user.FactionUser;

public class TrackerAbandonArgument extends CommandArgument{
	
	public TrackerAbandonArgument(){
		super("abandon", "Abandonner ta traque actuelle");
	}

	@Override
	public String getUsage(String label) {
		return ChatColor.YELLOW + "/" + label + " abandon";
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)){
			return false;
		}
		Player p = (Player) sender;
		FactionUser user = HCF.getPlugin().getUserManager().getUser(p.getUniqueId());
		if(user.getTrackingUser().isOnline()){
			if(System.currentTimeMillis() - user.getTrackingStartTime() > TimeUnit.MINUTES.toMillis(30L)){
				sender.sendMessage("§aTu as abandonné ta traque sur " + user.getTrackingUser().getName() + ".");
				user.setTrackedUser(null);			
			} else {		
				sender.sendMessage("§cTu dois attendre 30 minutes avant d'abandonner une traque si le joueur est connecté, tu n'as attendu que " + HCF.getRemaining(System.currentTimeMillis() - user.getTrackingStartTime(), true) + ".");
			}
		} else {
			FactionUser tUser = HCF.getPlugin().getUserManager().getUser(user.getTrackingUser().getUniqueId());
			if(System.currentTimeMillis() - tUser.lastLogoutTime > TimeUnit.MINUTES.toMillis(10L)){
				sender.sendMessage("§aTu as abandonné ta traque sur " + user.getTrackingUser().getName() + ".");
				user.setTrackedUser(null);			
			}
		}
		return false;
	}
}