package fr.taeron.hcf.flip;


import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.heavenmc.core.util.ItemBuilder;

import fr.taeron.hcf.HCF;
import fr.taeron.hcf.user.FactionUser;

public class FlipCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)){
			return false;
		}
		Player p = (Player) sender;
		FactionUser user = HCF.getPlugin().getUserManager().getUser(p.getUniqueId());
		if(user.hasUsedFlip){
			sender.sendMessage("§cTu as déjà utilisé le /" + label + ". Tu ne peux l'utiliser qu'une fois sur chaque map !");
			return false;
		}
		Inventory i = Bukkit.createInventory(null, 54, "§eFlip");
		ItemStack redPane = new ItemBuilder(Material.STAINED_GLASS_PANE).data((short)14).displayName(" ").build();
		ItemStack grayPane = new ItemBuilder(Material.STAINED_GLASS_PANE).data((short)7).displayName(" ").build();
		ItemStack whitePane = new ItemBuilder(Material.STAINED_GLASS_PANE).data((short)0).displayName(" ").build();
		ItemStack gold = new ItemBuilder(Material.GOLD_BLOCK).displayName("§e6 blocs d'or").build();
		gold.setAmount(6);
		ItemStack iron = new ItemBuilder(Material.IRON_BLOCK).displayName("§f8 blocs de fer").build();
		iron.setAmount(8);
		ItemStack emerald = new ItemBuilder(Material.EMERALD_BLOCK).displayName("§a4 blocs d'emeraude").build();
		emerald.setAmount(4);
		ItemStack diamond = new ItemBuilder(Material.DIAMOND_BLOCK).displayName("§b4 blocs de diamant").build();
		diamond.setAmount(4);
		ItemStack help = new ItemBuilder(Material.REDSTONE_TORCH_ON).displayName("§6Aide:").lore(new String[] {"§e- §7Le /" + label + " te permet de gagner des blocs de minerais de ton choix", "§e- §7Clique sur le minerais que tu veux essayer de gagner", "§e- §7Tu as 1 chance sur 3 de gagner le minerais en question", "§e- §7Tu ne peux utiliser cette commande qu'une fois durant la map"}).build();
		i.setItem(0, redPane);
		i.setItem(1, redPane);
		i.setItem(2, redPane);
		i.setItem(3, grayPane);
		i.setItem(4, whitePane);
		i.setItem(5, grayPane);
		i.setItem(6, redPane);
		i.setItem(7, redPane);
		i.setItem(8, redPane);
		i.setItem(9, redPane);
		i.setItem(10, emerald);
		i.setItem(11, redPane);
		i.setItem(12, grayPane);
		i.setItem(13, whitePane);
		i.setItem(14, grayPane);
		i.setItem(15, redPane);
		i.setItem(16, iron);
		i.setItem(17, redPane);
		i.setItem(18, redPane);
		i.setItem(19, redPane);
		i.setItem(20, redPane);
		i.setItem(21, grayPane);
		i.setItem(22, whitePane);
		i.setItem(23, grayPane);
		i.setItem(24, redPane);
		i.setItem(25, redPane);
		i.setItem(26, redPane);
		i.setItem(27, redPane);
		i.setItem(28, redPane);
		i.setItem(29, redPane);
		i.setItem(30, grayPane);
		i.setItem(31, whitePane);
		i.setItem(32, grayPane);
		i.setItem(33, redPane);
		i.setItem(34, redPane);
		i.setItem(35, redPane);
		i.setItem(36, redPane);
		i.setItem(37, diamond);
		i.setItem(38, redPane);
		i.setItem(39, grayPane);
		i.setItem(40, whitePane);
		i.setItem(41, grayPane);
		i.setItem(42, redPane);
		i.setItem(43, gold);
		i.setItem(44, redPane);
		i.setItem(45, redPane);
		i.setItem(46, redPane);
		i.setItem(47, redPane);
		i.setItem(48, grayPane);
		i.setItem(49, help);
		i.setItem(50, grayPane);
		i.setItem(51, redPane);
		i.setItem(52, redPane);
		i.setItem(53, redPane);
		p.openInventory(i);
		return false;
	}
}
