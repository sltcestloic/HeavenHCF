package fr.taeron.hcf.crate.argument;

import fr.taeron.hcf.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import fr.taeron.hcf.crate.*;
import org.bukkit.inventory.*;
import org.heavenmc.core.util.JavaUtils;
import org.heavenmc.core.util.command.CommandArgument;
import org.bukkit.*;
import java.util.stream.*;
import java.util.*;

public class KeyGiveArgument extends CommandArgument{
	
    private final HCF plugin;
    
    public KeyGiveArgument(final HCF plugin) {
        super("give", "Donner des keys a un joueur");
        this.plugin = plugin;
        this.aliases = new String[] { "send" };
        this.permission = "command.loot.argument." + this.getName();
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <joueur> <type> <nombre>";
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Utilisation: " + this.getUsage(label));
            return true;
        }
        final Player target = Bukkit.getPlayer(args[1]);
        if (target == null || (sender instanceof Player && !((Player)sender).canSee(target))) {
            sender.sendMessage(ChatColor.GOLD + "Le joueur '" + ChatColor.WHITE + args[1] + ChatColor.GOLD + "' n'est pas connecté.");
            return true;
        }
        final Key key = this.plugin.getKeyManager().getKey(args[2]);
        if (key == null) {
            sender.sendMessage(ChatColor.RED + "Il n'y a pas de clé avec le nom '" + args[2] + "'.");
            return true;
        }
        Integer quantity;
        if (args.length >= 4) {
            quantity = JavaUtils.tryParseInt(args[3]);
            if (quantity == null) {
                sender.sendMessage(ChatColor.RED + "'" + args[3] + "' n'est pas un nombre.");
                return true;
            }
        }
        else {
            quantity = 1;
        }
        if (quantity <= 0) {
            sender.sendMessage(ChatColor.RED + "La quantité doit être positive.");
            return true;
        }
        final ItemStack stack = key.getItemStack().clone();
        final int maxAmount = 20;
        if (quantity > maxAmount) {
            sender.sendMessage(ChatColor.RED + "Tu ne peux pas donner plus de " + maxAmount + " clées");
            return true;
        }
        stack.setAmount((int)quantity);
        final PlayerInventory inventory = target.getInventory();
        final Location location = target.getLocation();
        final World world = target.getWorld();
        final Map<Integer, ItemStack> excess = (Map<Integer, ItemStack>)inventory.addItem(new ItemStack[] { stack });
        for (final ItemStack entry : excess.values()) {
            world.dropItemNaturally(location, entry);
        }
        sender.sendMessage(ChatColor.GREEN + "Tu as donné " + quantity + "x " + key.getDisplayName() + ChatColor.GREEN + " key à " + target.getName() + '.');
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length == 2) {
            return null;
        }
        if (args.length == 3) {
            return this.plugin.getKeyManager().getKeys().stream().map(Key::getName).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
