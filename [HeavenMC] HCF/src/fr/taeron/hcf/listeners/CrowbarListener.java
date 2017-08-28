package fr.taeron.hcf.listeners;

import org.bukkit.event.player.*;
import fr.taeron.hcf.faction.claim.*;
import fr.taeron.hcf.*;
import org.bukkit.*;
import org.bukkit.block.*;
import fr.taeron.hcf.faction.type.*;
import net.minecraft.util.org.apache.commons.lang3.text.WordUtils;
import fr.taeron.hcf.faction.*;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.entity.*;

import com.google.common.base.Optional;

import org.bukkit.inventory.meta.*;
import org.heavenmc.core.util.GuavaCompat;
import org.heavenmc.core.util.ItemBuilder;
import org.heavenmc.core.util.ParticleEffect;

import java.util.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;

public class CrowbarListener implements Listener{
	
    private final HCF plugin;
    
    public CrowbarListener(final HCF plugin) {
        this.plugin = plugin;
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.hasItem()) {
            final Optional<Crowbar> crowbarOptional = Crowbar.fromStack(event.getItem());
            if (crowbarOptional.isPresent()) {
                event.setCancelled(true);
                final Player player = event.getPlayer();
                final World world = player.getWorld();
                if (world.getEnvironment() != World.Environment.NORMAL) {
                    player.sendMessage(ChatColor.RED + "Tu ne peux utiliser ta crowbar que dans l'overworld");
                    return;
                }
                final Block block = event.getClickedBlock();
                final Location blockLocation = block.getLocation();
                if (!ProtectionListener.attemptBuild((Entity)player, blockLocation, ChatColor.YELLOW + "Tu ne peux pas utiliser de crowbar dans le territoire de %1$s" + ChatColor.YELLOW + '.')) {
                    return;
                }
                final Crowbar crowbar = (Crowbar)crowbarOptional.get();
                final BlockState blockState = block.getState();
                if (blockState instanceof CreatureSpawner) {
                    final int remainingUses = crowbar.getSpawnerUses();
                    if (remainingUses <= 0) {
                        player.sendMessage(ChatColor.RED + "Cette crowbar ne peux plus récuperer de spawner.");
                        return;
                    }
                    crowbar.setSpawnerUses(remainingUses - 1);
                    player.setItemInHand(crowbar.getItemIfPresent());
                    final CreatureSpawner spawner = (CreatureSpawner)blockState;
                    block.setType(Material.AIR);
                    blockState.update();
                    world.dropItemNaturally(blockLocation, new ItemBuilder(Material.MOB_SPAWNER).displayName(ChatColor.GREEN + "Spawner").data((short)spawner.getData().getData()).loreLine(ChatColor.WHITE + WordUtils.capitalizeFully(spawner.getSpawnedType().name())).build());
                }
                else {
                    if (block.getType() != Material.ENDER_PORTAL_FRAME) {
                        return;
                    }
                    final int remainingUses = crowbar.getEndFrameUses();
                    if (remainingUses <= 0) {
                        player.sendMessage(ChatColor.RED + "Cette crowbar ne peux plus récuperer d'end portal.");
                        return;
                    }
                    boolean destroyed = false;
                    final int blockX = blockLocation.getBlockX();
                    final int blockY = blockLocation.getBlockY();
                    final int blockZ = blockLocation.getBlockZ();
                    for (int searchRadius = 4, x = blockX - searchRadius; x <= blockX + searchRadius; ++x) {
                        for (int z = blockZ - searchRadius; z <= blockZ + searchRadius; ++z) {
                            final Block next = world.getBlockAt(x, blockY, z);
                            if (next.getType() == Material.ENDER_PORTAL) {
                                next.setType(Material.AIR);
                                next.getState().update();
                                destroyed = true;
                            }
                        }
                    }
                    if (destroyed) {
                        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
                        player.sendMessage(ChatColor.RED.toString() + ChatColor.GOLD + "Ton end portal n'est plus actif.");
                        if (playerFaction != null) {
                            boolean informFaction = false;
                            for (final Claim claim : playerFaction.getClaims()) {
                                if (claim.contains(blockLocation)) {
                                    informFaction = true;
                                    break;
                                }
                            }
                            if (informFaction) {
                                final FactionMember factionMember = playerFaction.getMember(player);
                                final String astrix = factionMember.getRole().getAstrix();
                                playerFaction.broadcast(astrix + ConfigurationService.TEAMMATE_COLOUR + " a utilisé une crowbar pour désactiver un des end portal de la faction.", player.getUniqueId());
                            }
                        }
                    }
                    crowbar.setEndFrameUses(remainingUses - 1);
                    player.setItemInHand(crowbar.getItemIfPresent());
                    block.setType(Material.AIR);
                    blockState.update();
                    world.dropItemNaturally(blockLocation, new ItemStack(Material.ENDER_PORTAL_FRAME, 1));
                }
                if (event.getItem().getType() == Material.AIR) {
                    player.playSound(blockLocation, Sound.ITEM_BREAK, 1.0f, 1.0f);
                }
                else {
                    ParticleEffect.FIREWORK_SPARK.display(player, blockLocation, 0.125f, 50);
                    player.playSound(blockLocation, Sound.LEVEL_UP, 1.0f, 1.0f);
                }
            }
        }
    }
    
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockPlace(final BlockPlaceEvent event) {
        final Block block = event.getBlockPlaced();
        final ItemStack stack = event.getItemInHand();
        final Player player = event.getPlayer();
        if (block.getState() instanceof CreatureSpawner && stack.hasItemMeta()) {
            final ItemMeta meta = stack.getItemMeta();
            if (meta.hasLore() && meta.hasDisplayName()) {
                final CreatureSpawner spawner = (CreatureSpawner)block.getState();
                final List<String> lore = (List<String>)meta.getLore();
                if (!lore.isEmpty()) {
                    final String spawnerName = ChatColor.stripColor(lore.get(0).toUpperCase());
                    final Optional<EntityType> entityTypeOptional = GuavaCompat.getIfPresent(EntityType.class, spawnerName);
                    if (entityTypeOptional.isPresent()) {
                        spawner.setSpawnedType((EntityType)entityTypeOptional.get());
                        spawner.update(true, true);
                        player.sendMessage(ChatColor.AQUA + "Tu as placé un spawner à " + ChatColor.BLUE + spawnerName);
                    }
                }
            }
        }
    }
    
    @SuppressWarnings("unused")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPrepareCrowbarCraft(final PrepareItemCraftEvent event) {
        final CraftingInventory inventory = event.getInventory();
        if (event.isRepair() && event.getRecipe().getResult().getType() == Crowbar.CROWBAR_TYPE) {
            int endFrameUses = 0;
            int spawnerUses = 0;
            boolean changed = false;
            final ItemStack[] array;
            ItemStack[] matrix2 = array = (matrix2 = inventory.getMatrix());
            for (final ItemStack ingredient : array) {
                final Optional<Crowbar> crowbarOptional = Crowbar.fromStack(ingredient);
                if (crowbarOptional.isPresent()) {
                    final Crowbar crowbar = (Crowbar)crowbarOptional.get();
                    spawnerUses += crowbar.getSpawnerUses();
                    endFrameUses += crowbar.getEndFrameUses();
                    changed = true;
                }
            }
            if (changed) {
                inventory.setResult(new Crowbar(spawnerUses, endFrameUses).getItemIfPresent());
            }
        }
    }
}
