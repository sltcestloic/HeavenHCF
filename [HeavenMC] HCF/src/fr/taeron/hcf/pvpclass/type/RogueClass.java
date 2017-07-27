package fr.taeron.hcf.pvpclass.type;

import fr.taeron.hcf.pvpclass.*;
import java.util.concurrent.*;
import org.bukkit.potion.*;
import org.bukkit.event.entity.*;
import fr.taeron.hcf.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.inventory.*;

public class RogueClass extends PvpClass implements Listener
{
    private final HCF plugin;
    
    public RogueClass(final HCF plugin) {
        super("Rogue", TimeUnit.SECONDS.toMillis(1L));
        this.plugin = plugin;
        this.passiveEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
        final Entity entity = event.getEntity();
        final Entity damager = event.getDamager();
        if (entity instanceof Player && damager instanceof Player) {
            final Player attacker = (Player)damager;
            final PvpClass equipped = this.plugin.getPvpClassManager().getEquippedClass(attacker);
            if (equipped != null && equipped.equals(this)) {
                final ItemStack stack = attacker.getItemInHand();
                if (stack != null && stack.getType() == Material.GOLD_SWORD && stack.getEnchantments().isEmpty()) {
                    final Player player = (Player)entity;
                    player.sendMessage(ConfigurationService.ENEMY_COLOUR + attacker.getName() + ChatColor.YELLOW + " has backstabbed you.");
                    player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1.0f, 1.0f);
                    attacker.sendMessage(ChatColor.YELLOW + "You have backstabbed " + ConfigurationService.ENEMY_COLOUR + player.getName() + ChatColor.YELLOW + '.');
                    attacker.setItemInHand(new ItemStack(Material.AIR, 1));
                    attacker.playSound(player.getLocation(), Sound.ITEM_BREAK, 1.0f, 1.0f);
                    event.setDamage(3.0);
                }
            }
        }
    }
    
    @Override
    public boolean isApplicableFor(final Player player) {
        final PlayerInventory playerInventory = player.getInventory();
        final ItemStack helmet = playerInventory.getHelmet();
        if (helmet == null || helmet.getType() != Material.CHAINMAIL_HELMET) {
            return false;
        }
        final ItemStack chestplate = playerInventory.getChestplate();
        if (chestplate == null || chestplate.getType() != Material.CHAINMAIL_CHESTPLATE) {
            return false;
        }
        final ItemStack leggings = playerInventory.getLeggings();
        if (leggings == null || leggings.getType() != Material.CHAINMAIL_LEGGINGS) {
            return false;
        }
        final ItemStack boots = playerInventory.getBoots();
        return boots != null && boots.getType() == Material.CHAINMAIL_BOOTS;
    }
}
