package fr.taeron.hcf.timer.type;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.heavenmc.core.util.Config;

import com.google.common.base.Preconditions;

import fr.taeron.hcf.HCF;
import fr.taeron.hcf.pvpclass.PvpClass;
import fr.taeron.hcf.timer.PlayerTimer;
import fr.taeron.hcf.timer.TimerRunnable;

public class PvpClassWarmupTimer extends PlayerTimer implements Listener {

    protected final Map<UUID, PvpClass> classWarmups = new HashMap<>();

    private final HCF plugin;

    public PvpClassWarmupTimer(HCF plugin) {
        super("Chargement", TimeUnit.SECONDS.toMillis(5L), false);
        this.plugin = plugin;

        new BukkitRunnable() {
			@Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    attemptEquip(player);
                }
            }
        }.runTaskTimer(plugin, 10L , 10L);
    }

    @Override
    public void onDisable(Config config) {
        super.onDisable(config);
        this.classWarmups.clear();
    }

    @Override
    public String getScoreboardPrefix() {
        return ChatColor.AQUA + ChatColor.BOLD.toString();
    }

    @Override
    public TimerRunnable clearCooldown(UUID playerUUID) {
    	TimerRunnable runnable = super.clearCooldown(playerUUID);
        if (runnable != null) {
            this.classWarmups.remove(playerUUID);
            return runnable;
        }

        return null;
    }

    @Override
    public void onExpire(UUID userUUID) {
        Player player = Bukkit.getPlayer(userUUID);
        if (player == null)
            return;

        PvpClass pvpClass = this.classWarmups.remove(userUUID);
        Preconditions.checkNotNull(pvpClass, "Attempted to equip a class for %s, but nothing was added", player.getName());
        this.plugin.getPvpClassManager().setEquippedClass(player, pvpClass);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerKick(PlayerQuitEvent event) {
        this.plugin.getPvpClassManager().setEquippedClass(event.getPlayer(), null);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.attemptEquip(event.getPlayer());
    }

   /* @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEquipmentSet(EquipmentSetEvent event) {
        HumanEntity humanEntity = event.getHumanEntity();
        if (humanEntity instanceof Player) {
            this.attemptEquip((Player) humanEntity);
        }
    }*/
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void equipArmor(InventoryClickEvent e){
    	if(!(e.getWhoClicked() instanceof Player)){
    		//Je pense pas que ça puisse arriver
    		return;
    	}
    	Player p = (Player) e.getWhoClicked();
    	new BukkitRunnable(){
    		public void run(){
    			if(p.getInventory().getHelmet() == null || p.getInventory().getHelmet().getType() == Material.AIR){
    				return;
    			}
    			if(p.getInventory().getChestplate() == null || p.getInventory().getChestplate().getType() == Material.AIR){
    				return;
    			}
    			if(p.getInventory().getLeggings() == null || p.getInventory().getLeggings().getType() == Material.AIR){
    				return;
    			}
    			if(p.getInventory().getBoots() == null || p.getInventory().getBoots().getType() == Material.AIR){
    				return;
    			}
    			if(p.getInventory().getHelmet().getType() == Material.GOLD_HELMET
    			&& p.getInventory().getChestplate().getType() == Material.GOLD_CHESTPLATE
    			&& p.getInventory().getLeggings().getType() == Material.GOLD_LEGGINGS
   				&& p.getInventory().getBoots().getType() == Material.GOLD_BOOTS){
    				PvpClassWarmupTimer.this.attemptEquip(p);
    			}
    			if(p.getInventory().getHelmet().getType() == Material.IRON_HELMET
    	    	&& p.getInventory().getChestplate().getType() == Material.IRON_CHESTPLATE
    	    	&& p.getInventory().getLeggings().getType() == Material.IRON_LEGGINGS
    	   		&& p.getInventory().getBoots().getType() == Material.IRON_BOOTS){
    	    		PvpClassWarmupTimer.this.attemptEquip(p);
    	    	}
    		}
    	}.runTaskLater(this.plugin, 1L);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void equipArmorWithRightClick(PlayerInteractEvent e){
    	if(e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK){
    		return;
    	}
    	Player p = (Player) e.getPlayer();
    	new BukkitRunnable(){
    		public void run(){
    			if(p.getInventory().getHelmet() == null || p.getInventory().getHelmet().getType() == Material.AIR){
    				return;
    			}
    			if(p.getInventory().getChestplate() == null || p.getInventory().getChestplate().getType() == Material.AIR){
    				return;
    			}
    			if(p.getInventory().getLeggings() == null || p.getInventory().getLeggings().getType() == Material.AIR){
    				return;
    			}
    			if(p.getInventory().getBoots() == null || p.getInventory().getBoots().getType() == Material.AIR){
    				return;
    			}
    			if(p.getInventory().getHelmet().getType() == Material.GOLD_HELMET
    			&& p.getInventory().getChestplate().getType() == Material.GOLD_CHESTPLATE
    			&& p.getInventory().getLeggings().getType() == Material.GOLD_LEGGINGS
   				&& p.getInventory().getBoots().getType() == Material.GOLD_BOOTS){
    				PvpClassWarmupTimer.this.attemptEquip(p);
    			}
    			if(p.getInventory().getHelmet().getType() == Material.IRON_HELMET
    	    	&& p.getInventory().getChestplate().getType() == Material.IRON_CHESTPLATE
    	    	&& p.getInventory().getLeggings().getType() == Material.IRON_LEGGINGS
    	   		&& p.getInventory().getBoots().getType() == Material.IRON_BOOTS){
    	    		PvpClassWarmupTimer.this.attemptEquip(p);
    	    	}
    		}
    	}.runTaskLater(this.plugin, 1L);
    }

    private void attemptEquip(Player player) {
        PvpClass current = plugin.getPvpClassManager().getEquippedClass(player);
        if (current != null) {
            if (current.isApplicableFor(player)) {
                return;
            }

            this.plugin.getPvpClassManager().setEquippedClass(player, null);
        } else if ((current = classWarmups.get(player.getUniqueId())) != null) {
            if (current.isApplicableFor(player)) {
                return;
            }

            this.clearCooldown(player.getUniqueId());
        }

        Collection<PvpClass> pvpClasses = plugin.getPvpClassManager().getPvpClasses();
        for (PvpClass pvpClass : pvpClasses) {
            if (pvpClass.isApplicableFor(player)) {
                this.classWarmups.put(player.getUniqueId(), pvpClass);
                this.setCooldown(player, player.getUniqueId(), pvpClass.getWarmupDelay(), false);
                break;
            }
        }
    }
}
