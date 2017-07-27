package fr.taeron.hcf.crate.argument;

import fr.taeron.hcf.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.heavenmc.core.util.JavaUtils;
import org.heavenmc.core.util.command.CommandArgument;

import fr.taeron.hcf.crate.*;
import org.bukkit.*;
import java.util.*;
import java.util.stream.*;

public class KeyWithdrawArgument extends CommandArgument{
	
    private final HCF plugin;
    
    public KeyWithdrawArgument(final HCF plugin) {
        super("withdraw", "Récuperer des keys dans ta banque");
        this.plugin = plugin;
        this.aliases = new String[] { "retrieve" };
        this.permission = "command.loot.argument." + this.getName();
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <type> <nombre>";
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Utilisation: " + this.getUsage(label));
            return true;
        }
        final Key key = this.plugin.getKeyManager().getKey(args[1]);
        if (key == null) {
            sender.sendMessage(ChatColor.RED + "Il n'y a pas de key avec le nom '" + args[1] + "'.");
            return true;
        }
        final Integer quantity = JavaUtils.tryParseInt(args[2]);
        if (quantity == null) {
            sender.sendMessage(ChatColor.RED + "'" + args[3] + "' n'est pas un nombre.");
            return true;
        }
        if (quantity <= 0) {
            sender.sendMessage(ChatColor.RED + "La valeur doit être positive.");
            return true;
        }
        final Player player = (Player)sender;
        final UUID uuid = player.getUniqueId();
        final Map<String, Integer> crateKeyMap = this.plugin.getKeyManager().getDepositedCrateMap(uuid);
        final String keyName = key.getName();
        final int keyBalance = crateKeyMap.getOrDefault(keyName, 0);
        if (quantity > keyBalance) {
            sender.sendMessage(ChatColor.RED + "Tu ne peux pas récupérer " + quantity + ' ' + keyName + " keys car tu n'as que " + keyBalance + " dans ta banque.");
            return true;
        }
        final int newBalance = keyBalance - quantity;
        crateKeyMap.put(keyName, newBalance);
        final ItemStack stack = key.getItemStack();
        stack.setAmount((int)quantity);
        final Location location = player.getLocation();
        final World world = player.getWorld();
        for (final Map.Entry<Integer, ItemStack> entry : player.getInventory().addItem(new ItemStack[] { stack }).entrySet()) {
            world.dropItemNaturally(location, (ItemStack)entry.getValue());
        }
        sender.sendMessage(ChatColor.YELLOW + "Tu as récupéré " + quantity + ' ' + keyName + " keys dans ta banque. Il te restes désormais " + newBalance + " de ces clés.");
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length != 2) {
            return Collections.emptyList();
        }
        return this.plugin.getKeyManager().getKeys().stream().map(Key::getName).collect(Collectors.toList());
    }
}
