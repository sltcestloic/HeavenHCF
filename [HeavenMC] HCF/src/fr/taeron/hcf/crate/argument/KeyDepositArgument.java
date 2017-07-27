package fr.taeron.hcf.crate.argument;

import fr.taeron.hcf.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import org.bukkit.inventory.*;
import org.heavenmc.core.util.JavaUtils;
import org.heavenmc.core.util.command.CommandArgument;

import fr.taeron.hcf.crate.*;
import java.util.*;

public class KeyDepositArgument extends CommandArgument
{
    private final HCF plugin;
    
    public KeyDepositArgument(final HCF plugin) {
        super("deposit", "Déposer des keys dans ta banque");
        this.plugin = plugin;
        this.aliases = new String[] { "store" };
        this.permission = "command.loot.argument." + this.getName();
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <amount>";
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Utilisation: " + this.getUsage(label));
            return true;
        }
        final Player player = (Player)sender;
        final UUID uuid = player.getUniqueId();
        final ItemStack stack = player.getItemInHand();
        final Key key = this.plugin.getKeyManager().getKey(stack);
        if (key == null) {
            sender.sendMessage(ChatColor.RED + "Tu dois avoir une crate key dans la main.");
            return true;
        }
        final Integer quantity = JavaUtils.tryParseInt(args[1]);
        if (quantity == null) {
            sender.sendMessage(ChatColor.RED + "'" + args[1] + "' n'est pas un nombre.");
            return true;
        }
        if (quantity <= 0) {
            sender.sendMessage(ChatColor.RED + "Le nombre doit être positif.");
            return true;
        }
        final String keyName = key.getName();
        if (quantity > stack.getAmount()) {
            sender.sendMessage(ChatColor.RED + "Tu ne peux pas déposer " + quantity + ' ' + keyName + " keys, tu n'en possèdes que " + stack.getAmount() + '.');
            return true;
        }
        final Map<String, Integer> crateKeyMap = this.plugin.getKeyManager().getDepositedCrateMap(uuid);
        final int newAmount = crateKeyMap.getOrDefault(keyName, 0) + quantity;
        crateKeyMap.put(keyName, newAmount);
        if (quantity == stack.getAmount()) {
            player.setItemInHand(new ItemStack(Material.AIR, 1));
        }
        else {
            stack.setAmount(stack.getAmount() - quantity);
        }
        sender.sendMessage(ChatColor.YELLOW + "Tu as déposé " + quantity + ' ' + keyName + " key ".toString() + ((quantity > 1) ? "s" : "") + ". Il t'en restes désormais " + ChatColor.LIGHT_PURPLE + newAmount + ChatColor.YELLOW + ".");
        return true;
    }
}
