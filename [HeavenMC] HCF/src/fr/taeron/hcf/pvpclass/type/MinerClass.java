package fr.taeron.hcf.pvpclass.type;

import fr.taeron.hcf.pvpclass.*;
import fr.taeron.hcf.*;
import java.util.concurrent.*;
import org.bukkit.potion.*;
import org.heavenmc.core.util.BukkitUtils;
import org.bukkit.event.entity.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import fr.taeron.hcf.pvpclass.event.*;
import org.bukkit.*;
import org.bukkit.inventory.*;

public class MinerClass extends PvpClass implements Listener{
	
    @SuppressWarnings("unused")
	private static final int INVISIBILITY_HEIGHT_LEVEL = 30;
    private static final PotionEffect HEIGHT_INVISIBILITY;
    private final HCF plugin;
    
    public MinerClass(final HCF plugin) {
        super("Mineur", TimeUnit.SECONDS.toMillis(3L));
        this.plugin = plugin;
        this.passiveEffects.add(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 1));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));
    }
    
    private void removeInvisibilitySafely(final Player player) {
        for (final PotionEffect active : player.getActivePotionEffects()) {
            if (active.getType().equals((Object)PotionEffectType.INVISIBILITY) && active.getDuration() > MinerClass.DEFAULT_MAX_DURATION) {
            	player.sendMessage("§eInvisibilité de la classe §dMineur §edésactivé.");
                player.removePotionEffect(active.getType());
                break;
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDamage(final EntityDamageByEntityEvent event) {
        final Entity entity = event.getEntity();
        if (entity instanceof Player && BukkitUtils.getFinalAttacker((EntityDamageEvent)event, false) != null) {
            final Player player = (Player)entity;
            if (this.plugin.getPvpClassManager().hasClassEquipped(player, this)) {
                this.removeInvisibilitySafely(player);
            }
        }
    }
    
    @Override
    public void onUnequip(final Player player) {
        super.onUnequip(player);
        this.removeInvisibilitySafely(player);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerMove(final PlayerMoveEvent event) {
        this.conformMinerInvisibility(event.getPlayer(), event.getFrom(), event.getTo());
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
        this.conformMinerInvisibility(event.getPlayer(), event.getFrom(), event.getTo());
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onClassEquip(final PvpClassEquipEvent event) {
        final Player player = event.getPlayer();
        if (event.getPvpClass().equals(this)) {
        	if(player.getLocation().getX() >= 30){
	            player.sendMessage("§eEffets de la classe §dMineur §eactivés.");
        	} else {
        		player.addPotionEffect(MinerClass.HEIGHT_INVISIBILITY, true);
        		player.sendMessage("§eEffets de la classe §dMineur §eet invisibilité activés.");
        		
        	}
        }
    }
    
    private void conformMinerInvisibility(final Player player, final Location from, final Location to) {
        final int fromY = from.getBlockY();
        final int toY = to.getBlockY();
        if (fromY != toY && this.plugin.getPvpClassManager().hasClassEquipped(player, this)) {
            final boolean isInvisible = player.hasPotionEffect(PotionEffectType.INVISIBILITY);
            if (toY > 30) {
                if (fromY <= 30 && isInvisible) {
                    this.removeInvisibilitySafely(player);
                }
            }
            else if (!isInvisible) {
                player.addPotionEffect(MinerClass.HEIGHT_INVISIBILITY, true);
                player.sendMessage("§eInvisibilité de la classe §dMineur §eactivée.");
            }
        }
    }
    
    @Override
    public boolean isApplicableFor(final Player player) {
        final PlayerInventory playerInventory = player.getInventory();
        final ItemStack helmet = playerInventory.getHelmet();
        if (helmet == null || helmet.getType() != Material.IRON_HELMET || !helmet.getEnchantments().isEmpty()) {
            return false;
        }
        final ItemStack chestplate = playerInventory.getChestplate();
        if (chestplate == null || chestplate.getType() != Material.IRON_CHESTPLATE || !chestplate.getEnchantments().isEmpty()) {
            return false;
        }
        final ItemStack leggings = playerInventory.getLeggings();
        if (leggings == null || leggings.getType() != Material.IRON_LEGGINGS || !leggings.getEnchantments().isEmpty()) {
            return false;
        }
        final ItemStack boots = playerInventory.getBoots();
        return boots != null && boots.getType() == Material.IRON_BOOTS && boots.getEnchantments().isEmpty();
    }
    
    static {
        HEIGHT_INVISIBILITY = new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0);
    }
}
