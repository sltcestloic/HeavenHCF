package fr.taeron.hcf.combatlog;

import java.util.*;
import org.bukkit.event.player.*;

import fr.taeron.hcf.HCF;

import org.bukkit.entity.*;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.event.*;

public final class NpcManager
{
    private final HCF plugin;
    private final Map<UUID, Npc> spawnedNpcs;
    private final Map<Npc, NpcDespawnTask> despawnTasks;
    private static final Sound EXPLODE_SOUND;
    
    public NpcManager(final HCF plugin) {
        this.spawnedNpcs = new HashMap<UUID, Npc>();
        this.despawnTasks = new HashMap<Npc, NpcDespawnTask>();
        this.plugin = plugin;
    }
    
    public Npc spawn(final Player player) {
        Npc npc = this.getSpawnedNpc(player.getUniqueId());
        if (npc != null) {
            return null;
        }
        npc = new Npc(this.plugin.getNpcPlayerHelper(), this.plugin.getNpcPlayerHelper().spawn(player));
        this.spawnedNpcs.put(player.getUniqueId(), npc);
        final Player entity = npc.getEntity();
        final CraftPlayer p = (CraftPlayer) player;
        entity.setCanPickupItems(false);
        entity.setNoDamageTicks(0);
        entity.setHealthScale(player.getHealthScale());
        entity.setMaxHealth(p.getMaxHealth());
        entity.setHealth(p.getHealth());
        entity.setTotalExperience(player.getTotalExperience());
        entity.setFoodLevel(player.getFoodLevel());
        entity.setExhaustion(player.getExhaustion());
        entity.setSaturation(player.getSaturation());
        entity.setFireTicks(player.getFireTicks());
        entity.getInventory().setContents(player.getInventory().getContents());
        entity.getInventory().setArmorContents(player.getInventory().getArmorContents());
        entity.addPotionEffects(player.getActivePotionEffects());
        entity.teleport((Entity)player, PlayerTeleportEvent.TeleportCause.PLUGIN);
        this.plugin.getNpcPlayerHelper().updateEquipment(entity);
        final Location l = entity.getLocation();
        l.getWorld().playEffect(l, Effect.MOBSPAWNER_FLAMES, 0, 64);
        l.getWorld().playSound(l, NpcManager.EXPLODE_SOUND, 0.9f, 0.0f);
        final long despawnTime = System.currentTimeMillis() + 30000L;
        //TODO Verifier si la task se crée bien
        final NpcDespawnTask despawnTask = new NpcDespawnTask(this.plugin, npc, despawnTime);
        despawnTask.start();
        this.despawnTasks.put(npc, despawnTask);
        return npc;
    }
    
    public void despawn(final Npc npc) {
        this.despawn(npc, NpcDespawnReason.DESPAWN);
    }
    
    public void despawn(final Npc npc, final NpcDespawnReason reason) {
        final Npc other = this.getSpawnedNpc(npc.getIdentity().getId());
        if (other == null || other != npc) {
            return;
        }
        final NpcDespawnEvent event = new NpcDespawnEvent(npc, reason);
        Bukkit.getPluginManager().callEvent((Event)event);
        if (this.hasDespawnTask(npc)) {
            final NpcDespawnTask despawnTask = this.getDespawnTask(npc);
            despawnTask.stop();
            this.despawnTasks.remove(npc);
        }
        this.plugin.getNpcPlayerHelper().despawn(npc.getEntity());
        this.spawnedNpcs.remove(npc.getIdentity().getId());
    }
    
    public Npc getSpawnedNpc(final UUID playerId) {
        return this.spawnedNpcs.get(playerId);
    }
    
    public boolean npcExists(final UUID playerId) {
        return this.spawnedNpcs.containsKey(playerId);
    }
    
    public NpcDespawnTask getDespawnTask(final Npc npc) {
        return this.despawnTasks.get(npc);
    }
    
    public boolean hasDespawnTask(final Npc npc) {
        return this.despawnTasks.containsKey(npc);
    }
    
    static {
        Sound sound;
        try {
            sound = Sound.valueOf("ENTITY_GENERIC_EXPLODE");
        }
        catch (IllegalArgumentException e) {
            try {
                sound = Sound.valueOf("EXPLODE");
            }
            catch (IllegalArgumentException e2) {
                throw new AssertionError((Object)"Unable to find explosion sound");
            }
        }
        EXPLODE_SOUND = sound;
    }


}
