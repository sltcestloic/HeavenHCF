package fr.taeron.hcf.pvpclass.archer;

import fr.taeron.hcf.pvpclass.*;
import fr.taeron.hcf.scoreboard.ScoreboardHandler;

import net.minecraft.util.gnu.trove.map.*;
import java.util.concurrent.*;
import net.minecraft.util.gnu.trove.map.hash.*;
import org.bukkit.potion.*;
import org.bukkit.plugin.*;
import org.bukkit.metadata.*;
import org.bukkit.event.*;
import fr.taeron.hcf.*;
import org.bukkit.scheduler.*;
import org.bukkit.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.entity.*;
import org.bukkit.*;
import org.bukkit.projectiles.*;
import java.util.*;
import org.bukkit.inventory.*;

public class ArcherClass extends PvpClass implements Listener
{
    @SuppressWarnings("unused")
	private static final PotionEffect ARCHER_CRITICAL_EFFECT;
    @SuppressWarnings("unused")
	private static final int MARK_TIMEOUT_SECONDS = 15;
    @SuppressWarnings("unused")
	private static final int MARK_EXECUTION_LEVEL = 3;
    @SuppressWarnings("unused")
	private static final float MINIMUM_FORCE = 0.5f;
    @SuppressWarnings("unused")
	private static final String ARROW_FORCE_METADATA = "ARROW_FORCE";
    public static final HashMap<UUID, UUID> tagged;
    @SuppressWarnings("unused")
	private static final PotionEffect ARCHER_SPEED_EFFECT;
    @SuppressWarnings("unused")
	private static final HashMap<UUID, Long> ARCHER_COOLDOWN;
    @SuppressWarnings("unused")
	private static final long ARCHER_SPEED_COOLDOWN_DELAY;
    @SuppressWarnings("unused")
	private final TObjectLongMap<UUID> archerSpeedCooldowns;
    private final HCF plugin;
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public ArcherClass(final HCF plugin) {
        super("Archer", TimeUnit.SECONDS.toMillis(1L));
        this.archerSpeedCooldowns = (TObjectLongMap<UUID>)new TObjectLongHashMap();
        this.plugin = plugin;
        this.passiveEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityShootBow(final EntityShootBowEvent event) {
        final Entity projectile = event.getProjectile();
        if (projectile instanceof Arrow) {
            projectile.setMetadata("ARROW_FORCE", (MetadataValue)new FixedMetadataValue((Plugin)this.plugin, (Object)event.getForce()));
        }
    }
    
    @EventHandler
    public void onPlayerClickSugar(final PlayerInteractEvent e) {
        final Player p = e.getPlayer();
        if (this.plugin.getPvpClassManager().getEquippedClass(p) != null && this.plugin.getPvpClassManager().getEquippedClass(p).equals(this) && p.getItemInHand().getType() == Material.SUGAR) {
            if (Cooldowns.isOnCooldown("Archer_item_cooldown", p)) {
                p.sendMessage(ChatColor.RED + "Tu es sous cooldown pendant " + ChatColor.DARK_RED.toString() + Cooldowns.getCooldownForPlayerInt("Archer_item_cooldown", p) + ChatColor.RED.toString() + " secondes");
                e.setCancelled(true);
                return;
            }
            Cooldowns.addCooldown("Archer_item_cooldown", p, 25);
            p.sendMessage(ChatColor.GREEN.toString() + "Speed 4 activée");
            if (p.getItemInHand().getAmount() == 1) {
                p.getInventory().remove(p.getItemInHand());
            }
            else {
                p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
            }
            p.removePotionEffect(PotionEffectType.SPEED);
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 120, 4));
            new BukkitRunnable() {
                public void run() {
                    if (ArcherClass.this.isApplicableFor(p)) {
                        p.removePotionEffect(PotionEffectType.SPEED);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
                    }
                }
            }.runTaskLater(this.plugin, 120L);
        }
    }
    
    @EventHandler
    public void onQuit(final PlayerQuitEvent e) {
        if (ArcherClass.tagged.containsKey(e.getPlayer().getUniqueId())) {
            ArcherClass.tagged.remove(e.getPlayer().getUniqueId());
        }
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityDamage(final EntityDamageByEntityEvent event) {
        final Entity entity = event.getEntity();
        final Entity damager = event.getDamager();
        if (entity instanceof Player && damager instanceof Arrow) {
            final Arrow arrow = (Arrow)damager;
            final ProjectileSource source = arrow.getShooter();
            if (source instanceof Player) {
                final Player damaged = (Player)event.getEntity();
                final Player shooter = (Player)source;
                if(damaged == shooter){
                	return;
                }
                final PvpClass equipped = this.plugin.getPvpClassManager().getEquippedClass(shooter);
                if (equipped == null || !equipped.equals(this)) {
                    return;
                }       
                if (this.plugin.getPvpClassManager().getEquippedClass(damaged) != null && this.plugin.getPvpClassManager().getEquippedClass(damaged).equals(this)) {
                    return;
                }
                this.plugin.getTimerManager().archerTimer.setCooldown((Player)entity, entity.getUniqueId());
                ArcherClass.tagged.put(damaged.getUniqueId(), shooter.getUniqueId());
                for (final Player player : Bukkit.getOnlinePlayers()) {
                    this.plugin.getScoreboardHandler().getPlayerBoard(player.getUniqueId()).addUpdates(ScoreboardHandler.getOnline());
                }
                if (this.plugin.getTimerManager().archerTimer.getRemaining((Player)entity) == 0L) {
                	shooter.sendMessage(ChatColor.YELLOW + "Tu as touché " + ChatColor.RED + damaged.getName() + ChatColor.YELLOW + ", il est désormais sous archer tag");
                	damaged.sendMessage(ChatColor.YELLOW + "Tu es désormais sous archer tag car tu as été touché par " + ChatColor.RED + shooter.getName());
                }
            }
        }
    }
    
    @Override
    public boolean isApplicableFor(final Player player) {
        final PlayerInventory playerInventory = player.getInventory();
        final ItemStack helmet = playerInventory.getHelmet();
        if (helmet == null || helmet.getType() != Material.LEATHER_HELMET) {
            return false;
        }
        final ItemStack chestplate = playerInventory.getChestplate();
        if (chestplate == null || chestplate.getType() != Material.LEATHER_CHESTPLATE) {
            return false;
        }
        final ItemStack leggings = playerInventory.getLeggings();
        if (leggings == null || leggings.getType() != Material.LEATHER_LEGGINGS) {
            return false;
        }
        final ItemStack boots = playerInventory.getBoots();
        return boots != null && boots.getType() == Material.LEATHER_BOOTS;
    }
    
    static {
        tagged = new HashMap<UUID, UUID>();
        ARCHER_COOLDOWN = new HashMap<UUID, Long>();
        ARCHER_CRITICAL_EFFECT = new PotionEffect(PotionEffectType.WITHER, 60, 0);
        ARCHER_SPEED_EFFECT = new PotionEffect(PotionEffectType.SPEED, 160, 2);
        ARCHER_SPEED_COOLDOWN_DELAY = TimeUnit.MINUTES.toMillis(1L);
    }
}
