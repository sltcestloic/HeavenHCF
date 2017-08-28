package fr.taeron.hcf.listeners;

import com.google.common.collect.*;
import org.bukkit.command.*;

import fr.taeron.hcf.events.*;
import fr.taeron.hcf.events.factions.*;
import fr.taeron.hcf.faction.event.*;

import java.util.*;
import org.bukkit.event.*;
import org.bukkit.block.*;
import org.bukkit.*;
import org.bukkit.World.Environment;

import fr.taeron.hcf.*;
import fr.taeron.hcf.faction.struct.*;
import org.bukkit.event.vehicle.*;
import org.bukkit.projectiles.*;
import org.heavenmc.core.util.BukkitUtils;
import org.heavenmc.core.util.cuboid.Cuboid;
import org.bukkit.material.*;
import fr.taeron.hcf.faction.type.*;
import org.bukkit.event.block.*;
import org.bukkit.event.hanging.*;
import org.bukkit.event.entity.*;
import org.bukkit.entity.*;
import org.bukkit.event.player.*;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class ProtectionListener implements Listener{
	
    public static final String PROTECTION_BYPASS_PERMISSION = "faction.protection.bypass";
    private static final ImmutableMultimap<Object, Object> ITEM_BLOCK_INTERACTABLES;
    private static final ImmutableSet<Material> BLOCK_INTERACTABLES;
    private final HCF plugin;
    private static /* synthetic */ int[] $SWITCH_TABLE$org$bukkit$event$block$BlockIgniteEvent$IgniteCause;
    private static /* synthetic */ int[] $SWITCH_TABLE$org$bukkit$event$entity$EntityTargetEvent$TargetReason;
    
    static {
        ITEM_BLOCK_INTERACTABLES = ImmutableMultimap.builder().put((Object)Material.DIAMOND_HOE, (Object)Material.GRASS).put((Object)Material.GOLD_HOE, (Object)Material.GRASS).put((Object)Material.IRON_HOE, (Object)Material.GRASS).put((Object)Material.STONE_HOE, (Object)Material.GRASS).put((Object)Material.WOOD_HOE, (Object)Material.GRASS).build();
        BLOCK_INTERACTABLES = Sets.immutableEnumSet((Enum)Material.BED, (Enum[])new Material[] { Material.BED_BLOCK, Material.BEACON, Material.FENCE_GATE, Material.IRON_DOOR, Material.TRAP_DOOR, Material.WOOD_DOOR, Material.WOODEN_DOOR, Material.IRON_DOOR_BLOCK, Material.CHEST, Material.TRAPPED_CHEST, Material.FURNACE, Material.BURNING_FURNACE, Material.BREWING_STAND, Material.HOPPER, Material.DROPPER, Material.DISPENSER, Material.STONE_BUTTON, Material.WOOD_BUTTON, Material.ENCHANTMENT_TABLE, Material.WORKBENCH, Material.ANVIL, Material.LEVER, Material.FIRE });
    }
    
    public ProtectionListener(final HCF plugin) {
        this.plugin = plugin;
    }
    
    public static boolean attemptBuild(final Entity entity, final Location location, final String denyMessage) {
        return attemptBuild(entity, location, denyMessage, false);
    }
    
    @SuppressWarnings("deprecation")
	public static boolean attemptBuild(final Entity entity, final Location location, final String denyMessage, final boolean isInteraction) {
        boolean result = false;
        if (entity instanceof Player) {
            final Player player = (Player)entity;
            if (player != null && player.getGameMode() == GameMode.CREATIVE && player.hasPermission("build")) {
                return true;
            }
            if (player != null && player.getWorld().getEnvironment() == World.Environment.THE_END) {
                player.sendMessage(ChatColor.RED + "Tu ne peux pas construire dans l'end.");
                return false;
            }
            final Faction factionAt = HCF.getPlugin().getFactionManager().getFactionAt(location);
            if (!(factionAt instanceof ClaimableFaction)) {
                result = true;
            }
            else if (factionAt instanceof Raidable && ((Raidable)factionAt).isRaidable()) {
                result = true;
            }
            if (player != null && factionAt instanceof PlayerFaction) {
                final PlayerFaction playerFaction = HCF.getPlugin().getFactionManager().getPlayerFaction(player);
                if (playerFaction != null && playerFaction.equals(factionAt)) {
                    result = true;
                }
            }
            if(factionAt.getName().equalsIgnoreCase("Warzone")){
            	result = false;
            	if (denyMessage != null && player != null && HCF.getPlugin().getFactionManager().getPlayerFaction(player) == null){
            		if(!(factionAt instanceof PlayerFaction)){
            			player.sendMessage(String.format(denyMessage, factionAt.getDisplayName((CommandSender)player)));
            		} else {
            			PlayerFaction f = (PlayerFaction) factionAt;
            			if(!f.isRaidable()){
            				player.sendMessage(String.format(denyMessage, factionAt.getDisplayName((CommandSender)player)));
            			}
            		}
            	}
            } 
            if(HCF.getPlugin().getFactionManager().getPlayerFaction(player.getUniqueId()) == null){
            
            }
            else if (denyMessage != null && player != null && !factionAt.getName().equalsIgnoreCase("The Wilderness") && !factionAt.getName().equalsIgnoreCase(HCF.getPlugin().getFactionManager().getPlayerFaction(player.getUniqueId()).getName())) {
                player.sendMessage(String.format(denyMessage, factionAt.getDisplayName((CommandSender)player)));
            }
        }
        return result;
    }
    
    public static boolean canBuildAt(final Location from, final Location to) {
        final Faction toFactionAt = HCF.getPlugin().getFactionManager().getFactionAt(to);
        if (toFactionAt instanceof Raidable && !((Raidable)toFactionAt).isRaidable()) {
            final Faction fromFactionAt = HCF.getPlugin().getFactionManager().getFactionAt(from);
            if (!toFactionAt.equals(fromFactionAt)) {
                return false;
            }
        }
        return true;
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void blockSpawnerInNether(BlockBreakEvent e){
    	if(e.getBlock().getWorld().getEnvironment() == Environment.NETHER && e.getBlock().getType() == Material.MOB_SPAWNER){    		
    		e.getPlayer().sendMessage("§cTu ne peux pas casser de spawners dans le nether.");
    		e.setCancelled(true);
    	}
    }
    

    
    
    private void handleMove(final PlayerMoveEvent event, final PlayerClaimEnterEvent.EnterCause enterCause) {
        final Location from = event.getFrom();
        final Location to = event.getTo();
        if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ()) {
            return;
        }
        final Player player = event.getPlayer();
        boolean cancelled = false;
        final Faction fromFaction = this.plugin.getFactionManager().getFactionAt(from);
        final Faction toFaction = this.plugin.getFactionManager().getFactionAt(to);
        if (!Objects.equals(fromFaction, toFaction)) {
            final PlayerClaimEnterEvent calledEvent = new PlayerClaimEnterEvent(player, from, to, fromFaction, toFaction, enterCause);
            Bukkit.getPluginManager().callEvent((Event)calledEvent);
            cancelled = calledEvent.isCancelled();
        }
        else if (toFaction instanceof CapturableFaction) {
            final CapturableFaction capturableFaction = (CapturableFaction)toFaction;
            for (final CaptureZone captureZone : capturableFaction.getCaptureZones()) {
                final Cuboid cuboid = captureZone.getCuboid();
                if (cuboid != null) {
                    final boolean containsFrom = cuboid.contains(from);
                    final boolean containsTo = cuboid.contains(to);
                    if (containsFrom && !containsTo) {
                        final CaptureZoneLeaveEvent calledEvent2 = new CaptureZoneLeaveEvent(player, capturableFaction, captureZone);
                        Bukkit.getPluginManager().callEvent((Event)calledEvent2);
                        cancelled = calledEvent2.isCancelled();
                        break;
                    }
                    if (!containsFrom && containsTo) {
                        final CaptureZoneEnterEvent calledEvent3 = new CaptureZoneEnterEvent(player, capturableFaction, captureZone);
                        Bukkit.getPluginManager().callEvent((Event)calledEvent3);
                        cancelled = calledEvent3.isCancelled();
                        break;
                    }
                    continue;
                }
            }
        }
        if (cancelled) {
            if (enterCause == PlayerClaimEnterEvent.EnterCause.TELEPORT) {
                event.setCancelled(true);
            }
            else {
                from.add(0.5, 0.0, 0.5);
                event.setTo(from);
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerMove(final PlayerMoveEvent event) {
        this.handleMove(event, PlayerClaimEnterEvent.EnterCause.MOVEMENT);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerMove(final PlayerTeleportEvent event) {
        this.handleMove((PlayerMoveEvent)event, PlayerClaimEnterEvent.EnterCause.TELEPORT);
    }
    
    @SuppressWarnings("unused")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockIgnite(final BlockIgniteEvent event) {
        final int n = $SWITCH_TABLE$org$bukkit$event$block$BlockIgniteEvent$IgniteCause()[event.getCause().ordinal()];
        final Faction factionAt = this.plugin.getFactionManager().getFactionAt(event.getBlock().getLocation());
        if (factionAt instanceof ClaimableFaction && !(factionAt instanceof PlayerFaction)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onStickyPistonExtend(final BlockPistonExtendEvent event) {
        final Block block = event.getBlock();
        final Block targetBlock = block.getRelative(event.getDirection(), event.getLength() + 1);
        if (targetBlock.isEmpty() || targetBlock.isLiquid()) {
            final Faction targetFaction = this.plugin.getFactionManager().getFactionAt(targetBlock.getLocation());
            if (targetFaction instanceof Raidable && !((Raidable)targetFaction).isRaidable() && !targetFaction.equals(this.plugin.getFactionManager().getFactionAt(block))) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onStickyPistonRetract(final BlockPistonRetractEvent event) {
        if (!event.isSticky()) {
            return;
        }
        final Location retractLocation = event.getRetractLocation();
        final Block retractBlock = retractLocation.getBlock();
        if (!retractBlock.isEmpty() && !retractBlock.isLiquid()) {
            final Block block = event.getBlock();
            final Faction targetFaction = this.plugin.getFactionManager().getFactionAt(retractLocation);
            if (targetFaction instanceof Raidable && !((Raidable)targetFaction).isRaidable() && !targetFaction.equals(this.plugin.getFactionManager().getFactionAt(block))) {
                event.setCancelled(true);
            }
        }
    }
    
    /*@EventHandler
    public void onBreak(BlockBreakEvent e){
    	if(e.getPlayer().getGameMode().equals(GameMode.SURVIVAL)){
    		e.setCancelled(true);
    	}
    }
    
    @EventHandler
    public void onPlace(BlockPlaceEvent e){
    	if(e.getPlayer().getGameMode().equals(GameMode.SURVIVAL)){
    		e.setCancelled(true);
    	}
    }*/
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockFromTo(final BlockFromToEvent event) {
        final Block toBlock = event.getToBlock();
        final Block fromBlock = event.getBlock();
        final Material fromType = fromBlock.getType();
        final Material toType = toBlock.getType();
        if ((toType == Material.REDSTONE_WIRE || toType == Material.TRIPWIRE) && (fromType == Material.AIR || fromType == Material.STATIONARY_LAVA || fromType == Material.LAVA)) {
            toBlock.setType(Material.AIR);
        }
        if ((toBlock.getType() == Material.WATER || toBlock.getType() == Material.STATIONARY_WATER || toBlock.getType() == Material.LAVA || toBlock.getType() == Material.STATIONARY_LAVA) && !canBuildAt(fromBlock.getLocation(), toBlock.getLocation())) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            final Faction toFactionAt = this.plugin.getFactionManager().getFactionAt(event.getTo());
            if (toFactionAt.isSafezone() && !this.plugin.getFactionManager().getFactionAt(event.getFrom()).isSafezone()) {
                final Player player = event.getPlayer();
                player.sendMessage(ChatColor.RED + "Tu ne peux pas envoyer d'enderpearl dans une safe-zone, ton enderpearl t'a été rendue.");
                this.plugin.getTimerManager().enderPearlTimer.refund(player);
                event.setCancelled(true);
            }
        }
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerPortal(final PlayerPortalEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            final Location from = event.getFrom();
            final Location to = event.getTo();
            final Player player = event.getPlayer();
            final Faction fromFac = this.plugin.getFactionManager().getFactionAt(from);
            if (fromFac.isSafezone()) {
                event.setTo(to.getWorld().getSpawnLocation().add(0.5, 0.0, 0.5));
                event.useTravelAgent(false);
                player.sendMessage(ChatColor.YELLOW + "Tu as été téléporté au spawn car tu as pris un portail dans une zone protégée.");
                return;
            }
            if (event.useTravelAgent() && to.getWorld().getEnvironment() == World.Environment.NORMAL) {
                final TravelAgent travelAgent = event.getPortalTravelAgent();
                if (!travelAgent.getCanCreatePortal()) {
                    return;
                }
                final Location foundPortal = travelAgent.findPortal(to);
                if (foundPortal != null) {
                    return;
                }
                final Faction factionAt = this.plugin.getFactionManager().getFactionAt(to);
                if (factionAt instanceof ClaimableFaction) {
                    final Faction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
                    if (playerFaction != null && playerFaction.equals(factionAt)) {
                        return;
                    }
                    player.sendMessage(ChatColor.YELLOW + "Le portail dirige vers le territoire de " + factionAt.getDisplayName((CommandSender)player) + ChatColor.YELLOW + ", la téléportation a été annulée.");
                    event.setCancelled(true);
                }
            }
        }
    }
    
    @SuppressWarnings("unused")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onCreatureSpawn(final CreatureSpawnEvent event) {
        final CreatureSpawnEvent.SpawnReason reason = event.getSpawnReason();
        if (reason == CreatureSpawnEvent.SpawnReason.SLIME_SPLIT) {
            return;
        }
        final Location location = event.getLocation();
        final Faction factionAt = this.plugin.getFactionManager().getFactionAt(location);
        if (factionAt.isSafezone()) {
            final CreatureSpawnEvent.SpawnReason spawner = CreatureSpawnEvent.SpawnReason.SPAWNER;
        }
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityDamage(final EntityDamageEvent event) {
        final Entity entity = event.getEntity();
        if (entity instanceof Player) {
            final Player player = (Player)entity;
            final Faction playerFactionAt = this.plugin.getFactionManager().getFactionAt(player.getLocation());
            final EntityDamageEvent.DamageCause cause = event.getCause();
            if (playerFactionAt.isSafezone() && cause != EntityDamageEvent.DamageCause.SUICIDE) {
                event.setCancelled(true);
            }
            final Player attacker = BukkitUtils.getFinalAttacker(event, true);
            if (attacker != null) {
                final Faction attackerFactionAt = this.plugin.getFactionManager().getFactionAt(attacker.getLocation());
                if (attackerFactionAt.isSafezone()) {
                    event.setCancelled(true);
                    attacker.sendMessage(ChatColor.RED + "Tu ne peux pas attaquer un joueur dans une safe-zone");
                    return;
                }
                if (playerFactionAt.isSafezone()) {
                    attacker.sendMessage(ChatColor.RED + "Tu ne peux pas attaquer un joueur dans une safe-zone.");
                    return;
                }
                final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
                final PlayerFaction attackerFaction;
                if (playerFaction != null && (attackerFaction = this.plugin.getFactionManager().getPlayerFaction(attacker)) != null) {
                    final Role role = playerFaction.getMember(player).getRole();
                    final String astrix = role.getAstrix();
                    if (attackerFaction.equals(playerFaction)) {
                        attacker.sendMessage(ConfigurationService.TEAMMATE_COLOUR + astrix + player.getName() + ChatColor.YELLOW + " est dans ta faction.");
                        event.setCancelled(true);
                    }
                    else if (attackerFaction.getAllied().contains(playerFaction.getUniqueID())) {
                        attacker.sendMessage(ConfigurationService.TEAMMATE_COLOUR + astrix + player.getName() + ChatColor.YELLOW + " est un allié.");
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onVehicleEnter(final VehicleEnterEvent event) {
        final Entity entered = event.getEntered();
        if (entered instanceof Player) {
            final Vehicle vehicle = event.getVehicle();
            if (vehicle instanceof Horse) {
                final Horse horse = (Horse)event.getVehicle();
                final AnimalTamer owner = horse.getOwner();
                if (owner != null && !owner.equals(entered)) {
                    ((Player)entered).sendMessage(ChatColor.YELLOW + "Tu ne peux pas monter sur ce cheval, il appartient à " + ChatColor.RED + owner.getName() + ChatColor.YELLOW + '.');
                    event.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onFoodLevelChange(final FoodLevelChangeEvent event) {
        final Entity entity = (Entity)event.getEntity();
        if (entity instanceof Player && ((Player)entity).getFoodLevel() < event.getFoodLevel() && this.plugin.getFactionManager().getFactionAt(entity.getLocation()).isSafezone()) {
            event.setCancelled(true);
        }
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPotionSplash(final PotionSplashEvent event) {
        final ThrownPotion potion = event.getEntity();
        if (!BukkitUtils.isDebuff(potion)) {
            return;
        }
        final Faction factionAt = this.plugin.getFactionManager().getFactionAt(potion.getLocation());
        if (factionAt.isSafezone()) {
            event.setCancelled(true);
            return;
        }
        final ProjectileSource source = (ProjectileSource)potion.getShooter();
        if (source instanceof Player) {
            final Player player = (Player)source;
            for (final LivingEntity affected : event.getAffectedEntities()) {
                if (affected instanceof Player && !player.equals(affected)) {
                    final Player target = (Player)affected;
                    if (target.equals(source) || !this.plugin.getFactionManager().getFactionAt(target.getLocation()).isSafezone()) {
                        continue;
                    }
                    event.setIntensity(affected, 0.0);
                }
            }
        }
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityTarget(final EntityTargetEvent event) {
        switch ($SWITCH_TABLE$org$bukkit$event$entity$EntityTargetEvent$TargetReason()[event.getReason().ordinal()]) {
            case 2:
            case 8: {
                final Entity target = event.getTarget();
                if (!(event.getEntity() instanceof LivingEntity) || !(target instanceof Player)) {
                    break;
                }
                final Faction factionAt = this.plugin.getFactionManager().getFactionAt(target.getLocation());
                final Faction playerFaction;
                if (factionAt.isSafezone() || ((playerFaction = this.plugin.getFactionManager().getPlayerFaction((Player)target)) != null && factionAt.equals(playerFaction))) {
                    event.setCancelled(true);
                    break;
                }
                break;
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (!event.hasBlock()) {
            return;
        }
        final Block block = event.getClickedBlock();
        final Action action = event.getAction();
        if (action == Action.PHYSICAL && !attemptBuild((Entity)event.getPlayer(), block.getLocation(), null)) {
            event.setCancelled(true);
        }
        if (action == Action.RIGHT_CLICK_BLOCK) {
            boolean canBuild = !ProtectionListener.BLOCK_INTERACTABLES.contains((Object)block.getType());
            if (canBuild) {
                final Material itemType = event.hasItem() ? event.getItem().getType() : null;
                if (itemType != null && ProtectionListener.ITEM_BLOCK_INTERACTABLES.containsKey((Object)itemType) && ProtectionListener.ITEM_BLOCK_INTERACTABLES.get((Object)itemType).contains((Object)event.getClickedBlock().getType())) {
                    canBuild = false;
                }
                else {
                    final MaterialData materialData = block.getState().getData();
                    if (materialData instanceof Cauldron) {
                        final Cauldron cauldron = (Cauldron)materialData;
                        if (!cauldron.isEmpty() && event.hasItem() && event.getItem().getType() == Material.GLASS_BOTTLE) {
                            canBuild = false;
                        }
                    }
                }
            }
            if (!canBuild && !attemptBuild((Entity)event.getPlayer(), block.getLocation(), ChatColor.YELLOW + "Tu ne peux pas faire ça dans le territoire de %1$s" + ChatColor.YELLOW + '.', true)) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockBurn(final BlockBurnEvent event) {
        final Faction factionAt = this.plugin.getFactionManager().getFactionAt(event.getBlock().getLocation());
        if (factionAt instanceof WarzoneFaction || (factionAt instanceof Raidable && !((Raidable)factionAt).isRaidable())) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockFade(final BlockFadeEvent event) {
        final Faction factionAt = this.plugin.getFactionManager().getFactionAt(event.getBlock().getLocation());
        if (factionAt instanceof ClaimableFaction && !(factionAt instanceof PlayerFaction)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onLeavesDelay(final LeavesDecayEvent event) {
        final Faction factionAt = this.plugin.getFactionManager().getFactionAt(event.getBlock().getLocation());
        if (factionAt instanceof ClaimableFaction && !(factionAt instanceof PlayerFaction)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockForm(final BlockFormEvent event) {
        final Faction factionAt = this.plugin.getFactionManager().getFactionAt(event.getBlock().getLocation());
        if (factionAt instanceof ClaimableFaction && !(factionAt instanceof PlayerFaction)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityChangeBlock(final EntityChangeBlockEvent event) {
        final Entity entity = event.getEntity();
        if (entity instanceof LivingEntity && !attemptBuild(entity, event.getBlock().getLocation(), null)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockBreak(final BlockBreakEvent event) {
        if (!attemptBuild((Entity)event.getPlayer(), event.getBlock().getLocation(), ChatColor.YELLOW + "Tu ne peux pas construire dans le territoire de %1$s" + ChatColor.YELLOW + '.')) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockPlace(final BlockPlaceEvent event) {
        if (!attemptBuild((Entity)event.getPlayer(), event.getBlockPlaced().getLocation(), ChatColor.YELLOW + "Tu ne peux pas construire dans le territoire de %1$s" + ChatColor.YELLOW + '.')) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBucketFill(final PlayerBucketFillEvent event) {
        if (!attemptBuild((Entity)event.getPlayer(), event.getBlockClicked().getLocation(), ChatColor.YELLOW + "Tu ne peux pas construire dans le territoire de %1$s" + ChatColor.YELLOW + '.')) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBucketEmpty(final PlayerBucketEmptyEvent event) {
        if (!attemptBuild((Entity)event.getPlayer(), event.getBlockClicked().getLocation(), ChatColor.YELLOW + "Tu ne peux pas construire dans le territoire de %1$s" + ChatColor.YELLOW + '.')) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onHangingBreakByEntity(final HangingBreakByEntityEvent event) {
        final Entity remover = event.getRemover();
        if (remover instanceof Player && !attemptBuild(remover, event.getEntity().getLocation(), ChatColor.YELLOW + "Tu ne peux pas construire dans le territoire de %1$s" + ChatColor.YELLOW + '.')) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onHangingPlace(final HangingPlaceEvent event) {
        if (!attemptBuild((Entity)event.getPlayer(), event.getEntity().getLocation(), ChatColor.YELLOW + "Tu ne peux pas construire dans le territoire de %1$s" + ChatColor.YELLOW + '.')) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onHangingDamageByEntity(final EntityDamageByEntityEvent event) {
        final Entity entity = event.getEntity();
        if (entity instanceof Hanging) {
            final Player attacker = BukkitUtils.getFinalAttacker((EntityDamageEvent)event, false);
            if (!attemptBuild((Entity)attacker, entity.getLocation(), ChatColor.YELLOW + "Tu ne peux pas construire dans le territoire de %1$s" + ChatColor.YELLOW + '.')) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onHangingInteractByPlayer(final PlayerInteractEntityEvent event) {
        final Entity entity = event.getRightClicked();
        if (entity instanceof Hanging && !attemptBuild((Entity)event.getPlayer(), entity.getLocation(), ChatColor.YELLOW + "Tu ne peux pas construire dans le territoire de %1$s" + ChatColor.YELLOW + '.')) {
            event.setCancelled(true);
        }
    }
    
    static /* synthetic */ int[] $SWITCH_TABLE$org$bukkit$event$block$BlockIgniteEvent$IgniteCause() {
        final int[] $switch_TABLE$org$bukkit$event$block$BlockIgniteEvent$IgniteCause = ProtectionListener.$SWITCH_TABLE$org$bukkit$event$block$BlockIgniteEvent$IgniteCause;
        if ($switch_TABLE$org$bukkit$event$block$BlockIgniteEvent$IgniteCause != null) {
            return $switch_TABLE$org$bukkit$event$block$BlockIgniteEvent$IgniteCause;
        }
        final int[] $switch_TABLE$org$bukkit$event$block$BlockIgniteEvent$IgniteCause2 = new int[BlockIgniteEvent.IgniteCause.values().length];
        try {
            $switch_TABLE$org$bukkit$event$block$BlockIgniteEvent$IgniteCause2[BlockIgniteEvent.IgniteCause.ENDER_CRYSTAL.ordinal()] = 6;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            $switch_TABLE$org$bukkit$event$block$BlockIgniteEvent$IgniteCause2[BlockIgniteEvent.IgniteCause.EXPLOSION.ordinal()] = 7;
        }
        catch (NoSuchFieldError noSuchFieldError2) {}
        try {
            $switch_TABLE$org$bukkit$event$block$BlockIgniteEvent$IgniteCause2[BlockIgniteEvent.IgniteCause.FIREBALL.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError3) {}
        try {
            $switch_TABLE$org$bukkit$event$block$BlockIgniteEvent$IgniteCause2[BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError4) {}
        try {
            $switch_TABLE$org$bukkit$event$block$BlockIgniteEvent$IgniteCause2[BlockIgniteEvent.IgniteCause.LAVA.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError5) {}
        try {
            $switch_TABLE$org$bukkit$event$block$BlockIgniteEvent$IgniteCause2[BlockIgniteEvent.IgniteCause.LIGHTNING.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError6) {}
        try {
            $switch_TABLE$org$bukkit$event$block$BlockIgniteEvent$IgniteCause2[BlockIgniteEvent.IgniteCause.SPREAD.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError7) {}
        return ProtectionListener.$SWITCH_TABLE$org$bukkit$event$block$BlockIgniteEvent$IgniteCause = $switch_TABLE$org$bukkit$event$block$BlockIgniteEvent$IgniteCause2;
    }
    
    static /* synthetic */ int[] $SWITCH_TABLE$org$bukkit$event$entity$EntityTargetEvent$TargetReason() {
        final int[] $switch_TABLE$org$bukkit$event$entity$EntityTargetEvent$TargetReason = ProtectionListener.$SWITCH_TABLE$org$bukkit$event$entity$EntityTargetEvent$TargetReason;
        if ($switch_TABLE$org$bukkit$event$entity$EntityTargetEvent$TargetReason != null) {
            return $switch_TABLE$org$bukkit$event$entity$EntityTargetEvent$TargetReason;
        }
        final int[] $switch_TABLE$org$bukkit$event$entity$EntityTargetEvent$TargetReason2 = new int[EntityTargetEvent.TargetReason.values().length];
        try {
            $switch_TABLE$org$bukkit$event$entity$EntityTargetEvent$TargetReason2[EntityTargetEvent.TargetReason.CLOSEST_PLAYER.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            $switch_TABLE$org$bukkit$event$entity$EntityTargetEvent$TargetReason2[EntityTargetEvent.TargetReason.COLLISION.ordinal()] = 12;
        }
        catch (NoSuchFieldError noSuchFieldError2) {}
        try {
            $switch_TABLE$org$bukkit$event$entity$EntityTargetEvent$TargetReason2[EntityTargetEvent.TargetReason.CUSTOM.ordinal()] = 13;
        }
        catch (NoSuchFieldError noSuchFieldError3) {}
        try {
            $switch_TABLE$org$bukkit$event$entity$EntityTargetEvent$TargetReason2[EntityTargetEvent.TargetReason.DEFEND_VILLAGE.ordinal()] = 9;
        }
        catch (NoSuchFieldError noSuchFieldError4) {}
        try {
            $switch_TABLE$org$bukkit$event$entity$EntityTargetEvent$TargetReason2[EntityTargetEvent.TargetReason.FORGOT_TARGET.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError5) {}
        try {
            $switch_TABLE$org$bukkit$event$entity$EntityTargetEvent$TargetReason2[EntityTargetEvent.TargetReason.OWNER_ATTACKED_TARGET.ordinal()] = 7;
        }
        catch (NoSuchFieldError noSuchFieldError6) {}
        try {
            $switch_TABLE$org$bukkit$event$entity$EntityTargetEvent$TargetReason2[EntityTargetEvent.TargetReason.PIG_ZOMBIE_TARGET.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError7) {}
        try {
            $switch_TABLE$org$bukkit$event$entity$EntityTargetEvent$TargetReason2[EntityTargetEvent.TargetReason.RANDOM_TARGET.ordinal()] = 8;
        }
        catch (NoSuchFieldError noSuchFieldError8) {}
        try {
            $switch_TABLE$org$bukkit$event$entity$EntityTargetEvent$TargetReason2[EntityTargetEvent.TargetReason.REINFORCEMENT_TARGET.ordinal()] = 11;
        }
        catch (NoSuchFieldError noSuchFieldError9) {}
        try {
            $switch_TABLE$org$bukkit$event$entity$EntityTargetEvent$TargetReason2[EntityTargetEvent.TargetReason.TARGET_ATTACKED_ENTITY.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError10) {}
        try {
            $switch_TABLE$org$bukkit$event$entity$EntityTargetEvent$TargetReason2[EntityTargetEvent.TargetReason.TARGET_ATTACKED_NEARBY_ENTITY.ordinal()] = 10;
        }
        catch (NoSuchFieldError noSuchFieldError11) {}
        try {
            $switch_TABLE$org$bukkit$event$entity$EntityTargetEvent$TargetReason2[EntityTargetEvent.TargetReason.TARGET_ATTACKED_OWNER.ordinal()] = 6;
        }
        catch (NoSuchFieldError noSuchFieldError12) {}
        try {
            $switch_TABLE$org$bukkit$event$entity$EntityTargetEvent$TargetReason2[EntityTargetEvent.TargetReason.TARGET_DIED.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError13) {}
        return ProtectionListener.$SWITCH_TABLE$org$bukkit$event$entity$EntityTargetEvent$TargetReason = $switch_TABLE$org$bukkit$event$entity$EntityTargetEvent$TargetReason2;
    }
}
