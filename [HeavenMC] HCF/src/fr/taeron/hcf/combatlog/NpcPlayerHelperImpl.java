package fr.taeron.hcf.combatlog;

import java.lang.reflect.*;
import org.bukkit.entity.*;
import org.bukkit.craftbukkit.v1_7_R4.*;
import org.bukkit.craftbukkit.v1_7_R4.entity.*;
import org.bukkit.*;
import java.io.*;
import net.minecraft.server.v1_7_R4.*;
import net.minecraft.server.v1_7_R4.Entity;
import net.minecraft.server.v1_7_R4.World;

public final class NpcPlayerHelperImpl implements NpcPlayerHelper
{
    private static Method addPlayer;
    private static Method removePlayer;
    
    @Override
    public Player spawn(final Player player) {
        final NpcPlayer npcPlayer = NpcPlayer.valueOf(player);
        final WorldServer worldServer = ((CraftWorld)player.getWorld()).getHandle();
        final Location l = player.getLocation();
        npcPlayer.spawnIn((World)worldServer);
        npcPlayer.setPositionRotation(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
        npcPlayer.playerInteractManager.a(worldServer);
        npcPlayer.invulnerableTicks = 0;
        for (final Object o : MinecraftServer.getServer().getPlayerList().players) {
            if (o instanceof EntityPlayer) {
                if (o instanceof NpcPlayer) {
                    continue;
                }
                if (NpcPlayerHelperImpl.addPlayer == null) {
                    break;
                }
                try {
                    ((EntityPlayer)o).playerConnection.sendPacket((Packet)NpcPlayerHelperImpl.addPlayer.invoke(null, npcPlayer));
                }
                catch (Exception ex) {}
            }
        }
        worldServer.addEntity((Entity)npcPlayer);
        worldServer.getPlayerChunkMap().addPlayer((EntityPlayer)npcPlayer);
        return (Player)npcPlayer.getBukkitEntity();
    }
    
    @Override
    public void despawn(final Player player) {
        final EntityPlayer entity = ((CraftPlayer)player).getHandle();
        if (!(entity instanceof NpcPlayer)) {
            throw new IllegalArgumentException();
        }
        for (final Object o : MinecraftServer.getServer().getPlayerList().players) {
            if (o instanceof EntityPlayer) {
                if (o instanceof NpcPlayer) {
                    continue;
                }
                if (NpcPlayerHelperImpl.addPlayer == null) {
                    break;
                }
                try {
                    ((EntityPlayer)o).playerConnection.sendPacket((Packet)NpcPlayerHelperImpl.removePlayer.invoke(null, entity));
                }
                catch (Exception ex) {}
            }
        }
        final WorldServer worldServer = MinecraftServer.getServer().getWorldServer(entity.dimension);
        worldServer.removeEntity((Entity)entity);
        worldServer.getPlayerChunkMap().removePlayer(entity);
    }
    
    @Override
    public boolean isNpc(final Player player) {
        return ((CraftPlayer)player).getHandle() instanceof NpcPlayer;
    }
    
    @Override
    public NpcIdentity getIdentity(final Player player) {
        if (!this.isNpc(player)) {
            throw new IllegalArgumentException();
        }
        return ((NpcPlayer)((CraftPlayer)player).getHandle()).getNpcIdentity();
    }
    
    @Override
    public void updateEquipment(final Player player) {
        final EntityPlayer entity = ((CraftPlayer)player).getHandle();
        if (!(entity instanceof NpcPlayer)) {
            throw new IllegalArgumentException();
        }
        final Location l = player.getLocation();
        final int rangeSquared = 262144;
        for (int i = 0; i < 5; ++i) {
            final ItemStack item = entity.getEquipment(i);
            if (item != null) {
                final Packet packet = (Packet)new PacketPlayOutEntityEquipment(entity.getId(), i, item);
                for (final Object o : entity.world.players) {
                    if (!(o instanceof EntityPlayer)) {
                        continue;
                    }
                    final EntityPlayer p = (EntityPlayer)o;
                    final Location loc = p.getBukkitEntity().getLocation();
                    if (!l.getWorld().equals(loc.getWorld()) || l.distanceSquared(loc) > rangeSquared) {
                        continue;
                    }
                    p.playerConnection.sendPacket(packet);
                }
            }
        }
    }
    
    @Override
    public void syncOffline(final Player player) {
        final EntityPlayer entity = ((CraftPlayer)player).getHandle();
        if (!(entity instanceof NpcPlayer)) {
            throw new IllegalArgumentException();
        }
        final NpcPlayer npcPlayer = (NpcPlayer)entity;
        final NpcIdentity identity = npcPlayer.getNpcIdentity();
        final Player p = Bukkit.getPlayer(identity.getId());
        if (p != null && p.isOnline()) {
            return;
        }
        final WorldNBTStorage worldStorage = (WorldNBTStorage)((CraftWorld) Bukkit.getWorlds().get(0)).getHandle().getDataManager();
        final NBTTagCompound playerNbt = worldStorage.getPlayerData(identity.getId().toString());
        if (playerNbt == null) {
            return;
        }
        playerNbt.setShort("Air", (short)entity.getAirTicks());
        playerNbt.setFloat("HealF", entity.getHealth());
        playerNbt.setShort("Health", (short)Math.ceil(entity.getHealth()));
        playerNbt.setFloat("AbsorptionAmount", entity.getAbsorptionHearts());
        playerNbt.setInt("XpTotal", entity.expTotal);
        playerNbt.setInt("foodLevel", entity.getFoodData().foodLevel);
        playerNbt.setInt("foodTickTimer", entity.getFoodData().foodTickTimer);
        playerNbt.setFloat("foodSaturationLevel", entity.getFoodData().saturationLevel);
        playerNbt.setFloat("foodExhaustionLevel", entity.getFoodData().exhaustionLevel);
        playerNbt.setShort("Fire", (short)entity.fireTicks);
        playerNbt.set("Inventory", (NBTBase)npcPlayer.inventory.a(new NBTTagList()));
        final File file1 = new File(worldStorage.getPlayerDir(), identity.getId().toString() + ".dat.tmp");
        final File file2 = new File(worldStorage.getPlayerDir(), identity.getId().toString() + ".dat");
        try {
            NBTCompressedStreamTools.a(playerNbt, (OutputStream)new FileOutputStream(file1));
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to save player data for " + identity.getName(), e);
        }
        if ((!file2.exists() || file2.delete()) && !file1.renameTo(file2)) {
            throw new RuntimeException("Failed to save player data for " + identity.getName());
        }
    }
    
    @Override
    public void createPlayerList(final Player player) {
        if (NpcPlayerHelperImpl.addPlayer == null) {
            return;
        }
        final EntityPlayer p = ((CraftPlayer)player).getHandle();
        for (final WorldServer worldServer : MinecraftServer.getServer().worlds) {
            for (final Object o : worldServer.players) {
                if (!(o instanceof NpcPlayer)) {
                    continue;
                }
                final NpcPlayer npcPlayer = (NpcPlayer)o;
                try {
                    p.playerConnection.sendPacket((Packet)NpcPlayerHelperImpl.addPlayer.invoke(null, npcPlayer));
                }
                catch (Exception ex) {}
            }
        }
    }
    
    @Override
    public void removePlayerList(final Player player) {
        if (NpcPlayerHelperImpl.addPlayer == null) {
            return;
        }
        final EntityPlayer p = ((CraftPlayer)player).getHandle();
        for (final WorldServer worldServer : MinecraftServer.getServer().worlds) {
            for (final Object o : worldServer.players) {
                if (!(o instanceof NpcPlayer)) {
                    continue;
                }
                final NpcPlayer npcPlayer = (NpcPlayer)o;
                try {
                    p.playerConnection.sendPacket((Packet)NpcPlayerHelperImpl.removePlayer.invoke(null, npcPlayer));
                }
                catch (Exception ex) {}
            }
        }
    }
    
    static {
        try {
            NpcPlayerHelperImpl.addPlayer = PacketPlayOutPlayerInfo.class.getMethod("addPlayer", EntityPlayer.class);
            NpcPlayerHelperImpl.removePlayer = PacketPlayOutPlayerInfo.class.getMethod("removePlayer", EntityPlayer.class);
        }
        catch (NoSuchMethodException ex) {}
    }
}
