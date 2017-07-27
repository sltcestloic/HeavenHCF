package fr.taeron.hcf.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SpawnerCommand implements CommandExecutor{
	
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
		if (args.length == 4) {
			if (args[0].equalsIgnoreCase("give")) {
      		   final Player p = Bukkit.getPlayer(args[1]);
      		   if (p != null) {
      			   final EntityType type = EntityType.valueOf(args[2].toUpperCase());
      			   if (type != null) {
      				   final ItemStack item = getSpawnerItem(Integer.valueOf(args[3]), type);
      			   	   p.getInventory().addItem(new ItemStack[] { item });
      			   	}
                	else {
                            sender.sendMessage("§cType d'entité inconnue: " + args[2]);
                        }
                    }
                    else {
                        sender.sendMessage("§cCe joueur n'est pas connecté.");
                    }
                }
            }
            else {
                sender.sendMessage("§cUtilisation: /spawner give [player] [type] [quantité]");
            }
		return false;
	}
    
    public static ItemStack getSpawnerItem(final int amount, final EntityType type) {
        final ItemStack item = new ItemStack(Material.MOB_SPAWNER, amount);
        final List<String> lore = new ArrayList<String>();
        String loreString = type.toString();
        loreString = String.valueOf(loreString.substring(0, 1).toUpperCase()) + loreString.substring(1).toLowerCase();
        loreString = String.valueOf(loreString) + " Spawner";
        lore.add(loreString);
        final ItemMeta meta = item.getItemMeta();
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}
