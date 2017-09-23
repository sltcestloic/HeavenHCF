package fr.taeron.hcf.combatlog;

import org.bukkit.entity.*;
import java.util.*;
import org.bukkit.event.player.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import org.bukkit.plugin.*;

import fr.taeron.hcf.HCF;
import net.minecraft.server.v1_7_R4.EntityPlayer;

import org.bukkit.*;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.event.CraftEventFactory;

import java.util.concurrent.*;

public class CombatLogListener implements Listener
{
    @SuppressWarnings("unused")
	private final ExecutorService executor;
    private static final int NEARBY_SPAWN_RADIUS = 64;
    private static Set<UUID> safelyDisconnected;
    private final HCF plugin;
    
    @SuppressWarnings("static-access")
	public CombatLogListener(final HCF plugin) {
        this.executor = Executors.newFixedThreadPool(2);
        this.safelyDisconnected = new HashSet<UUID>();
        this.plugin = plugin;
    }
    
    public static void safelyDisconnect(final Player player, final String reason) {
        if (safelyDisconnected.add(player.getUniqueId())) {
            player.kickPlayer(reason);
        }
    }
    
    @EventHandler
    public void despawnNpc(final PlayerJoinEvent event) {
    	this.plugin.getPlayerCache().addPlayer(event.getPlayer());
        final Npc npc = this.plugin.getNpcManager().getSpawnedNpc(event.getPlayer().getUniqueId());
        if (npc != null) {
            this.plugin.getNpcManager().despawn(npc);
        }
    }

    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void updateDespawnTime(final EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        final Player player = (Player)event.getEntity();
        if (!this.plugin.getNpcPlayerHelper().isNpc(player)) {
            return;
        }
        final UUID npcId = this.plugin.getNpcPlayerHelper().getIdentity(player).getId();
        final Npc npc = this.plugin.getNpcManager().getSpawnedNpc(npcId);
        if (this.plugin.getNpcManager().hasDespawnTask(npc)) {
            final long despawnTime = System.currentTimeMillis() + 30000;
            this.plugin.getNpcManager().getDespawnTask(npc).setTime(despawnTime);
        }
    }
    

    
    @SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOWEST)
    public void syncOffline(final PlayerDeathEvent event) {
        final Player player = event.getEntity();
        if (!this.plugin.getNpcPlayerHelper().isNpc(player)) {
            return;
        }
        CraftEventFactory.callPlayerDeathEvent((EntityPlayer)this.plugin.getNpcManager().getSpawnedNpc(player.getUniqueId()).getEntity().getPlayer(), event.getDrops(), "§7(Combat-Logger) " + event.getDeathMessage(), false);
        HCF.getPlugin().getDeathbanManager().applyDeathBan(this.plugin.getNpcManager().getSpawnedNpc(player.getUniqueId()).getEntity().getUniqueId(), "§7(Combat-Logger) " + event.getDeathMessage());
        player.setHealth(20);
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, (Runnable)new Runnable() {
            @Override
            public void run() {
            	CombatLogListener.this.plugin.getNpcPlayerHelper().syncOffline(player);
            }
        });
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@EventHandler
    public void syncOffline(final AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            return;
        }
        final UUID playerId = event.getUniqueId();
        if (!this.plugin.getNpcManager().npcExists(playerId)) {
            return;
        }
        final Future<?> future = (Future<?>)Bukkit.getScheduler().callSyncMethod((Plugin)this.plugin, (Callable)new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                final Npc npc = CombatLogListener.this.plugin.getNpcManager().getSpawnedNpc(playerId);
                if (npc == null) {
                    return null;
                }
                CombatLogListener.this.plugin.getNpcPlayerHelper().syncOffline(npc.getEntity());
                return null;
            }
        });
        try {
            future.get();
        }
        catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
    
    @EventHandler
    public void syncOffline(final NpcDespawnEvent event) {
        final Npc npc = event.getNpc();
        final Player player = this.plugin.getPlayerCache().getPlayer(npc.getIdentity().getId());
        if (player == null) {
            this.plugin.getNpcPlayerHelper().syncOffline(npc.getEntity());
            return;
        }
        final Player npcPlayer = npc.getEntity();
        final CraftPlayer p = (CraftPlayer) npc.getEntity();
        player.setMaximumAir(npcPlayer.getMaximumAir());
        player.setRemainingAir(npcPlayer.getRemainingAir());
        player.setHealthScale(npcPlayer.getHealthScale());
        player.setMaxHealth(p.getMaxHealth());
        player.setHealth(p.getHealth());
        player.setTotalExperience(npcPlayer.getTotalExperience());
        player.setFoodLevel(npcPlayer.getFoodLevel());
        player.setExhaustion(npcPlayer.getExhaustion());
        player.setSaturation(npcPlayer.getSaturation());
        player.setFireTicks(npcPlayer.getFireTicks());
        player.getInventory().setContents(npcPlayer.getInventory().getContents());
        player.getInventory().setArmorContents(npcPlayer.getInventory().getArmorContents());
        player.addPotionEffects(npcPlayer.getActivePotionEffects());
    }


    
  
    
    @SuppressWarnings("static-access")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();
        final boolean result = this.safelyDisconnected.remove(uuid);

        if (player.getGameMode() != GameMode.CREATIVE && !player.isDead() && !result) {
            if (this.plugin.getTimerManager().teleportTimer.getNearbyEnemies(player, this.NEARBY_SPAWN_RADIUS) <= 0 || this.plugin.getTimerManager().starterTimer.getRemaining(player) > 0) {
                return;
            }
            final Location location = player.getLocation();
            if (this.plugin.getFactionManager().getFactionAt(location).isSafezone()) {
                return;
            }
            this.plugin.getNpcManager().spawn(player);
            this.plugin.getPlayerCache().removePlayer(player);
        }
    }
}
