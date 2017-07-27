package fr.taeron.hcf.timer.type;

import org.bukkit.plugin.java.*;
import java.util.concurrent.*;
import fr.taeron.hcf.timer.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.event.entity.*;
import fr.taeron.hcf.*;
import org.bukkit.projectiles.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.craftbukkit.v1_7_R4.inventory.*;
import java.util.*;
import org.bukkit.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.scheduler.*;
import org.heavenmc.core.util.Config;
import org.heavenmc.core.util.NmsUtils;
import org.bukkit.plugin.*;
import org.bukkit.craftbukkit.v1_7_R4.entity.*;
import net.minecraft.server.v1_7_R4.*;
import net.minecraft.server.v1_7_R4.PlayerInventory;

public class EnderPearlTimer extends PlayerTimer implements Listener{
	
	private final Map<UUID, PearlNameFaker> itemNameFakes = new HashMap<>();
    private final JavaPlugin plugin;
    
    public EnderPearlTimer(final JavaPlugin plugin) {
        super("Enderpearl", TimeUnit.SECONDS.toMillis(16L));
        this.plugin = plugin;
    }
    
    public String getScoreboardPrefix() {
        return ChatColor.YELLOW.toString() + ChatColor.BOLD;
    }
    
    @Override
    public void load(final Config config) {
        super.load(config);
        final Collection<UUID> cooldowned = this.cooldowns.keySet();
        for (final UUID uuid : cooldowned) {
            final Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                continue;
            }
            this.startDisplaying(player);
        }
    }
    
    @Override
    public void onExpire(final UUID userUUID) {
        super.onExpire(userUUID);
        final Player player = Bukkit.getPlayer(userUUID);
        if (player != null) {
            player.sendMessage(ChatColor.GREEN + "Tu peux de nouveau utiliser une enderpearl");
        }
    }
    
    @Override
    public TimerRunnable clearCooldown(final UUID playerUUID) {
        final TimerRunnable runnable = super.clearCooldown(playerUUID);
        if (runnable != null) {
            this.itemNameFakes.remove(playerUUID);
            return runnable;
        }
        return null;
    }
    
    @Override
    public void clearCooldown(final Player player) {
        this.stopDisplaying(player);
        super.clearCooldown(player);
    }
    
    public void refund(final Player player) {
        player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.ENDER_PEARL, 1) });
        this.clearCooldown(player);
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onProjectileLaunch(final ProjectileLaunchEvent event) {
        final Projectile projectile = event.getEntity();
        if (projectile instanceof EnderPearl) {
            final EnderPearl enderPearl = (EnderPearl)projectile;
            final ProjectileSource source = enderPearl.getShooter();
            if (source instanceof Player) {
                final Player shooter = (Player)source;
                final long remaining = this.getRemaining(shooter);
                if (remaining > 0L) {
                    shooter.sendMessage(ChatColor.RED + "Tu es sous cooldown pendant §c§l" + HCF.getRemaining(remaining, true, false) + ChatColor.RED + '.');
                    event.setCancelled(true);
                    shooter.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
                    return;
                }
                if (this.setCooldown(shooter, shooter.getUniqueId(), this.defaultCooldown, true)) {
                    this.startDisplaying(shooter);
                }
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        this.clearCooldown(event.getPlayer());
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerKick(final PlayerKickEvent event) {
        this.clearCooldown(event.getPlayer());
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerItemHeld(final PlayerItemHeldEvent event) {
        final Player player = event.getPlayer();
        final PearlNameFaker pearlNameFaker = (PearlNameFaker) this.itemNameFakes.get(player.getUniqueId());
        if (pearlNameFaker != null) {
            final int previousSlot = event.getPreviousSlot();
            final ItemStack item = player.getInventory().getItem(previousSlot);
            if (item == null) {
                return;
            }
            pearlNameFaker.setFakeItem(CraftItemStack.asNMSCopy(item), previousSlot);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onInventoryDrag(final InventoryDragEvent event) {
        final HumanEntity humanEntity = event.getWhoClicked();
        if (humanEntity instanceof Player) {
            final Player player = (Player)humanEntity;
            final PearlNameFaker pearlNameFaker = (PearlNameFaker) this.itemNameFakes.get(player.getUniqueId());
            if (pearlNameFaker == null) {
                return;
            }
            for (final Map.Entry<Integer, ItemStack> entry : event.getNewItems().entrySet()) {
                if (entry.getKey() == player.getInventory().getHeldItemSlot()) {
                    pearlNameFaker.setFakeItem(CraftItemStack.asNMSCopy(player.getItemInHand()), player.getInventory().getHeldItemSlot());
                    break;
                }
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onInventoryClick(final InventoryClickEvent event) {
        final HumanEntity humanEntity = event.getWhoClicked();
        if (humanEntity instanceof Player) {
            final Player player = (Player)humanEntity;
            final PearlNameFaker pearlNameFaker = (PearlNameFaker) this.itemNameFakes.get(player.getUniqueId());
            if (pearlNameFaker == null) {
                return;
            }
            final int heldSlot = player.getInventory().getHeldItemSlot();
            if (event.getSlot() == heldSlot) {
                pearlNameFaker.setFakeItem(CraftItemStack.asNMSCopy(player.getItemInHand()), heldSlot);
            }
            else if (event.getHotbarButton() == heldSlot) {
                pearlNameFaker.setFakeItem(CraftItemStack.asNMSCopy(event.getCurrentItem()), event.getSlot());
                new BukkitRunnable() {
                    public void run() {
                        player.updateInventory();
                    }
                }.runTask((Plugin)this.plugin);
            }
        }
    }
    
    public void startDisplaying(final Player player) {
        final PearlNameFaker pearlNameFaker;
        if (this.getRemaining(player) > 0L && this.itemNameFakes.putIfAbsent(player.getUniqueId(), pearlNameFaker = new PearlNameFaker(this, player)) == null) {
            final long ticks = (((CraftPlayer)player).getHandle().playerConnection.networkManager.getVersion() >= 47) ? 20L : 2L;
            pearlNameFaker.runTaskTimerAsynchronously((Plugin)this.plugin, ticks, ticks);
        }
    }
    
    public void stopDisplaying(final Player player) {
        final PearlNameFaker pearlNameFaker = (PearlNameFaker) this.itemNameFakes.remove(player.getUniqueId());
        if (pearlNameFaker != null) {
            pearlNameFaker.cancel();
        }
    }
    
    public static class PearlNameFaker extends BukkitRunnable
    {
        private final PlayerTimer timer;
        private final Player player;
        
        public PearlNameFaker(final PlayerTimer timer, final Player player) {
            this.timer = timer;
            this.player = player;
        }
        
        public void run() {
            final ItemStack stack = this.player.getItemInHand();
            if (stack != null && stack.getType() == Material.ENDER_PEARL) {
                final long remaining = this.timer.getRemaining(this.player);
                net.minecraft.server.v1_7_R4.ItemStack item = NmsUtils.getDirectNmsItemstack(stack);
                if (remaining > 0L) {
                    item = item.cloneItemStack();
                    item.c(ChatColor.YELLOW + "Enderpearl Cooldown: " + ChatColor.RED + HCF.getRemaining(remaining, true, true));
                    this.setFakeItem(item, this.player.getInventory().getHeldItemSlot());
                }
                else {
                    this.cancel();
                }
            }
        }
        
        public synchronized void cancel() throws IllegalStateException {
            super.cancel();
            this.setFakeItem(CraftItemStack.asNMSCopy(this.player.getItemInHand()), this.player.getInventory().getHeldItemSlot());
        }
        
        public void setFakeItem(final net.minecraft.server.v1_7_R4.ItemStack nms, int index) {
            final EntityPlayer entityPlayer = ((CraftPlayer)this.player).getHandle();
            if (index < PlayerInventory.getHotbarSize()) {
                index += 36;
            }
            else if (index > 35) {
                index = 8 - (index - 36);
            }
            entityPlayer.playerConnection.sendPacket((Packet)new PacketPlayOutSetSlot(entityPlayer.activeContainer.windowId, index, nms));
        }
    }
}
