package fr.taeron.hcf.listeners.fixes;

import fr.taeron.hcf.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerItemConsumeEvent;


import org.bukkit.*;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PotionEffectAddEvent;
import org.bukkit.event.entity.PotionEffectAddEvent.EffectCause;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.potion.*;

public class PotionLimitListener implements Listener
{
    
    public int getMaxLevel(final PotionType type) {
        return ConfigurationService.POTION_LIMITS.getOrDefault(type, type.getMaxLevel());
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBrew(BrewEvent event) {
        /*
         * if (!testValidity(event.getContents().getContents())) { event.setCancelled(true); event.getContents().getHolder().setBrewingTime(EMPTY_BREW_TIME); }
         */

        // *** Version that works with a Spigot version that does not
        // *** have a BrewEvent#getResults() method:

        BrewerInventory inventory = event.getContents();
        ItemStack[] contents = inventory.getContents();
        int length = contents.length;
        ItemStack[] cloned = new ItemStack[length];
        for (int i = 0; i < length; i++) {
            ItemStack previous = contents[i];
            cloned[i] = (previous == null ? null : previous.clone());
        }

        BrewingStand stand = inventory.getHolder();
        Bukkit.getScheduler().runTask(HCF.getPlugin(), () -> {
            if (!testValidity(inventory.getContents())) {
                stand.setBrewingTime(400);
                inventory.setContents(cloned);
            }
        });

    }
    
    @EventHandler
    public void onPlayerDamage(final EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            final Player player = (Player)event.getDamager();
            if (player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
                for (final PotionEffect effect : player.getActivePotionEffects()) {
                    if (effect.getType().equals((Object)PotionEffectType.INCREASE_DAMAGE)) {
                        final int level = effect.getAmplifier() + 1;
                        final double newDamage = event.getDamage(EntityDamageEvent.DamageModifier.BASE) / (level * 1.3 + 1.0) + 3 * level;
                        final double damagePercent = newDamage / event.getDamage(EntityDamageEvent.DamageModifier.BASE);
                        try {
                            event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, event.getDamage(EntityDamageEvent.DamageModifier.ARMOR) * damagePercent);
                        }
                        catch (Exception ex) {}
                        try {
                            event.setDamage(EntityDamageEvent.DamageModifier.MAGIC, event.getDamage(EntityDamageEvent.DamageModifier.MAGIC) * damagePercent);
                        }
                        catch (Exception ex2) {}
                        try {
                            event.setDamage(EntityDamageEvent.DamageModifier.RESISTANCE, event.getDamage(EntityDamageEvent.DamageModifier.RESISTANCE) * damagePercent);
                        }
                        catch (Exception ex3) {}
                        try {
                            event.setDamage(EntityDamageEvent.DamageModifier.BLOCKING, event.getDamage(EntityDamageEvent.DamageModifier.BLOCKING) * damagePercent);
                        }
                        catch (Exception ex4) {}
                        event.setDamage(EntityDamageEvent.DamageModifier.BASE, newDamage);
                        break;
                    }
                }
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void fixForce(PotionSplashEvent e){
    	if(e.getPotion().getType().equals(PotionType.STRENGTH)){
    		e.setCancelled(true);
    		
    	}
    	if(e.getPotion().getType().equals(PotionType.POISON)){
    		Potion pot = (Potion) e.getPotion();
    		if(pot.hasExtendedDuration() || pot.getLevel() > 1){
    			pot.setHasExtendedDuration(false);
    			e.setCancelled(true);
    		}
    	}
    }
    
    @EventHandler
    public void test(PotionEffectAddEvent e){
    	if(e.getCause() == EffectCause.SPLASH_POTION && e.getEffect().getDuration() > 680){
    		e.setCancelled(true);
    	}
    }

    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void fixForceDrink(PlayerItemConsumeEvent e){
    	if(e.getPlayer().getItemInHand().getType().equals(Material.POTION) && e.getPlayer().getItemInHand().getData().equals(8201) || e.getPlayer().getItemInHand().getData().equals(8233) || e.getPlayer().getItemInHand().getData().equals(8265)){ 
    		e.setCancelled(true);
    		e.getPlayer().getInventory().remove(e.getPlayer().getItemInHand());
    	}
    }
    
    /*@SuppressWarnings("deprecation")
	@EventHandler
    private static void FinalBukkitInterfaceType(final ProjectileLaunchEvent JSONMessageParserString) {
        if (!(JSONMessageParserString.getEntity() instanceof ThrownPotion) || !(JSONMessageParserString.getEntity().getShooter() instanceof Player)) {
            return;
        }
        final Player ByteArrayHashmapCollector = (Player)JSONMessageParserString.getEntity().getShooter();
        final ThrownPotion InterfaceParameterByteArray = (ThrownPotion)JSONMessageParserString.getEntity();
        if (!ByteArrayHashmapCollector.isDead() && ByteArrayHashmapCollector.isSprinting()) {
            final Iterator<PotionEffect> iterator = InterfaceParameterByteArray.getEffects().iterator();
            while (iterator.hasNext()) {
                if (iterator.next().getType().equals((Object)PotionEffectType.HEAL)) {
                	CraftPlayer cp = (CraftPlayer) ByteArrayHashmapCollector;
                    ByteArrayHashmapCollector.setHealth((cp.getHealth() + 3.0 > cp.getMaxHealth()) ? cp.getMaxHealth() : (cp.getHealth() + 3.0));
                    InterfaceParameterByteArray.setVelocity(InterfaceParameterByteArray.getVelocity().setY(-2));
                }
            }
        }
    }*/
    
    
    private boolean testValidity(ItemStack[] contents) {
        for (ItemStack stack : contents) {
            if (stack != null && stack.getType() == Material.POTION && stack.getDurability() != 0) {
                Potion potion = Potion.fromItemStack(stack);

                // Just to be safe, null check this.
                if (potion == null)
                    continue;

                PotionType type = potion.getType();

                // Mundane potions etc, can return a null type
                if (type == null)
                    continue;

                // is 33s poison, allow
                if (type == PotionType.POISON && !potion.hasExtendedDuration() && potion.getLevel() == 1) {
                    continue;
                }

                if (potion.getLevel() > getMaxLevel(type)) {
                    return false;
                }
            }
        }
        return true;
    }
}
