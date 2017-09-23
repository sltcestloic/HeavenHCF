package fr.taeron.hcf.timer.type;

import fr.taeron.hcf.*;
import java.util.concurrent.*;
import fr.taeron.hcf.visualise.*;
import fr.taeron.hcf.timer.event.*;
import com.google.common.base.Optional;

import org.bukkit.event.*;
import fr.taeron.hcf.faction.event.cause.*;
import fr.taeron.hcf.faction.claim.*;
import org.bukkit.inventory.*;
import org.heavenmc.core.util.BukkitUtils;
import org.heavenmc.core.util.Config;
import org.heavenmc.core.util.GenericUtils;
import org.bukkit.*;
import org.bukkit.event.block.*;
import org.bukkit.event.player.*;
import fr.taeron.hcf.timer.*;
import org.spigotmc.event.player.*;
import fr.taeron.hcf.faction.event.*;
import fr.taeron.hcf.faction.type.*;
import org.bukkit.command.*;
import org.bukkit.event.entity.*;
import org.bukkit.entity.*;
import javax.annotation.*;
import java.util.stream.*;
import java.util.*;

public class StarterTimer extends PlayerTimer implements Listener{
	
    public final Set<UUID> legible;
    private final Map<UUID, Long> itemUUIDPickupDelays = new HashMap<>();
    
    private final HCF plugin;
    
    public StarterTimer(final HCF plugin) {
        super("Start", TimeUnit.MINUTES.toMillis(90L));
        this.legible = new HashSet<UUID>();
        this.plugin = plugin;
    }
    
    public String getScoreboardPrefix() {
        return ChatColor.BLUE.toString() + ChatColor.BOLD.toString();
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onSOTWHit(EntityDamageEvent e){
    	if(e.getEntity() instanceof Player && this.getRemaining((Player)e.getEntity()) > 0){
    		e.setCancelled(true);
    	}
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onFoodChange(FoodLevelChangeEvent e){
    	if(!(e.getEntity() instanceof Player)){
    		return;
    	}
    	if(this.getRemaining((Player)e.getEntity()) > 0 || HCF.getPlugin().getTimerManager().starterTimer.getRemaining((Player)e.getEntity()) > 0){
    		e.setCancelled(true);
    	}
    }
    
    @Override
    public void onExpire(final UUID userUUID) {
        final Player player = Bukkit.getPlayer(userUUID);
        if (player == null) {
            return;
        }
        if (this.getRemaining(player) <= 0L) {
            this.plugin.getVisualiseHandler().clearVisualBlocks(player, VisualType.CLAIM_BORDER, null);
            player.sendMessage(ChatColor.RED.toString() + "Ton Starter Timer a expiré.");
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onTimerStop(final TimerClearEvent event) {
        if (event.getTimer().equals(this)) {
            final Optional<UUID> optionalUserUUID = event.getUserUUID();
            if (optionalUserUUID.isPresent()) {
                this.onExpire((UUID)optionalUserUUID.get());
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onClaimChange(final FactionClaimChangedEvent event) {
        if (event.getCause() != ClaimChangeCause.CLAIM) {
            return;
        }
        final Collection<Claim> claims = event.getAffectedClaims();
        for (final Claim claim : claims) {
            final Collection<Player> players = (Collection<Player>)claim.getPlayers();
            for (final Player player : players) {
                if (this.getRemaining(player) > 0L) {
                    Location location = player.getLocation();
                    location.setX(claim.getMinimumX() - 1);
                    location.setY(0);
                    location.setZ(claim.getMinimumZ() - 1);
                    location = BukkitUtils.getHighestLocation(location, location);
                    if (!player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN)) {
                        continue;
                    }
                    player.sendMessage(ChatColor.RED + "Un territoire a été claim a l'endoit ou tu te trouvais, tu as été téléporté en dehors de ce dernier.");
                }
            }
        }
    }

    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final World world = player.getWorld();
        final Location location = player.getLocation();
        final Iterator<ItemStack> iterator = event.getDrops().iterator();
        while (iterator.hasNext()) {
            this.itemUUIDPickupDelays.put(world.dropItemNaturally(location, (ItemStack)iterator.next()).getUniqueId(), System.currentTimeMillis() + 30000L);
            iterator.remove();
        }
        this.clearCooldown(player);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBucketEmpty(final PlayerBucketEmptyEvent event) {
        final Player player = event.getPlayer();
        final long remaining = this.getRemaining(player);
        if (remaining > 0L) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Tu ne peux pas utiliser de sceau pendant que ton Starter Timer est actif.");
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockIgnite(final BlockIgniteEvent event) {
        final Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        final long remaining = this.getRemaining(player);
        if (remaining > 0L) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Tu ne peux pas utiliser de briquet tant que ton Starter Timer est actif.");
        }
    }
    

    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final TimerRunnable runnable = this.cooldowns.get(player.getUniqueId());
        if (runnable != null && runnable.getRemaining() > 0L) {
            runnable.setPaused(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerSpawnLocation(final PlayerSpawnLocationEvent event) {
        final Player player = event.getPlayer();
        if (!player.hasPlayedBefore()) {
            if (!this.plugin.getEotwHandler().isEndOfTheWorld() && this.legible.add(player.getUniqueId())) {
                player.sendMessage(ChatColor.GREEN + "");
                player.sendMessage(ChatColor.GREEN + "Tu viens de rejoindre le serveur pour la première fois depuis le début de cette map, tu as donc 60 minutes de Starter Timer");
                player.sendMessage(ChatColor.GREEN + "");
                this.setCooldown(player, player.getUniqueId());
                this.setPaused(player, player.getUniqueId(), true);
                HCF.getPlugin().getTimerManager().pvpProtectionTimer.legible.remove(player.getUniqueId());
            }
        }
        else if (this.isPaused(player) && this.getRemaining(player) > 0L && !this.plugin.getFactionManager().getFactionAt(event.getSpawnLocation()).isSafezone()) {
            this.setPaused(player, player.getUniqueId(), false);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerClaimEnterMonitor(final PlayerClaimEnterEvent event) {
        final Player player = event.getPlayer();
        if (event.getTo().getWorld().getEnvironment() == World.Environment.THE_END) {
            this.clearCooldown(player);
            return;
        }
        final Faction toFaction = event.getToFaction();
        final Faction fromFaction = event.getFromFaction();
        if (fromFaction.isSafezone() && !toFaction.isSafezone()) {
            if (this.legible.remove(player.getUniqueId())) {
                this.setCooldown(player, player.getUniqueId());
                this.setPaused(player, player.getUniqueId(), false);
                player.sendMessage(ChatColor.GREEN + "Ton Starter Timer a commencé.");
                return;
            }
            if (this.getRemaining(player) > 0L) {
                this.setPaused(player, player.getUniqueId(), false);
                player.sendMessage(ChatColor.RED + "Ton Starter Timer n'est plus en pause.");
            }
        }
        else if (!fromFaction.isSafezone() && toFaction.isSafezone() && this.getRemaining(player) > 0L) {
            player.sendMessage(ChatColor.GREEN + "Ton Starter Timer est désormais en pause.");
            this.setPaused(player, player.getUniqueId(), true);
        }
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerClaimEnter(final PlayerClaimEnterEvent event) {
        final Player player = event.getPlayer();
        final Faction toFaction = event.getToFaction();
        if (toFaction instanceof ClaimableFaction && this.getRemaining(player) > 0L) {
            final PlayerFaction playerFaction;
            if (event.getEnterCause() == PlayerClaimEnterEvent.EnterCause.TELEPORT && toFaction instanceof PlayerFaction && (playerFaction = this.plugin.getFactionManager().getPlayerFaction(player)) != null && playerFaction.equals(toFaction)) {
                player.sendMessage(ChatColor.RED + "Tu es entré dans ton propre claim, ton Starter Timer a donc été retiré.");
                this.clearCooldown(player);
                return;
            }
            if (!toFaction.isSafezone() && !(toFaction instanceof RoadFaction)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "Tu ne peux pas entrer dans le territoire de " + toFaction.getDisplayName((CommandSender)player) + ChatColor.RED + " tant que ton Starter Timer est actif. Fait '" + ChatColor.GOLD + "/pvp enable" + ChatColor.RED + "' pour le désactiver.");
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
        final Entity entity = event.getEntity();
        if (entity instanceof Player) {
            final Player attacker = BukkitUtils.getFinalAttacker((EntityDamageEvent)event, true);
            if (attacker == null) {
                return;
            }
            final Player player = (Player)entity;
            if (this.getRemaining(player) > 0L) {
                event.setCancelled(true);
                attacker.sendMessage(ChatColor.RED + player.getName() + " est sous Starter Timer.");
                return;
            }
            if (this.getRemaining(attacker) > 0L) {
                event.setCancelled(true);
                attacker.sendMessage(ChatColor.RED + "Tu ne peux pas attaquer de joueurs tant que ton Starter Timer est actif."); 
            }
        }
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPotionSplash(final PotionSplashEvent event) {
        final ThrownPotion potion = event.getPotion();
        if (potion.getShooter() instanceof Player && BukkitUtils.isDebuff(potion)) {
            for (final LivingEntity livingEntity : event.getAffectedEntities()) {
                if (livingEntity instanceof Player && this.getRemaining((Player)livingEntity) > 0L) {
                    event.setIntensity(livingEntity, 0.0);
                }
            }
        }
    }
    
    public Set<UUID> getLegible() {
        return this.legible;
    }
    
    @Override
    public long getRemaining(final UUID playerUUID) {
        return this.plugin.getEotwHandler().isEndOfTheWorld() ? 0L : super.getRemaining(playerUUID);
    }
    
    @Override
    public boolean setCooldown(@Nullable final Player player, final UUID playerUUID, final long duration, final boolean overwrite) {
        return !this.plugin.getEotwHandler().isEndOfTheWorld() && super.setCooldown(player, playerUUID, duration, overwrite);
    }
    
    @Override
    public TimerRunnable clearCooldown(final UUID playerUUID) {
        final TimerRunnable runnable = super.clearCooldown(playerUUID);
        if (runnable != null) {
            this.legible.remove(playerUUID);
            return runnable;
        }
        return null;
    }
    
    @Override
    public void load(final Config config) {
        super.load(config);
        final Object object = config.get("starter-timer-legible");
        if (object instanceof List) {
            this.legible.addAll((Collection<? extends UUID>)GenericUtils.createList(object, String.class).stream().map(UUID::fromString).collect(Collectors.toList()));
        }
    }
}
