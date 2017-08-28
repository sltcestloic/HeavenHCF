package fr.taeron.hcf.listeners;

import org.bukkit.inventory.*;
import com.google.common.collect.*;

import fr.taeron.hcf.DateTimeFormats;
import fr.taeron.hcf.HCF;

import org.bukkit.inventory.meta.*;
import org.bukkit.event.*;
import org.bukkit.block.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import org.bukkit.event.block.*;
import java.util.*;

public class EventSignListener implements Listener
{
    private static final String EVENT_SIGN_ITEM_NAME;
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static ItemStack getEventSign(final String playerName, final String kothName) {
        final ItemStack stack = new ItemStack(Material.SIGN, 1);
        final ItemMeta meta = stack.getItemMeta();
        final String name = ChatColor.AQUA + kothName;
        meta.setDisplayName(EventSignListener.EVENT_SIGN_ITEM_NAME);
        meta.setLore((List)Lists.newArrayList((Object[])new String[] { ChatColor.YELLOW.toString() + HCF.getPlugin().getFactionManager().getFaction(playerName).getName(), ChatColor.WHITE + "captured by", ChatColor.YELLOW + ChatColor.stripColor(name), DateTimeFormats.DAY_MTH_HR_MIN_SECS.format(System.currentTimeMillis()) }));
        stack.setItemMeta(meta);
        return stack;
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onSignChange(final SignChangeEvent event) {
        if (this.isEventSign(event.getBlock())) {
            event.setCancelled(true);
        }
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockBreak(final BlockBreakEvent event) {
        final Block block = event.getBlock();
        if (this.isEventSign(block)) {
            final BlockState state = block.getState();
            final Sign sign = (Sign)state;
            final ItemStack stack = new ItemStack(Material.SIGN, 1);
            final ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(EventSignListener.EVENT_SIGN_ITEM_NAME);
            meta.setLore((List)Arrays.asList(sign.getLines()));
            stack.setItemMeta(meta);
            final Player player = event.getPlayer();
            final World world = player.getWorld();
            if (player.getGameMode() != GameMode.CREATIVE && world.isGameRule("doTileDrops")) {
                world.dropItemNaturally(block.getLocation(), stack);
            }
            event.setCancelled(true);
            block.setType(Material.AIR);
            state.update();
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockPlace(final BlockPlaceEvent event) {
        final ItemStack stack = event.getItemInHand();
        final BlockState state = event.getBlock().getState();
        if (state instanceof Sign && stack.hasItemMeta()) {
            final ItemMeta meta = stack.getItemMeta();
            if (meta.hasDisplayName() && meta.getDisplayName().equals(EventSignListener.EVENT_SIGN_ITEM_NAME)) {
                final Sign sign = (Sign)state;
                final List<String> lore = (List<String>)meta.getLore();
                int count = 0;
                for (final String loreLine : lore) {
                    sign.setLine(count++, loreLine);
                    if (count == 4) {
                        break;
                    }
                }
                sign.update();
               // sign.setEditible(false);
            }
        }
    }
    
    private boolean isEventSign(final Block block) {
        final BlockState state = block.getState();
        if (state instanceof Sign) {
            final String[] lines = ((Sign)state).getLines();
            return lines.length > 0 && lines[1] != null && lines[1].equals(ChatColor.WHITE + "captured by");
        }
        return false;
    }
    
    static {
        EVENT_SIGN_ITEM_NAME = ChatColor.GOLD + "Event Sign";
    }
}
