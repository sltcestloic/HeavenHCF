package fr.taeron.hcf.economy;

import java.util.regex.*;
import fr.taeron.hcf.*;
import org.bukkit.event.player.*;
import org.bukkit.event.block.*;
import org.bukkit.inventory.*;
import org.heavenmc.core.Core;
import org.heavenmc.core.util.InventoryUtils;
import org.heavenmc.core.util.ItemBuilder;
import org.heavenmc.core.util.JavaUtils;
import org.bukkit.block.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import java.util.*;
import org.bukkit.event.*;

public class ShopSignListener implements Listener
{
    private static final Pattern ALPHANUMERIC_REMOVER;
    private final HCF plugin;
    
    public ShopSignListener(final HCF plugin) {
        this.plugin = plugin;
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = false, priority = EventPriority.HIGH)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            final Block block = event.getClickedBlock();
            final BlockState state = block.getState();
            if (state instanceof Sign) {
                final Sign sign = (Sign)state;
                final String[] lines = sign.getLines();
                final Integer quantity = JavaUtils.tryParseInt(lines[2]);
                if (quantity == null) {
                    return;
                }
                final Integer price = JavaUtils.tryParseInt(ShopSignListener.ALPHANUMERIC_REMOVER.matcher(lines[3]).replaceAll(""));
                if (price == null) {
                    return;
                }
                ItemStack stack;
                if (lines[1].equalsIgnoreCase("Crowbar")) {
                    stack = new Crowbar().getItemIfPresent();
                } else {
                	stack = new ItemBuilder(Material.getMaterial(lines[1].toUpperCase().replace(" ", "_")), quantity).build();
                }
                final Player player = event.getPlayer();
                final String[] fakeLines = Arrays.copyOf(sign.getLines(), 4);
                if ((lines[0].contains("Sell") && lines[0].contains(ChatColor.RED.toString())) || lines[0].contains(ChatColor.AQUA.toString())) {
                    final int sellQuantity = Math.min(quantity, InventoryUtils.countAmount((Inventory)player.getInventory(), stack.getType(), stack.getDurability()));
                    if (sellQuantity <= 0) {
                        fakeLines[0] = ChatColor.RED + "Tu n'as aucun";
                        fakeLines[2] = ChatColor.RED + "sur toi.";
                        fakeLines[3] = "";
                    }
                    else {
                        final int newPrice = price / quantity * sellQuantity;
                        fakeLines[0] = ChatColor.GREEN + "Tu as vendu " + sellQuantity;
                        fakeLines[2] = ChatColor.GREEN + "pour " + '$' + newPrice;
                        this.plugin.getEconomyManager().addBalance(player.getUniqueId(), newPrice);
                        InventoryUtils.removeItem((Inventory)player.getInventory(), stack.getType(), (short)stack.getData().getData(), sellQuantity);
                        player.updateInventory();
                    }
                }
                else {
                    if (!lines[0].contains("Buy") || !lines[0].contains(ChatColor.GREEN.toString())) {
                        return;
                    }
                    if (price > this.plugin.getEconomyManager().getBalance(player.getUniqueId())) {
                        fakeLines[0] = ChatColor.RED + "Trop cher !";
                    }
                    else {
                        fakeLines[0] = ChatColor.GREEN + "Item acheté:";
                        fakeLines[2] = ChatColor.GREEN + "pour " + '$' + price;
                        this.plugin.getEconomyManager().subtractBalance(player.getUniqueId(), price);
                        final World world = player.getWorld();
                        final Location location = player.getLocation();
                        final Map<Integer, ItemStack> excess = (Map<Integer, ItemStack>)player.getInventory().addItem(new ItemStack[] { stack });
                        for (final Map.Entry<Integer, ItemStack> excessItemStack : excess.entrySet()) {
                            world.dropItemNaturally(location, (ItemStack)excessItemStack.getValue());
                        }
                        player.setItemInHand(player.getItemInHand());
                        player.updateInventory();
                    }
                }
                event.setCancelled(true);
                Core.getPlugin().getSignHandler().showLines(player, sign, fakeLines, 100L, true);
            }
        }
    }
    
    static {
        ALPHANUMERIC_REMOVER = Pattern.compile("[^A-Za-z0-9]");
    }
}
