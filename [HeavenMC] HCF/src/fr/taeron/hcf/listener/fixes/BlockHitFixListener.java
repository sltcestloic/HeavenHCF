package fr.taeron.hcf.listener.fixes;

import org.bukkit.*;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.entity.*;
import org.bukkit.event.player.*;
import org.heavenmc.core.util.BukkitUtils;

import java.util.*;
import com.google.common.collect.*;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class BlockHitFixListener implements Listener{
	
    private static final ImmutableSet<Material> NON_TRANSPARENT_ATTACK_BREAK_TYPES;
    private static final ImmutableSet<Material> NON_TRANSPARENT_ATTACK_INTERACT_TYPES;
    private final Map<UUID, Long> lastInteractTimes = new HashMap<>();

    
    @EventHandler(ignoreCancelled = false, priority = EventPriority.HIGH)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (event.hasBlock() && event.getAction() != Action.PHYSICAL && BlockHitFixListener.NON_TRANSPARENT_ATTACK_INTERACT_TYPES.contains((Object)event.getClickedBlock().getType())) {
            this.cancelAttackingMillis(event.getPlayer().getUniqueId(), 850L);
        }
    }
    
    @EventHandler(ignoreCancelled = false, priority = EventPriority.MONITOR)
    public void onBlockBreak(final BlockBreakEvent event) {
        if (event.isCancelled() && BlockHitFixListener.NON_TRANSPARENT_ATTACK_BREAK_TYPES.contains((Object)event.getBlock().getType())) {
            this.cancelAttackingMillis(event.getPlayer().getUniqueId(), 850L);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(final EntityDamageEvent event) {
        final Player attacker = BukkitUtils.getFinalAttacker(event, true);
        if (attacker != null) {
            final Long lastInteractTime = (Long) this.lastInteractTimes.get(attacker.getUniqueId());
            if (lastInteractTime != null && lastInteractTime - System.currentTimeMillis() > 0L) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerLogout(final PlayerQuitEvent event) {
        this.lastInteractTimes.remove(event.getPlayer().getUniqueId());
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerKick(final PlayerKickEvent event) {
        this.lastInteractTimes.remove(event.getPlayer().getUniqueId());
    }
    
    public void cancelAttackingMillis(final UUID uuid, final long delay) {
        this.lastInteractTimes.put(uuid, System.currentTimeMillis() + delay);
    }
    
    static {
        NON_TRANSPARENT_ATTACK_BREAK_TYPES = Sets.immutableEnumSet((Enum)Material.GLASS, (Enum[])new Material[] { Material.STAINED_GLASS, Material.STAINED_GLASS_PANE });
        NON_TRANSPARENT_ATTACK_INTERACT_TYPES = Sets.immutableEnumSet((Enum)Material.IRON_DOOR_BLOCK, (Enum[])new Material[] { Material.IRON_DOOR, Material.WOODEN_DOOR, Material.WOOD_DOOR, Material.TRAP_DOOR, Material.FENCE_GATE });
    }
}
