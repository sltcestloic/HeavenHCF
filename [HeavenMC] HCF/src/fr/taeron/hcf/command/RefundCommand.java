package fr.taeron.hcf.command;

import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import org.bukkit.ChatColor;

import fr.taeron.hcf.listener.*;
import org.bukkit.inventory.*;

public class RefundCommand implements CommandExecutor
{
    public boolean onCommand(final CommandSender cs, final Command cmd, final String s, final String[] args) {
        final String Usage = ChatColor.RED + "/" + s + " <joueur> <raison>";
        if (!(cs instanceof Player)) {
            cs.sendMessage(ChatColor.RED + "You must be a player");
            return true;
        }
        final Player p = (Player)cs;
        if (args.length < 2) {
            cs.sendMessage(Usage);
            return true;
        }
        if (Bukkit.getPlayer(args[0]) == null) {
            p.sendMessage(ChatColor.RED + "Le joueur doit être connecté");
            return true;
        }
        final Player target = Bukkit.getPlayer(args[0]);
        if (DeathListener.PlayerInventoryContents.containsKey(target.getUniqueId())) {
            target.getInventory().setContents((ItemStack[])DeathListener.PlayerInventoryContents.get(target.getUniqueId()));
            target.getInventory().setArmorContents((ItemStack[])DeathListener.PlayerArmorContents.get(target.getUniqueId()));
            Command.broadcastCommandMessage((CommandSender)p, ChatColor.YELLOW + "Tu as rendu ses items à " + target.getName());
            DeathListener.PlayerArmorContents.remove(target.getUniqueId());
            DeathListener.PlayerInventoryContents.remove(target.getUniqueId());
            return true;
        }
        p.sendMessage(ChatColor.RED + "Ce joueur a déjà récuperé ses items");
        return false;
    }
}
