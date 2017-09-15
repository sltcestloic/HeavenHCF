package fr.taeron.hcf.listeners;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.Bukkit;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;

import fr.taeron.hcf.command.SetKnockbackCommand;


public class KnockbackListener implements Listener{
	
	private Field fieldPlayerConnection;
	private Method sendPacket;
	private Constructor<?> packetVelocity;
	  
	  public KnockbackListener()
	  {
	    try
	    {
	      Class<?> entityPlayerClass = Class.forName("net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + ".EntityPlayer");
	      Class<?> packetVelocityClass = Class.forName("net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + ".PacketPlayOutEntityVelocity");
	      Class<?> playerConnectionClass = Class.forName("net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + ".PlayerConnection");
	      
	      this.fieldPlayerConnection = entityPlayerClass.getField("playerConnection");
	      this.sendPacket = playerConnectionClass.getMethod("sendPacket", new Class[] { packetVelocityClass.getSuperclass() });
	      this.packetVelocity = packetVelocityClass.getConstructor(new Class[] { Integer.TYPE, Double.TYPE, Double.TYPE, Double.TYPE });
	    }
	    catch (ClassNotFoundException|NoSuchFieldException|SecurityException|NoSuchMethodException e)
	    {
	      e.printStackTrace();
	    }
	  }
	  
	  @EventHandler
	  public void onPlayerVelocity(PlayerVelocityEvent event) throws IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, SecurityException
	  {
	    Player player = event.getPlayer();
		    EntityDamageEvent lastDamage = player.getLastDamageCause();
		    if ((lastDamage == null) || (!(lastDamage instanceof EntityDamageByEntityEvent))) {
		      return;
		    }
		    if(player.getMaximumNoDamageTicks() == 4){
		    	event.setCancelled(true);
		    	return;
		    }
		    if ((((EntityDamageByEntityEvent)lastDamage).getDamager() instanceof Player)) {
		    	EntityDamageByEntityEvent test = (EntityDamageByEntityEvent) lastDamage;
		    	event.setVelocity(this.VelocityCalculator((Player) test.getDamager(), player));
		    }
		    return; 
	  }
	  
	  @EventHandler(priority=EventPriority.HIGHEST)
	  public void onEntityDamageByEntity(EntityDamageByEntityEvent event)
	  {
	    if ((!(event.getEntity() instanceof Player))) {
	      return;
	    }
	    if (event.isCancelled()) {
	      return;
	    }
	    if(!(event.getDamager() instanceof Player)){
	    	return;
	    }
	    Player damaged = (Player)event.getEntity();
	    Player damager = (Player)event.getDamager();
	    if (damaged.getNoDamageTicks() > damaged.getMaximumNoDamageTicks() / 2.0D) {
	      return;
	    }
	    if (damaged.getMaximumNoDamageTicks() == 4){
	    	double horMultiplier = 0.77;
		    double verMultiplier = 0.70;
		    double sprintMultiplier = damager.isSprinting() ? 0.8D : 0.5D;
		    double kbMultiplier = damager.getItemInHand() == null ? 0.0D : damager.getItemInHand().getEnchantmentLevel(Enchantment.KNOCKBACK) * 0.2D;
		    
		    
		    Vector knockback = damaged.getLocation().toVector().subtract(damager.getLocation().toVector()).normalize();
		    knockback.setX((knockback.getX() * sprintMultiplier + kbMultiplier) * horMultiplier);
		    if(damaged.getLocation().getBlockY() - damager.getLocation().getBlockY() < 1){
		    	knockback.setY(0.35D * verMultiplier);
		    } else if(damaged.getLocation().getBlockY() - damager.getLocation().getBlockY() < 1.8){
		    	knockback.setY(0.35D * verMultiplier / 2);
		    } else {
		    	knockback.setY(0.35D * verMultiplier / 5);
		    }
		    knockback.setZ((knockback.getZ() * sprintMultiplier + kbMultiplier) * horMultiplier);
		    try {
		      Object entityPlayer = damaged.getClass().getMethod("getHandle", new Class[0]).invoke(damaged, new Object[0]);
		      Object playerConnection = this.fieldPlayerConnection.get(entityPlayer);
		      Object packet = this.packetVelocity.newInstance(new Object[] { Integer.valueOf(damaged.getEntityId()), Double.valueOf(knockback.getX()), Double.valueOf(knockback.getY()), Double.valueOf(knockback.getZ()) });
		      this.sendPacket.invoke(playerConnection, new Object[] { packet });
		    } catch (SecurityException|IllegalArgumentException|IllegalAccessException|InvocationTargetException|NoSuchMethodException|InstantiationException e) {
		      e.printStackTrace();
		    }	    
	    }
	}

	public Vector VelocityCalculator(Player damager, Player damaged){
		  if (damaged.getMaximumNoDamageTicks() == 4){
		    	double horMultiplier = 0.80;
			    double verMultiplier = 0.70;
			    double sprintMultiplier = damager.isSprinting() ? 0.8D : 0.5D;
			    double kbMultiplier = damager.getItemInHand() == null ? 0.0D : damager.getItemInHand().getEnchantmentLevel(Enchantment.KNOCKBACK) * 0.2D;
			    
			    
			    Vector knockback = damaged.getLocation().toVector().subtract(damager.getLocation().toVector()).normalize();
			    knockback.setX((knockback.getX() * sprintMultiplier + kbMultiplier) * horMultiplier);
			    if(damaged.getLocation().getBlockY() - damager.getLocation().getBlockY() < 1){
			    	knockback.setY(0.35D * verMultiplier);
			    } else if(damaged.getLocation().getBlockY() - damager.getLocation().getBlockY() < 1.8){
			    	knockback.setY(0.35D * verMultiplier / 2);
			    } else {
			    	knockback.setY(0.35D * verMultiplier / 10);
			    	knockback.setZ(knockback.getZ() + 0.1);
			    	knockback.setX(knockback.getZ() + 0.1);
			    }
			    knockback.setZ((knockback.getZ() * sprintMultiplier + kbMultiplier) * horMultiplier);
			    return knockback; 
		    } else {
		    	double horMultiplier = SetKnockbackCommand.xz;
			    double verMultiplier = SetKnockbackCommand.y;
			    double sprintMultiplier = damager.isSprinting() ? 0.8D : 0.5D;
			    Vector knockback = damaged.getLocation().toVector().subtract(damager.getLocation().toVector()).normalize();
			    knockback.setX((knockback.getX() * sprintMultiplier) * horMultiplier);
			    knockback.setY(0.35D * verMultiplier);
			    knockback.setZ((knockback.getZ() * sprintMultiplier) * horMultiplier);
			    if(damager.getItemInHand().getEnchantmentLevel(Enchantment.KNOCKBACK) > 0){
			    	knockback.setX(knockback.getX() * damager.getItemInHand().getEnchantmentLevel(Enchantment.KNOCKBACK));
			    	knockback.setZ(knockback.getZ() * damager.getItemInHand().getEnchantmentLevel(Enchantment.KNOCKBACK));
			    }
			    	return knockback;
		    	}
		  }
	}
