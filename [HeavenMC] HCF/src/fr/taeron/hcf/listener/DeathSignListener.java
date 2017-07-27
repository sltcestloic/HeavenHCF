package fr.taeron.hcf.listener;

import org.bukkit.plugin.*;
import org.bukkit.inventory.*;
import fr.taeron.hcf.*;
import com.google.common.collect.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.event.*;
import org.bukkit.block.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import org.bukkit.event.block.*;
import java.util.*;
import org.bukkit.event.entity.*;

public class DeathSignListener implements Listener
{
    private static final String DEATH_SIGN_ITEM_NAME;
    
    public DeathSignListener(final HCF plugin) {
        if (!plugin.getConfig().getBoolean("death-signs", true)) {
            Bukkit.getScheduler().runTaskLater((Plugin)plugin, () -> HandlerList.unregisterAll((Listener)this), 5L);
        }
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static ItemStack getDeathSign(final String playerName, final String killerName) {
        final ItemStack stack = new ItemStack(Material.SIGN, 1);
        final ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(DeathSignListener.DEATH_SIGN_ITEM_NAME);
        meta.setLore((List)Lists.newArrayList((Object[])new String[] { ChatColor.YELLOW + playerName, ChatColor.WHITE + "tué par", ChatColor.YELLOW + killerName, DateTimeFormats.DAY_MTH_HR_MIN_SECS.format(System.currentTimeMillis()) }));
        stack.setItemMeta(meta);
        return stack;
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onSignChange(final SignChangeEvent event) {
        if (this.isDeathSign(event.getBlock())) {
            event.setCancelled(true);
        }
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockBreak(final BlockBreakEvent event) {
        final Block block = event.getBlock();
        if (this.isDeathSign(block)) {
            final BlockState state = block.getState();
            final Sign sign = (Sign)state;
            final ItemStack stack = new ItemStack(Material.SIGN, 1);
            final ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(DeathSignListener.DEATH_SIGN_ITEM_NAME);
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
            if (meta.hasDisplayName() && meta.getDisplayName().equals(DeathSignListener.DEATH_SIGN_ITEM_NAME)) {
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
                //sign.setEditible(false);
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final Player killer = player.getKiller();
        if (killer != null && (!killer.equals(player) & true)) {
            event.getDrops().add(getDeathSign(player.getName(), killer.getName()));
        }
    }
    
    private boolean isDeathSign(final Block block) {
        final BlockState state = block.getState();
        if (state instanceof Sign) {
            final String[] lines = ((Sign)state).getLines();
            return lines.length > 0 && lines[1] != null && lines[1].equals(ChatColor.WHITE + "tué par");
        }
        return false;
    }
    
    static {
        DEATH_SIGN_ITEM_NAME = ChatColor.GOLD + "Death Sign";
    }
}
