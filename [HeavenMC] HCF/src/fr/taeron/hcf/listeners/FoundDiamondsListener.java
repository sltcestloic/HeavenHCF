package fr.taeron.hcf.listeners;

import fr.taeron.hcf.*;
import org.bukkit.block.*;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.entity.*;
import org.bukkit.command.*;
import org.bukkit.*;
import java.util.*;

public class FoundDiamondsListener implements Listener{
	
    public static final Material SEARCH_TYPE;
    public final Set<String> foundLocations;
    private final HCF plugin;
    
    public FoundDiamondsListener(final HCF plugin) {
        this.foundLocations = new HashSet<String>();
        this.plugin = plugin;
        this.foundLocations.addAll(plugin.getConfig().getStringList("registered-diamonds"));
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPistonExtend(final BlockPistonExtendEvent event) {
        for (final Block block : event.getBlocks()) {
            if (block.getType() == FoundDiamondsListener.SEARCH_TYPE) {
                this.foundLocations.add(block.getLocation().toString());
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockPlace(final BlockPlaceEvent event) {
        final Block block = event.getBlock();
        if (block.getType() == FoundDiamondsListener.SEARCH_TYPE) {
            this.foundLocations.add(block.getLocation().toString());
        }
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(final BlockBreakEvent event) {
        final Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        final Block block = event.getBlock();
        final Location blockLocation = block.getLocation();
        if (block.getType() == FoundDiamondsListener.SEARCH_TYPE && this.foundLocations.add(blockLocation.toString())) {
            int count = 1;
            for (int x = -5; x < 5; ++x) {
                for (int y = -5; y < 5; ++y) {
                    for (int z = -5; z < 5; ++z) {
                        final Block otherBlock = blockLocation.clone().add((double)x, (double)y, (double)z).getBlock();
                        if (!otherBlock.equals(block) && otherBlock.getType() == FoundDiamondsListener.SEARCH_TYPE && this.foundLocations.add(otherBlock.getLocation().toString())) {
                            ++count;
                        }
                    }
                }
            }
            this.plugin.getUserManager().getUser(player.getUniqueId()).setDiamondsMined(this.plugin.getUserManager().getUser(player.getUniqueId()).getDiamondsMined() + count);
            for (final Player on : Bukkit.getOnlinePlayers()) {
                final String message = this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId()).getRelation((CommandSender)on).toChatColour() + player.getName() + ChatColor.GRAY + " a trouvé " + ChatColor.AQUA + count + (count > 1 ? "diamants" : "diamant");
                on.sendMessage(message);
            }
        }
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public void saveConfig() {
        this.plugin.getConfig().set("registered-diamonds", new ArrayList(this.foundLocations));
        this.plugin.saveConfig();
    }
    
    static {
        SEARCH_TYPE = Material.DIAMOND_ORE;
    }
}
