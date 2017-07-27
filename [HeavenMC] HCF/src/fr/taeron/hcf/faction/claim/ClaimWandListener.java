package fr.taeron.hcf.faction.claim;

import fr.taeron.hcf.*;
import org.bukkit.inventory.*;
import com.google.common.base.*;
import fr.taeron.hcf.visualise.*;
import org.bukkit.*;
import org.bukkit.scheduler.*;
import org.bukkit.plugin.*;
import java.util.*;
import fr.taeron.hcf.faction.type.*;
import org.bukkit.block.*;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.player.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.entity.*;

public class ClaimWandListener implements Listener
{
    private final HCF plugin;
    
    public ClaimWandListener(final HCF plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(ignoreCancelled = false, priority = EventPriority.HIGH)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final Action action = event.getAction();
        if (action == Action.PHYSICAL || !event.hasItem() || !this.isClaimingWand(event.getItem())) {
            return;
        }
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();
        if (action == Action.RIGHT_CLICK_AIR) {
            this.plugin.getClaimHandler().clearClaimSelection(player);
            player.setItemInHand(new ItemStack(Material.AIR, 1));
            player.sendMessage(ChatColor.RED + "Tu as effacé ta sélection.");
            return;
        }
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(uuid);
        if ((action != Action.LEFT_CLICK_AIR && action != Action.LEFT_CLICK_BLOCK) || !player.isSneaking()) {
            if (action == Action.LEFT_CLICK_BLOCK || action == Action.RIGHT_CLICK_BLOCK) {
                final Block block = event.getClickedBlock();
                final Location blockLocation = block.getLocation();
                if (action == Action.RIGHT_CLICK_BLOCK) {
                    event.setCancelled(true);
                }
                if (this.plugin.getClaimHandler().canClaimHere(player, blockLocation)) {
                    final ClaimSelection revert;
                    ClaimSelection claimSelection = (ClaimSelection) this.plugin.getClaimHandler().claimSelectionMap.putIfAbsent(uuid, revert = new ClaimSelection(blockLocation.getWorld()));
                    if (claimSelection == null) {
                        claimSelection = revert;
                    }
                    Location oldPosition = null;
                    Location opposite = null;
                    int selectionId = 0;
                    switch (action) {
                        case LEFT_CLICK_BLOCK: {
                            oldPosition = claimSelection.getPos1();
                            opposite = claimSelection.getPos2();
                            selectionId = 1;
                            break;
                        }
                        case RIGHT_CLICK_BLOCK: {
                            oldPosition = claimSelection.getPos2();
                            opposite = claimSelection.getPos1();
                            selectionId = 2;
                            break;
                        }
                        default: {
                            return;
                        }
                    }
                    final int blockX = blockLocation.getBlockX();
                    final int blockZ = blockLocation.getBlockZ();
                    if (oldPosition != null && blockX == oldPosition.getBlockX() && blockZ == oldPosition.getBlockZ()) {
                        return;
                    }
                    if (System.currentTimeMillis() - claimSelection.getLastUpdateMillis() <= 200L) {
                        return;
                    }
                    if (opposite != null) {
                        final int xDiff = Math.abs(opposite.getBlockX() - blockX) + 1;
                        final int zDiff = Math.abs(opposite.getBlockZ() - blockZ) + 1;
                        if (xDiff < 5 || zDiff < 5) {
                            player.sendMessage(ChatColor.RED + "La selection doit faire au moins une taille de " + 5 + 'x' + 5 + " blocs.");
                            return;
                        }
                    }
                    if (oldPosition != null) {
                        final Location finalOldPosition = oldPosition;
                        this.plugin.getVisualiseHandler().clearVisualBlocks(player, VisualType.CREATE_CLAIM_SELECTION, (Predicate<VisualBlock>)new Predicate<VisualBlock>() {
                            public boolean apply(final VisualBlock visualBlock) {
                                final Location location = visualBlock.getLocation();
                                return location.getBlockX() == finalOldPosition.getBlockX() && location.getBlockZ() == finalOldPosition.getBlockZ();
                            }
                        });
                    }
                    if (selectionId == 1) {
                        claimSelection.setPos1(blockLocation);
                    }
                    if (selectionId == 2) {
                        claimSelection.setPos2(blockLocation);
                    }
                    player.sendMessage(ChatColor.GREEN + "Tu as défini la position " + ChatColor.YELLOW + selectionId + ChatColor.GREEN + " en: " + ChatColor.GOLD + '(' + ChatColor.YELLOW + blockX + ", " + blockZ + ChatColor.GOLD + ')');
                    if (claimSelection.hasBothPositionsSet()) {
                        final Claim claim = claimSelection.toClaim(playerFaction);
                        final int selectionPrice = claimSelection.getPrice(playerFaction, false);
                        player.sendMessage(ChatColor.AQUA + "Prix du claim: " + ((selectionPrice > playerFaction.getBalance()) ? ChatColor.RED : ChatColor.GREEN) + '$' + selectionPrice + ChatColor.AQUA + ". Taille: (" + ChatColor.WHITE + claim.getWidth() + ", " + claim.getLength() + ChatColor.AQUA + "), " + ChatColor.WHITE + claim.getArea() + ChatColor.AQUA + " blocs.");
                    }
                    final int blockY = block.getY();
                    final int maxHeight = player.getWorld().getMaxHeight();
                    final List<Location> locations = new ArrayList<Location>(maxHeight);
                    for (int i = blockY; i < maxHeight; ++i) {
                        final Location other = blockLocation.clone();
                        other.setY(i);
                        locations.add(other);
                    }
                    new BukkitRunnable() {
                        public void run() {
                            ClaimWandListener.this.plugin.getVisualiseHandler().generate(player, locations, VisualType.CREATE_CLAIM_SELECTION, true);
                        }
                    }.runTask((Plugin)this.plugin);
                }
            }
            return;
        }
        final ClaimSelection claimSelection2 = (ClaimSelection) this.plugin.getClaimHandler().claimSelectionMap.get(uuid);
        if (claimSelection2 == null || !claimSelection2.hasBothPositionsSet()) {
            player.sendMessage(ChatColor.RED + "Tu n'as pas défini les deux positions de ta sélection.");
            return;
        }
        if (this.plugin.getClaimHandler().tryPurchasing(player, claimSelection2.toClaim(playerFaction))) {
            this.plugin.getClaimHandler().clearClaimSelection(player);
            player.setItemInHand(new ItemStack(Material.AIR, 1));
        }
    }
    
    @EventHandler(ignoreCancelled = false, priority = EventPriority.NORMAL)
    public void onBlockBreak(final BlockBreakEvent event) {
        if (this.isClaimingWand(event.getPlayer().getItemInHand())) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = false, priority = EventPriority.NORMAL)
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            final Player player = (Player)event.getDamager();
            if (this.isClaimingWand(player.getItemInHand())) {
                player.setItemInHand(new ItemStack(Material.AIR, 1));
                this.plugin.getClaimHandler().clearClaimSelection(player);
            }
        }
    }
    
    @EventHandler(ignoreCancelled = false, priority = EventPriority.NORMAL)
    public void onPlayerKick(final PlayerKickEvent event) {
        event.getPlayer().getInventory().remove(ClaimHandler.CLAIM_WAND);
    }
    
    @EventHandler(ignoreCancelled = false, priority = EventPriority.NORMAL)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        event.getPlayer().getInventory().remove(ClaimHandler.CLAIM_WAND);
    }
    
    @EventHandler(ignoreCancelled = false, priority = EventPriority.NORMAL)
    public void onPlayerDrop(final PlayerDropItemEvent event) {
        final Item item = event.getItemDrop();
        if (this.isClaimingWand(item.getItemStack())) {
            item.remove();
            this.plugin.getClaimHandler().clearClaimSelection(event.getPlayer());
        }
    }
    
    @EventHandler(ignoreCancelled = false, priority = EventPriority.NORMAL)
    public void onPlayerPickup(final PlayerPickupItemEvent event) {
        final Item item = event.getItem();
        if (this.isClaimingWand(item.getItemStack())) {
            item.remove();
            this.plugin.getClaimHandler().clearClaimSelection(event.getPlayer());
        }
    }
    
    @EventHandler(ignoreCancelled = false, priority = EventPriority.NORMAL)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        if (event.getDrops().remove(ClaimHandler.CLAIM_WAND)) {
            this.plugin.getClaimHandler().clearClaimSelection(event.getEntity());
        }
    }
    
    @EventHandler(ignoreCancelled = false, priority = EventPriority.NORMAL)
    public void onInventoryOpen(final InventoryOpenEvent event) {
        final HumanEntity humanEntity = event.getPlayer();
        if (humanEntity instanceof Player) {
            final Player player = (Player)humanEntity;
            player.getInventory().remove(ClaimHandler.CLAIM_WAND);
            this.plugin.getClaimHandler().clearClaimSelection(player);
        }
    }
    
    public boolean isClaimingWand(final ItemStack stack) {
        return stack != null && stack.isSimilar(ClaimHandler.CLAIM_WAND);
    }
}
