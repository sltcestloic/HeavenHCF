package fr.taeron.hcf.listeners;

import org.bukkit.inventory.ItemStack;
import org.heavenmc.core.util.JavaUtils;
import org.bukkit.event.entity.*;
import org.bukkit.entity.*;

import fr.taeron.hcf.user.*;
import org.bukkit.event.*;
import java.util.concurrent.*;
import fr.taeron.hcf.*;
import org.bukkit.craftbukkit.v1_7_R4.*;
import org.bukkit.craftbukkit.v1_7_R4.entity.*;
import fr.taeron.hcf.faction.type.*;
import fr.taeron.hcf.faction.struct.*;
import org.bukkit.*;
import net.minecraft.server.v1_7_R4.*;
import net.minecraft.server.v1_7_R4.Entity;
import net.minecraft.server.v1_7_R4.World;

import java.util.*;

public class DeathListener implements Listener{
	
    public static final long BASE_REGEN_DELAY;
    public static HashMap<UUID, ItemStack[]> PlayerInventoryContents;
    public static HashMap<UUID, ItemStack[]> PlayerArmorContents;
    private final HCF plugin;
    
    public DeathListener(final HCF plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerDeathKillIncrement(final PlayerDeathEvent event) {
        final Player killer = event.getEntity().getKiller();
        if (killer != null) {
            final FactionUser user = this.plugin.getUserManager().getUser(killer.getUniqueId());
            user.setKills(user.getKills() + 1);
            int money = 100;
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco " + killer.getName() + " give " + money);
            killer.sendMessage("§aTu as gagné §e§l$" + money + " §apour avoir tué §e" + event.getEntity().getName());
        }
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId());
        if (playerFaction != null) {
            final Faction factionAt = this.plugin.getFactionManager().getFactionAt(player.getLocation());
            final Role role = playerFaction.getMember(player.getUniqueId()).getRole();
            if (playerFaction.getDeathsUntilRaidable() >= -5.0) {
                playerFaction.setDeathsUntilRaidable(playerFaction.getDeathsUntilRaidable() - factionAt.getDtrLossMultiplier());
                playerFaction.setRemainingRegenerationTime(DeathListener.BASE_REGEN_DELAY + playerFaction.getOnlinePlayers().size() * TimeUnit.MINUTES.toMillis(2L));
                playerFaction.broadcast(ChatColor.YELLOW + "Membre mort: " + ConfigurationService.TEAMMATE_COLOUR + role.getAstrix() + player.getName() + ChatColor.YELLOW + ". DTR:" + ChatColor.GRAY + " [" + playerFaction.getDtrColour() + JavaUtils.format((Number)playerFaction.getDeathsUntilRaidable()) + ChatColor.WHITE + '/' + ChatColor.WHITE + playerFaction.getMaximumDeathsUntilRaidable() + ChatColor.GRAY + "].");
            }
            else {
                playerFaction.setRemainingRegenerationTime(DeathListener.BASE_REGEN_DELAY + playerFaction.getOnlinePlayers().size() * TimeUnit.MINUTES.toMillis(2L));
                playerFaction.broadcast(ChatColor.YELLOW + "Membre mort: " + ConfigurationService.TEAMMATE_COLOUR + role.getAstrix() + player.getName() + ChatColor.YELLOW + ". DTR:" + ChatColor.GRAY + " [" + playerFaction.getDtrColour() + JavaUtils.format((Number)playerFaction.getDeathsUntilRaidable()) + ChatColor.WHITE + '/' + ChatColor.WHITE + playerFaction.getMaximumDeathsUntilRaidable() + ChatColor.GRAY + "].");
            }
        }
        if (Bukkit.spigot().getTPS()[0] > 18.0) {
            DeathListener.PlayerInventoryContents.put(player.getUniqueId(), player.getInventory().getContents());
            DeathListener.PlayerArmorContents.put(player.getUniqueId(), player.getInventory().getArmorContents());
            final Location location = player.getLocation();
            final WorldServer worldServer = ((CraftWorld)location.getWorld()).getHandle();
            final EntityLightning entityLightning = new EntityLightning((World)worldServer, location.getX(), location.getY(), location.getZ(), false);
            final PacketPlayOutSpawnEntityWeather packet = new PacketPlayOutSpawnEntityWeather((Entity)entityLightning);
            for (final Player target : Bukkit.getOnlinePlayers()) {
                if (this.plugin.getUserManager().getUser(target.getUniqueId()).isShowLightning()) {
                    ((CraftPlayer)target).getHandle().playerConnection.sendPacket((Packet)packet);
                    target.playSound(target.getLocation(), Sound.AMBIENCE_THUNDER, 1.0f, 1.0f);
                }
            }
        }
    }
    
    static {
        DeathListener.PlayerInventoryContents = new HashMap<UUID, ItemStack[]>();
        DeathListener.PlayerArmorContents = new HashMap<UUID, ItemStack[]>();
        BASE_REGEN_DELAY = TimeUnit.MINUTES.toMillis(40L);
    }
}
