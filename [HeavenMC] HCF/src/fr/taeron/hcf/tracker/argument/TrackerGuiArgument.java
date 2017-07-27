package fr.taeron.hcf.tracker.argument;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.heavenmc.core.util.ItemBuilder;
import org.heavenmc.core.util.command.CommandArgument;

import fr.taeron.hcf.HCF;
import fr.taeron.hcf.user.FactionUser;

public class TrackerGuiArgument extends CommandArgument{
	
	public TrackerGuiArgument(){
		super("gui", "Ouvrir le menu du tracker");
	}

	@Override
	public String getUsage(String label) {
		return ChatColor.YELLOW + "/" + label + " gui";
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)){
			return false;
		}
		Player p = (Player) sender;
		this.openGUI(p);
		return false;
	}
	
	@SuppressWarnings("deprecation")
	public void openGUI(Player p){
		Inventory i = Bukkit.createInventory(null, 54, "§cTracker");
		ItemStack grayPane = new ItemBuilder(Material.STAINED_GLASS_PANE).data((short)7).displayName("").build();
		ItemStack redPane = new ItemBuilder(Material.STAINED_GLASS_PANE).data((short)14).displayName("").build();
		ItemStack whitePane = new ItemBuilder(Material.STAINED_GLASS_PANE).data((short)0).displayName("").build();
		ItemStack abandon;
		ItemStack search;
		FactionUser user = HCF.getPlugin().getUserManager().getUser(p.getUniqueId());
		ItemStack trackHead;
		if(user.getTrackingUser() == null){
			trackHead = new ItemBuilder(Material.ENDER_CHEST).displayName("§aInformations sur ta victime: ").lore(new String[] {"§cTu n'as pas choisit de victime."}).build();
		} else {
			if(!user.getTrackingUser().isOnline()){
				trackHead = new ItemBuilder(Material.SKULL_ITEM).data((short)3).displayName("§aInformations sur ta victime: ").lore(new String[] {"§bPseudo: " + user.getTrackingUser().getName(), "§cCe joueur est déconnecté."}).build();
			} else {
				Player t = user.getTrackingUser();
				double aproxX = this.randInt(-50, 50);
				double aproxZ = this.randInt(-50, 50);
				double X = aproxX > 50 ? t.getLocation().getBlockX() + aproxX : t.getLocation().getBlockX() - aproxX;
				double Z = aproxZ > 50 ? t.getLocation().getBlockZ() + aproxZ : t.getLocation().getBlockZ() - aproxZ;
				trackHead = new ItemBuilder(Material.SKULL_ITEM).data((short)3).displayName("§aInformations sur ta victime: ").lore(new String[] {"§bPseudo: " + user.getTrackingUser().getName(),
					"§e- §7Faction: §e" + HCF.getPlugin().getFactionManager().getPlayerFaction(t).getName(),
					"§e- §7Coordonnées approximatives: §cX: §e" + X  + " §cZ: §e" + Z,
					"§e- §7Territoire actuel: §e" + HCF.getPlugin().getFactionManager().getFactionAt(t.getLocation()).getName(), 
					"§e- §7Monde actuel: §e" + t.getLocation().getWorld().getEnvironment()}).build();
			}
		}
		if(user.getTrackingUser() != null){
			abandon = new ItemBuilder(Material.REDSTONE_TORCH_ON).displayName("§cAbandonner cette victime").build();
			search = new ItemBuilder(Material.SKULL_ITEM).data((short)3).displayName("§e§mRechercher une victime").lore(new String[] {"§7§oTu es déjà en train de traquer une victime."}).build();
		} else {
			abandon = new ItemBuilder(Material.REDSTONE_TORCH_ON).displayName("§c§mAbandonner cette victime").lore(new String[] {"§7§oTu n'as pas choisit de victime."}).build();
			search = new ItemBuilder(Material.SKULL_ITEM).data((short)3).displayName("§eRechercher une victime").build();
		}
		i.setItem(0, grayPane);
		i.setItem(1, grayPane);
		i.setItem(2, grayPane);
		i.setItem(3, grayPane);
		i.setItem(4, grayPane);
		i.setItem(5, grayPane);
		i.setItem(6, grayPane);
		i.setItem(7, grayPane);
		i.setItem(8, grayPane);
		i.setItem(9, redPane);
		i.setItem(10, redPane);
		i.setItem(11, redPane);
		i.setItem(12, grayPane);
		i.setItem(13, grayPane);
		i.setItem(14, grayPane);
		i.setItem(15, redPane);
		i.setItem(16, redPane);
		i.setItem(17, redPane);
		i.setItem(18, redPane);
		i.setItem(19, trackHead);
		i.setItem(20, redPane);
		i.setItem(21, grayPane);
		i.setItem(22, grayPane);
		i.setItem(23, grayPane);
		i.setItem(24, redPane);
		i.setItem(25, abandon);
		i.setItem(26, redPane);
		i.setItem(27, redPane);
		i.setItem(28, redPane);
		i.setItem(29, redPane);
		i.setItem(30, grayPane);
		i.setItem(31, grayPane);
		i.setItem(32, grayPane);
		i.setItem(33, redPane);
		i.setItem(34, redPane);
		i.setItem(35, redPane);
		i.setItem(36, grayPane);
		i.setItem(37, grayPane);
		i.setItem(38, grayPane);
		i.setItem(39, grayPane);
		i.setItem(40, grayPane);
		i.setItem(41, grayPane);
		i.setItem(42, grayPane);
		i.setItem(43, grayPane);
		i.setItem(44, grayPane);
		i.setItem(45, whitePane);
		i.setItem(46, whitePane);
		i.setItem(47, whitePane);
		i.setItem(48, whitePane);
		i.setItem(49, search);
		i.setItem(50, whitePane);
		i.setItem(51, whitePane);
		i.setItem(52, whitePane);
		i.setItem(53, whitePane);
		p.openInventory(i);
	}
	
	public int randInt(int min, int max) {

	    // NOTE: This will (intentionally) not run as written so that folks
	    // copy-pasting have to think about how to initialize their
	    // Random instance.  Initialization of the Random instance is outside
	    // the main scope of the question, but some decent options are to have
	    // a field that is initialized once and then re-used as needed or to
	    // use ThreadLocalRandom (if using at least Java 1.7).
	    Random rand = new Random();

	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}
}